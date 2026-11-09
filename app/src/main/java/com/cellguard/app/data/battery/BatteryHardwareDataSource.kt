package com.cellguard.app.data.battery

import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.HardwareCapabilities
import kotlinx.coroutines.flow.Flow

interface BatteryHardwareDataSource {
    /**
     * Emits continuous, event-driven battery state updates from Android BatteryManager
     */
    fun observeBatteryMetrics(): Flow<BatteryMetrics>

    /**
     * Takes an immediate single point-in-time battery reading
     */
    fun getCurrentMetrics(): BatteryMetrics

    /**
     * Evaluates hardware capabilities and API support on this device
     */
    fun getHardwareCapabilities(): HardwareCapabilities
}
