package com.cellguard.app.domain.model

data class HealthEstimate(
    val estimatedFullCapacityMah: Float?,
    val verifiedDesignCapacityMah: Int?,
    val healthPercentage: Float?,
    val condition: BatteryHealthStatus,
    val confidence: HealthConfidence,
    val confidenceReason: String,
    val cycleCount: Int?,
    val cycleCountSource: MetricSource,
    val estimatedCapacityLossPct: Float?,
    val validSessionsCount: Int,
    val lastCalculatedTimestamp: Long = System.currentTimeMillis()
)

data class ChargingSession(
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val startPercentage: Int,
    val endPercentage: Int,
    val startChargeCounterUah: Long? = null,
    val endChargeCounterUah: Long? = null,
    val startTemperatureC: Float,
    val maxTemperatureC: Float,
    val avgTemperatureC: Float,
    val avgVoltageMv: Int,
    val avgCurrentMa: Float? = null,
    val source: ChargingSource,
    val isCompleted: Boolean = true,
    val estimatedCapacityContribution: Float? = null
) {
    val durationMinutes: Long
        get() = ((endTime - startTime) / 60000).coerceAtLeast(1)

    val percentageGained: Int
        get() = (endPercentage - startPercentage).coerceAtLeast(0)
}

data class BatterySnapshot(
    val id: Long = 0,
    val timestamp: Long,
    val percentage: Int,
    val temperatureCelsius: Float,
    val voltageMv: Int,
    val currentMa: Float? = null,
    val isCharging: Boolean = false
)

enum class InsightType {
    INFO,
    WARNING,
    TIP,
    POSITIVE
}

data class SmartInsight(
    val id: String,
    val title: String,
    val description: String,
    val type: InsightType,
    val timestamp: Long = System.currentTimeMillis()
)
