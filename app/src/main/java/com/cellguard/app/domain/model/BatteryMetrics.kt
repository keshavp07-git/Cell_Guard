package com.cellguard.app.domain.model

data class BatteryMetrics(
    val level: Int,
    val status: ChargingStatus,
    val source: ChargingSource,
    val temperatureCelsius: Float,
    val voltageMv: Int,
    val technology: String,
    val chargeCounterUah: Long? = null,
    val currentNowUa: Long? = null,
    val currentAverageUa: Long? = null,
    val energyCounterNwh: Long? = null,
    val hardwareCycleCount: Int? = null,
    val designCapacityMah: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isCharging: Boolean
        get() = status == ChargingStatus.CHARGING

    val isFull: Boolean
        get() = status == ChargingStatus.FULL || level >= 100

    /**
     * Converts microamperes to milliamperes with consistent sign.
     * When charging: positive indicates inflow.
     * When discharging: positive indicates drain rate.
     */
    fun normalizedCurrentMa(): Float? {
        val raw = currentNowUa ?: return null
        val absMa = kotlin.math.abs(raw) / 1000f
        return if (absMa in 1f..15000f) absMa else null
    }

    /**
     * Calculate charging/discharging wattage if both valid voltage and current are present.
     * Wattage = (Voltage in Volts) * (Current in Amperes)
     */
    fun calculatedWattage(): Float? {
        val ma = normalizedCurrentMa() ?: return null
        if (voltageMv <= 0) return null
        val volts = voltageMv / 1000f
        val amps = ma / 1000f
        val watts = volts * amps
        return if (watts in 0.05f..200f) watts else null
    }
}

data class HardwareCapabilities(
    val manufacturer: String,
    val model: String,
    val androidVersion: Int,
    val supportsChargeCounter: Boolean,
    val supportsCurrentNow: Boolean,
    val supportsCurrentAverage: Boolean,
    val supportsEnergyCounter: Boolean,
    val supportsHardwareCycleCount: Boolean,
    val supportsDesignCapacityProfile: Boolean
)
