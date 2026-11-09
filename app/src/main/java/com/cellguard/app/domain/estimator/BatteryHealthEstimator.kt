package com.cellguard.app.domain.estimator

import com.cellguard.app.domain.model.BatteryHealthStatus
import com.cellguard.app.domain.model.ChargingSession
import com.cellguard.app.domain.model.HealthConfidence
import com.cellguard.app.domain.model.HealthEstimate
import com.cellguard.app.domain.model.MetricSource
import kotlin.math.abs
import kotlin.math.sqrt

object BatteryHealthEstimator {

    private const val MIN_PERCENTAGE_GAIN_FOR_ESTIMATION = 20
    private const val IDEAL_PERCENTAGE_GAIN = 40
    private const val MIN_VALID_SESSIONS_COUNT = 3

    /**
     * Estimates full charge capacity from a single charging session.
     * Formula: Full Capacity = (Delta Charge in mAh / Delta Percentage) * 100
     * Returns null if session does not qualify or data is invalid.
     */
    fun estimateSessionCapacityMah(session: ChargingSession): Float? {
        val deltaPct = session.endPercentage - session.startPercentage
        if (deltaPct < MIN_PERCENTAGE_GAIN_FOR_ESTIMATION) {
            return null
        }

        val startUah = session.startChargeCounterUah ?: return null
        val endUah = session.endChargeCounterUah ?: return null

        val deltaUah = endUah - startUah
        if (deltaUah <= 0) {
            // Counter reset, overflow, or invalid reading
            return null
        }

        val deltaMah = deltaUah / 1000f
        val estimatedFullCapacity = (deltaMah / deltaPct.toFloat()) * 100f

        // Physical sanity check for smartphone batteries (500mAh to 15000mAh)
        return if (estimatedFullCapacity in 500f..15000f) {
            estimatedFullCapacity
        } else {
            null
        }
    }

