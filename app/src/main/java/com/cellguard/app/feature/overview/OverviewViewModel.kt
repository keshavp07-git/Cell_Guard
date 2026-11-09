package com.cellguard.app.feature.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cellguard.app.data.preferences.TemperatureUnit
import com.cellguard.app.data.preferences.UserPreferencesRepository
import com.cellguard.app.data.preferences.UserSettings
import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.HardwareCapabilities
import com.cellguard.app.domain.model.HealthEstimate
import com.cellguard.app.domain.model.SmartInsight
import com.cellguard.app.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class OverviewUiState(
    val metrics: BatteryMetrics,
    val capabilities: HardwareCapabilities,
    val healthEstimate: HealthEstimate?,
    val insights: List<SmartInsight>,
    val settings: UserSettings,
    val isLoading: Boolean = false
)

class OverviewViewModel(
    private val batteryRepository: BatteryRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<OverviewUiState> = combine(
        batteryRepository.observeBatteryMetrics(),
        batteryRepository.observeHealthEstimate(),
        batteryRepository.observeSmartInsights(),
        preferencesRepository.userSettings
    ) { metrics, health, insights, settings ->
        OverviewUiState(
            metrics = metrics,
            capabilities = batteryRepository.getHardwareCapabilities(),
            healthEstimate = health,
            insights = insights,
            settings = settings,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OverviewUiState(
            metrics = batteryRepository.getCurrentMetrics(),
            capabilities = batteryRepository.getHardwareCapabilities(),
            healthEstimate = null,
            insights = emptyList(),
            settings = UserSettings(),
            isLoading = true
        )
    )
}
