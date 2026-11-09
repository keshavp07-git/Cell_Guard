package com.cellguard.app.feature.batteryhealth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cellguard.app.data.preferences.UserPreferencesRepository
import com.cellguard.app.data.preferences.UserSettings
import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.HardwareCapabilities
import com.cellguard.app.domain.model.HealthEstimate
import com.cellguard.app.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HealthUiState(
    val estimate: HealthEstimate?,
    val currentMetrics: BatteryMetrics,
    val capabilities: HardwareCapabilities,
    val settings: UserSettings,
    val isRecalculating: Boolean = false
)

class BatteryHealthViewModel(
    private val batteryRepository: BatteryRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<HealthUiState> = combine(
        batteryRepository.observeHealthEstimate(),
        batteryRepository.observeBatteryMetrics(),
        preferencesRepository.userSettings
    ) { estimate, metrics, settings ->
        HealthUiState(
            estimate = estimate,
            currentMetrics = metrics,
            capabilities = batteryRepository.getHardwareCapabilities(),
            settings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HealthUiState(
            estimate = null,
            currentMetrics = batteryRepository.getCurrentMetrics(),
            capabilities = batteryRepository.getHardwareCapabilities(),
            settings = UserSettings()
        )
    )

    fun forceRecalculate() {
        viewModelScope.launch {
            batteryRepository.recalculateHealthEstimate()
        }
    }
}
