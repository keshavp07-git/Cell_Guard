package com.cellguard.app.data.repository

import com.cellguard.app.core.database.dao.BatterySnapshotDao
import com.cellguard.app.core.database.dao.ChargingSessionDao
import com.cellguard.app.core.database.dao.HealthEstimateDao
import com.cellguard.app.core.database.entity.BatterySnapshotEntity
import com.cellguard.app.core.database.entity.ChargingSessionEntity
import com.cellguard.app.domain.model.BatterySnapshot
import com.cellguard.app.domain.model.ChargingSession
import com.cellguard.app.domain.model.ChargingSource
import com.cellguard.app.domain.repository.HistoryRepository
import com.cellguard.app.domain.repository.HistoryTimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryRepositoryImpl(
    private val batterySnapshotDao: BatterySnapshotDao,
    private val chargingSessionDao: ChargingSessionDao,
    private val healthEstimateDao: HealthEstimateDao
) : HistoryRepository {

    override fun getSnapshotsForRange(range: HistoryTimeRange): Flow<List<BatterySnapshot>> {
        val now = System.currentTimeMillis()
        val since = when (range) {
            HistoryTimeRange.HOURS_24 -> now - (24 * 3600 * 1000L)
            HistoryTimeRange.DAYS_7 -> now - (7 * 24 * 3600 * 1000L)
            HistoryTimeRange.DAYS_30 -> now - (30 * 24 * 3600 * 1000L)
            HistoryTimeRange.ALL -> 0L
        }
        return batterySnapshotDao.getSnapshotsSince(since).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllSessions(): Flow<List<ChargingSession>> =
        chargingSessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun recordSnapshot(snapshot: BatterySnapshot) {
        val entity = BatterySnapshotEntity(
            timestamp = snapshot.timestamp,
            percentage = snapshot.percentage,
            temperatureCelsius = snapshot.temperatureCelsius,
            voltageMv = snapshot.voltageMv,
            currentMa = snapshot.currentMa,
            isCharging = snapshot.isCharging
        )
        batterySnapshotDao.insertSnapshot(entity)
    }

    override suspend fun exportHistoryToCsv(): String {
        val snapshots = batterySnapshotDao.getSnapshotsSince(0L).first()
        val sessions = chargingSessionDao.getAllSessions().first()
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

        val sb = StringBuilder()
        sb.append("# CellGuard Battery Intelligence Data Export\n")
        sb.append("# Generated: ${isoFormat.format(Date())}\n\n")

        sb.append("--- BATTERY SNAPSHOTS ---\n")
        sb.append("Timestamp,ISO_Date,Battery_Level_Pct,Temperature_C,Voltage_mV,Current_mA,Is_Charging\n")
        snapshots.forEach { s ->
            sb.append("${s.timestamp},")
            sb.append("${isoFormat.format(Date(s.timestamp))},")
            sb.append("${s.percentage},")
            sb.append("${String.format(Locale.US, "%.1f", s.temperatureCelsius)},")
            sb.append("${s.voltageMv},")
            sb.append("${s.currentMa?.let { String.format(Locale.US, "%.1f", it) } ?: "N/A"},")
            sb.append("${s.isCharging}\n")
        }

        sb.append("\n--- CHARGING SESSIONS ---\n")
        sb.append("Session_ID,Start_Time,End_Time,Duration_Min,Start_Pct,End_Pct,Gain_Pct,Max_Temp_C,Avg_Temp_C,Avg_Voltage_mV,Source,Estimated_Capacity_mAh\n")
        sessions.forEach { cs ->
            val durationMin = ((cs.endTime - cs.startTime) / 60000).coerceAtLeast(1)
            val gainPct = cs.endPercentage - cs.startPercentage
            sb.append("${cs.id},")
            sb.append("${isoFormat.format(Date(cs.startTime))},")
            sb.append("${isoFormat.format(Date(cs.endTime))},")
            sb.append("$durationMin,")
            sb.append("${cs.startPercentage},")
            sb.append("${cs.endPercentage},")
            sb.append("$gainPct,")
            sb.append("${String.format(Locale.US, "%.1f", cs.maxTemperatureC)},")
            sb.append("${String.format(Locale.US, "%.1f", cs.avgTemperatureC)},")
            sb.append("${cs.avgVoltageMv},")
            sb.append("${cs.sourceName},")
            sb.append("${cs.estimatedCapacityContribution?.let { String.format(Locale.US, "%.1f", it) } ?: "N/A"}\n")
        }

        return sb.toString()
    }

    override suspend fun clearAllHistory() {
        batterySnapshotDao.clearAll()
        chargingSessionDao.clearAll()
        healthEstimateDao.clearAll()
    }

    private fun BatterySnapshotEntity.toDomain(): BatterySnapshot = BatterySnapshot(
        id = id,
        timestamp = timestamp,
        percentage = percentage,
        temperatureCelsius = temperatureCelsius,
        voltageMv = voltageMv,
        currentMa = currentMa,
        isCharging = isCharging
    )

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
}
