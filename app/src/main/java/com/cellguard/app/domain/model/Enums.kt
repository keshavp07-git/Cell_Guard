package com.cellguard.app.domain.model

enum class ChargingStatus {
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING,
    UNKNOWN;

    fun displayName(): String = when (this) {
        CHARGING -> "Charging"
        DISCHARGING -> "Discharging"
        FULL -> "Full"
        NOT_CHARGING -> "Not Charging"
        UNKNOWN -> "Unknown"
    }
}

enum class ChargingSource {
    AC,
    USB,
    WIRELESS,
    DOCK,
    NONE,
    UNKNOWN;

    fun displayName(): String = when (this) {
        AC -> "Fast Charger (AC)"
        USB -> "USB Cable"
        WIRELESS -> "Wireless Qi / Mag"
        DOCK -> "Charging Dock"
        NONE -> "Battery Power"
        UNKNOWN -> "Unknown Source"
    }
}

enum class BatteryHealthStatus {
    EXCELLENT,
    GOOD,
    FAIR,
    DEGRADED,
    CRITICAL,
    INSUFFICIENT_DATA;

    fun displayName(): String = when (this) {
        EXCELLENT -> "Excellent"
        GOOD -> "Good"
        FAIR -> "Fair"
        DEGRADED -> "Degraded"
        CRITICAL -> "Critical"
        INSUFFICIENT_DATA -> "Collecting Data"
    }
}

enum class HealthConfidence {
    HIGH,
    MEDIUM,
    LOW,
    NOT_ENOUGH_DATA;

    fun displayName(): String = when (this) {
        HIGH -> "High Confidence"
        MEDIUM -> "Medium Confidence"
        LOW -> "Low Confidence"
        NOT_ENOUGH_DATA -> "Not Enough Data"
    }
}

enum class MetricSource {
    HARDWARE_REPORTED,
    SYSTEM_REPORTED,
    ESTIMATED,
    UNSUPPORTED
}