    /**
     * Evaluates multiple charging sessions and calculates aggregated battery health.
     */
    fun computeHealthEstimate(
        sessions: List<ChargingSession>,
        verifiedDesignCapacityMah: Int?,
        hardwareCycleCount: Int?
    ): HealthEstimate {
        // Filter sessions with valid capacity estimates
        val validEstimatesWithWeights = sessions.mapNotNull { session ->
            val capacity = session.estimatedCapacityContribution ?: estimateSessionCapacityMah(session)
            if (capacity != null) {
                val weight = (session.percentageGained.toFloat() / 100f).coerceIn(0.2f, 1.0f)
                Pair(capacity, weight)
            } else null
        }

        val validCount = validEstimatesWithWeights.size

        // Calculate cycle count: hardware reported if available, otherwise sum of delta percentages / 100
        val (cycleCount, cycleSource) = if (hardwareCycleCount != null && hardwareCycleCount > 0) {
            Pair(hardwareCycleCount, MetricSource.SYSTEM_REPORTED)
        } else {
            val totalChargedPct = sessions.sumOf { it.percentageGained }
            val estimatedCycles = (totalChargedPct / 100).coerceAtLeast(0)
            if (estimatedCycles > 0) {
                Pair(estimatedCycles, MetricSource.ESTIMATED)
            } else {
                Pair(null, MetricSource.UNSUPPORTED)
            }
        }

        if (validCount < MIN_VALID_SESSIONS_COUNT || verifiedDesignCapacityMah == null || verifiedDesignCapacityMah <= 0) {
            val reason = when {
                verifiedDesignCapacityMah == null -> "Design capacity is not verified. Please set your device capacity in Settings."
                validCount == 0 -> "No complete charging sessions recorded yet. Connect charger and charge by at least 20% to calibrate."
                else -> "Recorded $validCount of $MIN_VALID_SESSIONS_COUNT required charging sessions. More data needed."
            }

            return HealthEstimate(
                estimatedFullCapacityMah = null,
                verifiedDesignCapacityMah = verifiedDesignCapacityMah,
                healthPercentage = null,
                condition = BatteryHealthStatus.INSUFFICIENT_DATA,
                confidence = HealthConfidence.NOT_ENOUGH_DATA,
                confidenceReason = reason,
                cycleCount = cycleCount,
                cycleCountSource = cycleSource,
                estimatedCapacityLossPct = null,
                validSessionsCount = validCount
            )
        }

        // Outlier rejection: remove estimates outside 40% to 125% of design capacity
        val filtered = validEstimatesWithWeights.filter { (cap, _) ->
            cap >= (verifiedDesignCapacityMah * 0.40f) && cap <= (verifiedDesignCapacityMah * 1.25f)
        }

        if (filtered.isEmpty()) {
            return HealthEstimate(
                estimatedFullCapacityMah = null,
                verifiedDesignCapacityMah = verifiedDesignCapacityMah,
                healthPercentage = null,
                condition = BatteryHealthStatus.INSUFFICIENT_DATA,
                confidence = HealthConfidence.NOT_ENOUGH_DATA,
                confidenceReason = "Measurements showed high variance or irregular hardware counter behavior. Further charging sessions required.",
                cycleCount = cycleCount,
                cycleCountSource = cycleSource,
                estimatedCapacityLossPct = null,
                validSessionsCount = 0
            )
        }

        // Weighted Average
        val totalWeightedCapacity = filtered.sumOf { (cap, weight) -> (cap * weight).toDouble() }
        val totalWeight = filtered.sumOf { (_, weight) -> weight.toDouble() }
        val estimatedFullCapacity = (totalWeightedCapacity / totalWeight).toFloat()

        // Health percentage = (Estimated Full Charge Capacity / Verified Design Capacity) * 100
        val rawHealthPct = (estimatedFullCapacity / verifiedDesignCapacityMah.toFloat()) * 100f
        val boundedHealthPct = rawHealthPct.coerceIn(10f, 100f)
        val capacityLossPct = (100f - boundedHealthPct).coerceAtLeast(0f)

        // Standard Deviation for measurement consistency
        val variance = filtered.map { (cap, _) -> (cap - estimatedFullCapacity) * (cap - estimatedFullCapacity) }.average()
        val stdDev = sqrt(variance).toFloat()
        val relativeStdDevPct = (stdDev / estimatedFullCapacity) * 100f

        // Confidence evaluation
        val (confidence, confidenceReason) = when {
            filtered.size >= 8 && relativeStdDevPct < 5.0f -> {
                Pair(HealthConfidence.HIGH, "High confidence based on ${filtered.size} consistent charge sessions.")
            }
            filtered.size >= 4 && relativeStdDevPct < 10.0f -> {
                Pair(HealthConfidence.MEDIUM, "Moderate confidence from ${filtered.size} charge cycles. Ongoing sessions will refine precision.")
            }
            else -> {
                Pair(HealthConfidence.LOW, "Preliminary estimate from ${filtered.size} sessions. Measurement variance is currently ${String.format("%.1f", relativeStdDevPct)}%.")
            }
        }

        // Condition classification
        val condition = when {
            boundedHealthPct >= 90f -> BatteryHealthStatus.EXCELLENT
            boundedHealthPct >= 80f -> BatteryHealthStatus.GOOD
            boundedHealthPct >= 70f -> BatteryHealthStatus.FAIR
            boundedHealthPct >= 50f -> BatteryHealthStatus.DEGRADED
            else -> BatteryHealthStatus.CRITICAL
        }

        return HealthEstimate(
            estimatedFullCapacityMah = estimatedFullCapacity,
            verifiedDesignCapacityMah = verifiedDesignCapacityMah,
            healthPercentage = boundedHealthPct,
            condition = condition,
            confidence = confidence,
            confidenceReason = confidenceReason,
            cycleCount = cycleCount,
            cycleCountSource = cycleSource,
            estimatedCapacityLossPct = capacityLossPct,
            validSessionsCount = filtered.size
        )
    }
}
