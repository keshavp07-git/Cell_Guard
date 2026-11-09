package com.cellguard.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battery_snapshots")
data class BatterySnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val percentage: Int,
    val temperatureCelsius: Float,
    val voltageMv: Int,
    val currentMa: Float?,
    val isCharging: Boolean
)

@Entity(tableName = "charging_sessions")
data class ChargingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val startPercentage: Int,
    val endPercentage: Int,
    val startChargeCounterUah: Long?,
    val endChargeCounterUah: Long?,
    val startTemperatureC: Float,
    val maxTemperatureC: Float,
    val avgTemperatureC: Float,
    val avgVoltageMv: Int,
    val avgCurrentMa: Float?,
    val sourceName: String,
    val isCompleted: Boolean,
    val estimatedCapacityContribution: Float?
)

@Entity(tableName = "health_estimates")
data class HealthEstimateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val estimatedFullCapacityMah: Float?,
    val verifiedDesignCapacityMah: Int?,
    val healthPercentage: Float?,
    val conditionName: String,
    val confidenceName: String,
    val cycleCount: Int?,
    val estimatedLossPct: Float?,
    val validSessionsCount: Int
)
