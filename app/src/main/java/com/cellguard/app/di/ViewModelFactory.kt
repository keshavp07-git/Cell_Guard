package com.cellguard.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cellguard.app.feature.analytics.AnalyticsViewModel
import com.cellguard.app.feature.batteryhealth.BatteryHealthViewModel
import com.cellguard.app.feature.overview.OverviewViewModel
import com.cellguard.app.feature.settings.SettingsViewModel

class ViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(OverviewViewModel::class.java) -> {
                OverviewViewModel(
                    batteryRepository = appContainer.batteryRepository,
                    preferencesRepository = appContainer.userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(BatteryHealthViewModel::class.java) -> {
                BatteryHealthViewModel(
                    batteryRepository = appContainer.batteryRepository,
                    preferencesRepository = appContainer.userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(AnalyticsViewModel::class.java) -> {
                AnalyticsViewModel(
                    batteryRepository = appContainer.batteryRepository,
                    historyRepository = appContainer.historyRepository,
                    preferencesRepository = appContainer.userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    preferencesRepository = appContainer.userPreferencesRepository,
                    historyRepository = appContainer.historyRepository,
                    batteryRepository = appContainer.batteryRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
