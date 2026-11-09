package com.cellguard.app.data.battery

import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.ChargingSource
import com.cellguard.app.domain.model.ChargingStatus
import com.cellguard.app.domain.model.HardwareCapabilities
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Test & Preview implementation of BatteryHardwareDataSource.
 * Used exclusively for automated UI tests and unit verification.
 * NEVER substituted silently for physical device readings in production.
 */
class SimulatedBatteryDataSource(
    private val initialPercentage: Int = 84,
    private val isCharging: Boolean = true,
    private val simulateDegradationPct: Float = 92f
) : BatteryHardwareDataSource {

    override fun observeBatteryMetrics(): Flow<BatteryMetrics> = flow {
        var currentPct = initialPercentage
        var temp = 31.4f
        var voltage = 4150
        while (true) {
            emit(
                BatteryMetrics(
                    level = currentPct,
                    status = if (isCharging) ChargingStatus.CHARGING else ChargingStatus.DISCHARGING,
                    source = if (isCharging) ChargingSource.AC else ChargingSource.NONE,
                    temperatureCelsius = temp,
                    voltageMv = voltage,
                    technology = "Li-poly",
                    chargeCounterUah = (4600L * 1000L * currentPct) / 100L,
                    currentNowUa = if (isCharging) 2_150_000L else -450_000L,
                    currentAverageUa = if (isCharging) 2_000_000L else -420_000L,
                    energyCounterNwh = 16_000_000_000L,
                    hardwareCycleCount = 142,
                    designCapacityMah = 5000,
                    timestamp = System.currentTimeMillis()
                )
            )
            delay(5000)
            if (isCharging && currentPct < 100) {
                currentPct++
                temp += 0.1f
            }
        }
    }

    override fun getCurrentMetrics(): BatteryMetrics = BatteryMetrics(
        level = initialPercentage,
        status = if (isCharging) ChargingStatus.CHARGING else ChargingStatus.DISCHARGING,
        source = if (isCharging) ChargingSource.AC else ChargingSource.NONE,
        temperatureCelsius = 31.4f,
        voltageMv = 4150,
        technology = "Li-poly",
        chargeCounterUah = 4_200_000L,
        currentNowUa = 2_150_000L,
        currentAverageUa = 2_000_000L,
        energyCounterNwh = 16_000_000_000L,
        hardwareCycleCount = 142,
        designCapacityMah = 5000,
        timestamp = System.currentTimeMillis()
    )

    override fun getHardwareCapabilities(): HardwareCapabilities = HardwareCapabilities(
        manufacturer = "CellGuard Sim",
        model = "Virtual Device",
        androidVersion = 35,
        supportsChargeCounter = true,
        supportsCurrentNow = true,
        supportsCurrentAverage = true,
        supportsEnergyCounter = true,
        supportsHardwareCycleCount = true,
        supportsDesignCapacityProfile = true
    )
}
