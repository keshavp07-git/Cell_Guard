package com.cellguard.app.domain.repository

import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.ChargingSession
import com.cellguard.app.domain.model.HardwareCapabilities
import com.cellguard.app.domain.model.HealthEstimate
import com.cellguard.app.domain.model.SmartInsight
import kotlinx.coroutines.flow.Flow

interface BatteryRepository {
    fun observeBatteryMetrics(): Flow<BatteryMetrics>
    fun getCurrentMetrics(): BatteryMetrics
    fun getHardwareCapabilities(): HardwareCapabilities
    fun observeHealthEstimate(): Flow<HealthEstimate?>
    fun observeRecentSessions(): Flow<List<ChargingSession>>
    fun observeSmartInsights(): Flow<List<SmartInsight>>
    suspend fun recordMetricsUpdate(metrics: BatteryMetrics)
    suspend fun recalculateHealthEstimate()
}
