package com.cellguard.app.di

import android.content.Context
import com.cellguard.app.core.database.CellGuardDatabase
import com.cellguard.app.data.battery.BatteryHardwareDataSource
import com.cellguard.app.data.battery.BatteryHardwareDataSourceImpl
import com.cellguard.app.data.preferences.UserPreferencesRepository
import com.cellguard.app.data.repository.BatteryRepositoryImpl
import com.cellguard.app.data.repository.HistoryRepositoryImpl
import com.cellguard.app.domain.repository.BatteryRepository
import com.cellguard.app.domain.repository.HistoryRepository
import com.cellguard.app.service.notifications.BatteryNotificationManager

interface AppContainer {
    val database: CellGuardDatabase
    val hardwareDataSource: BatteryHardwareDataSource
    val userPreferencesRepository: UserPreferencesRepository
    val batteryRepository: BatteryRepository
    val historyRepository: HistoryRepository
    val notificationManager: BatteryNotificationManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val database: CellGuardDatabase by lazy {
        CellGuardDatabase.getInstance(context)
    }

    override val hardwareDataSource: BatteryHardwareDataSource by lazy {
        BatteryHardwareDataSourceImpl(context)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    override val batteryRepository: BatteryRepository by lazy {
        BatteryRepositoryImpl(
            hardwareDataSource = hardwareDataSource,
            chargingSessionDao = database.chargingSessionDao(),
            healthEstimateDao = database.healthEstimateDao(),
            preferencesRepository = userPreferencesRepository
        )
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(
            batterySnapshotDao = database.batterySnapshotDao(),
            chargingSessionDao = database.chargingSessionDao(),
            healthEstimateDao = database.healthEstimateDao()
        )
    }

    override val notificationManager: BatteryNotificationManager by lazy {
        BatteryNotificationManager(context)
    }
}
