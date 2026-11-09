package com.cellguard.app.data.repository

import com.cellguard.app.core.database.dao.ChargingSessionDao
import com.cellguard.app.core.database.dao.HealthEstimateDao
import com.cellguard.app.core.database.entity.ChargingSessionEntity
import com.cellguard.app.core.database.entity.HealthEstimateEntity
import com.cellguard.app.data.battery.BatteryHardwareDataSource
import com.cellguard.app.data.preferences.UserPreferencesRepository
import com.cellguard.app.domain.estimator.BatteryHealthEstimator
import com.cellguard.app.domain.insights.SmartInsightEngine
import com.cellguard.app.domain.model.BatteryHealthStatus
import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.ChargingSession
import com.cellguard.app.domain.model.ChargingSource
import com.cellguard.app.domain.model.HardwareCapabilities
import com.cellguard.app.domain.model.HealthConfidence
import com.cellguard.app.domain.model.HealthEstimate
import com.cellguard.app.domain.model.MetricSource
import com.cellguard.app.domain.model.SmartInsight
import com.cellguard.app.domain.repository.BatteryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BatteryRepositoryImpl(
    private val hardwareDataSource: BatteryHardwareDataSource,
    private val chargingSessionDao: ChargingSessionDao,
    private val healthEstimateDao: HealthEstimateDao,
    private val preferencesRepository: UserPreferencesRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : BatteryRepository {

    private val sessionMutex = Mutex()
    private var activeSessionBuilder: ActiveSessionTracker? = null

    private data class ActiveSessionTracker(
        val startTime: Long,
        val startPercentage: Int,
        val startChargeCounterUah: Long?,
        val source: ChargingSource,
        var maxTempC: Float,
        val tempSamples: MutableList<Float> = mutableListOf(),
        val voltageSamples: MutableList<Int> = mutableListOf(),
        val currentSamples: MutableList<Float> = mutableListOf()
    )

    override fun observeBatteryMetrics(): Flow<BatteryMetrics> =
        hardwareDataSource.observeBatteryMetrics()

    override fun getCurrentMetrics(): BatteryMetrics =
        hardwareDataSource.getCurrentMetrics()

    override fun getHardwareCapabilities(): HardwareCapabilities =
        hardwareDataSource.getHardwareCapabilities()

    override fun observeHealthEstimate(): Flow<HealthEstimate?> =
        healthEstimateDao.getLatestEstimate().map { entity ->
            entity?.toDomain()
        }

    override fun observeRecentSessions(): Flow<List<ChargingSession>> =
        chargingSessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeSmartInsights(): Flow<List<SmartInsight>> =
        combine(
            observeBatteryMetrics(),
            observeHealthEstimate(),
            observeRecentSessions()
        ) { metrics, health, sessions ->
            SmartInsightEngine.generateInsights(metrics, health, sessions)
        }

    override suspend fun recordMetricsUpdate(metrics: BatteryMetrics) {
        sessionMutex.withLock {
            val wasCharging = activeSessionBuilder != null
            val isNowCharging = metrics.isCharging

            if (!wasCharging && isNowCharging) {
                // Charger connected -> Start new session
                activeSessionBuilder = ActiveSessionTracker(
                    startTime = metrics.timestamp,
                    startPercentage = metrics.level,
                    startChargeCounterUah = metrics.chargeCounterUah,
                    source = metrics.source,
                    maxTempC = metrics.temperatureCelsius,
                    tempSamples = mutableListOf(metrics.temperatureCelsius),
                    voltageSamples = mutableListOf(metrics.voltageMv),
                    currentSamples = metrics.normalizedCurrentMa()?.let { mutableListOf(it) } ?: mutableListOf()
                )
            } else if (wasCharging && isNowCharging) {
                // Ongoing charging -> sample parameters
                activeSessionBuilder?.let { tracker ->
                    if (metrics.temperatureCelsius > tracker.maxTempC) {
                        tracker.maxTempC = metrics.temperatureCelsius
                    }
                    tracker.tempSamples.add(metrics.temperatureCelsius)
                    tracker.voltageSamples.add(metrics.voltageMv)
                    metrics.normalizedCurrentMa()?.let { tracker.currentSamples.add(it) }
                }
            } else if (wasCharging && !isNowCharging) {
                // Charger disconnected -> finalize session
                val tracker = activeSessionBuilder
                activeSessionBuilder = null

                if (tracker != null) {
                    val endPct = metrics.level
                    val deltaPct = endPct - tracker.startPercentage

                    // Record only if charge session was at least 2 minutes or gained at least 1%
                    val durationMs = metrics.timestamp - tracker.startTime
                    if (deltaPct >= 1 || durationMs >= 120_000) {
                        val avgTemp = if (tracker.tempSamples.isNotEmpty()) tracker.tempSamples.average().toFloat() else metrics.temperatureCelsius
                        val avgVolt = if (tracker.voltageSamples.isNotEmpty()) tracker.voltageSamples.average().toInt() else metrics.voltageMv
                        val avgCur = if (tracker.currentSamples.isNotEmpty()) tracker.currentSamples.average().toFloat() else null

                        val preliminarySession = ChargingSession(
                            startTime = tracker.startTime,
                            endTime = metrics.timestamp,
                            startPercentage = tracker.startPercentage,
                            endPercentage = endPct,
                            startChargeCounterUah = tracker.startChargeCounterUah,
                            endChargeCounterUah = metrics.chargeCounterUah,
                            startTemperatureC = tracker.tempSamples.firstOrNull() ?: metrics.temperatureCelsius,
                            maxTemperatureC = tracker.maxTempC,
                            avgTemperatureC = avgTemp,
                            avgVoltageMv = avgVolt,
                            avgCurrentMa = avgCur,
                            source = tracker.source,
                            isCompleted = true
                        )

                        val capacityEst = BatteryHealthEstimator.estimateSessionCapacityMah(preliminarySession)
                        val entity = ChargingSessionEntity(
                            startTime = preliminarySession.startTime,
                            endTime = preliminarySession.endTime,
                            startPercentage = preliminarySession.startPercentage,
                            endPercentage = preliminarySession.endPercentage,
                            startChargeCounterUah = preliminarySession.startChargeCounterUah,
                            endChargeCounterUah = preliminarySession.endChargeCounterUah,
                            startTemperatureC = preliminarySession.startTemperatureC,
                            maxTemperatureC = preliminarySession.maxTemperatureC,
                            avgTemperatureC = preliminarySession.avgTemperatureC,
                            avgVoltageMv = preliminarySession.avgVoltageMv,
                            avgCurrentMa = preliminarySession.avgCurrentMa,
                            sourceName = preliminarySession.source.name,
                            isCompleted = true,
                            estimatedCapacityContribution = capacityEst
                        )
                        chargingSessionDao.insertSession(entity)

                        // Recalculate health
                        recalculateHealthEstimate()
                    }
                }
            }
        }
    }

    override suspend fun recalculateHealthEstimate() {
        val sessions = chargingSessionDao.getAllSessions().first().map { it.toDomain() }
        val settings = preferencesRepository.userSettings.first()
        val currentMetrics = hardwareDataSource.getCurrentMetrics()

        val verifiedDesignCapacity = settings.customDesignCapacityMah ?: currentMetrics.designCapacityMah
        val hardwareCycleCount = currentMetrics.hardwareCycleCount

        val estimate = BatteryHealthEstimator.computeHealthEstimate(
            sessions = sessions,
            verifiedDesignCapacityMah = verifiedDesignCapacity,
            hardwareCycleCount = hardwareCycleCount
        )

        val entity = HealthEstimateEntity(
            timestamp = estimate.lastCalculatedTimestamp,
            estimatedFullCapacityMah = estimate.estimatedFullCapacityMah,
            verifiedDesignCapacityMah = estimate.verifiedDesignCapacityMah,
            healthPercentage = estimate.healthPercentage,
            conditionName = estimate.condition.name,
            confidenceName = estimate.confidence.name,
            cycleCount = estimate.cycleCount,
            estimatedLossPct = estimate.estimatedCapacityLossPct,
            validSessionsCount = estimate.validSessionsCount
        )
        healthEstimateDao.insertEstimate(entity)
    }

    private fun ChargingSessionEntity.toDomain(): ChargingSession = ChargingSession(
        id = id,
        startTime = startTime,
        endTime = endTime,
        startPercentage = startPercentage,
        endPercentage = endPercentage,
        startChargeCounterUah = startChargeCounterUah,
        endChargeCounterUah = endChargeCounterUah,
        startTemperatureC = startTemperatureC,
        maxTemperatureC = maxTemperatureC,
        avgTemperatureC = avgTemperatureC,
        avgVoltageMv = avgVoltageMv,
        avgCurrentMa = avgCurrentMa,
        source = try { ChargingSource.valueOf(sourceName) } catch (_: Throwable) { ChargingSource.UNKNOWN },
        isCompleted = isCompleted,
        estimatedCapacityContribution = estimatedCapacityContribution
    )

    private fun HealthEstimateEntity.toDomain(): HealthEstimate = HealthEstimate(
        estimatedFullCapacityMah = estimatedFullCapacityMah,
        verifiedDesignCapacityMah = verifiedDesignCapacityMah,
        healthPercentage = healthPercentage,
        condition = try { BatteryHealthStatus.valueOf(conditionName) } catch (_: Throwable) { BatteryHealthStatus.INSUFFICIENT_DATA },
        confidence = try { HealthConfidence.valueOf(confidenceName) } catch (_: Throwable) { HealthConfidence.NOT_ENOUGH_DATA },
        confidenceReason = when (confidenceName) {
            HealthConfidence.HIGH.name -> "High confidence calculation."
            HealthConfidence.MEDIUM.name -> "Moderate confidence based on recorded sessions."
            HealthConfidence.LOW.name -> "Preliminary estimation."
            else -> "Collecting data."
        },
        cycleCount = cycleCount,
        cycleCountSource = if (cycleCount != null) MetricSource.SYSTEM_REPORTED else MetricSource.UNSUPPORTED,
        estimatedCapacityLossPct = estimatedLossPct,
        validSessionsCount = validSessionsCount,
        lastCalculatedTimestamp = timestamp
    )
}
