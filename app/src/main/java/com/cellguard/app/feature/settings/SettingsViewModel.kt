package com.cellguard.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cellguard.app.core.designsystem.theme.ThemeMode
import com.cellguard.app.data.preferences.TemperatureUnit
import com.cellguard.app.data.preferences.UserPreferencesRepository
import com.cellguard.app.data.preferences.UserSettings
import com.cellguard.app.domain.repository.BatteryRepository
import com.cellguard.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val historyRepository: HistoryRepository,
    private val batteryRepository: BatteryRepository
) : ViewModel() {

    val settings: StateFlow<UserSettings> = preferencesRepository.userSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch { preferencesRepository.setTemperatureUnit(unit) }
    }

    fun setCustomDesignCapacity(capacityMah: Int?) {
        viewModelScope.launch {
            preferencesRepository.setCustomDesignCapacity(capacityMah)
            batteryRepository.recalculateHealthEstimate()
        }
    }

    fun setNotifyFullCharge(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setNotifyFullCharge(enabled) }
    }

    fun setNotifyHighTemp(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setNotifyHighTemp(enabled) }
    }

    fun setHighTempThreshold(celsius: Int) {
        viewModelScope.launch { preferencesRepository.setHighTempThreshold(celsius) }
    }

    fun setNotifyLowBattery(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setNotifyLowBattery(enabled) }
    }

    fun setHistoryRetentionDays(days: Int) {
        viewModelScope.launch { preferencesRepository.setHistoryRetentionDays(days) }
    }

    suspend fun exportCsvData(): String {
        return historyRepository.exportHistoryToCsv()
    }

    fun clearAllData() {
        viewModelScope.launch {
            historyRepository.clearAllHistory()
            batteryRepository.recalculateHealthEstimate()
        }
    }
}
