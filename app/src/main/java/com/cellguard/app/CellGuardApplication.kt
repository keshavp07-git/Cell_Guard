package com.cellguard.app

import android.app.Application
import com.cellguard.app.di.AppContainer
import com.cellguard.app.di.DefaultAppContainer
import com.cellguard.app.domain.model.BatterySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CellGuardApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var lastRecordedPercentage = -1
    private var lastSnapshotTimestamp = 0L
    private var lastAlertedHighTempTimestamp = 0L
    private var lastAlertedFullChargeTimestamp = 0L
    private var lastAlertedLowBatteryTimestamp = 0L

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
        startBackgroundMonitoring()
    }

    private fun startBackgroundMonitoring() {
        applicationScope.launch {
            val batteryRepo = appContainer.batteryRepository
            val historyRepo = appContainer.historyRepository
            val prefsRepo = appContainer.userPreferencesRepository
            val notificationMgr = appContainer.notificationManager

            // Initial health estimation calculation
            launch {
                try {
                    batteryRepo.recalculateHealthEstimate()
                } catch (_: Throwable) {}
            }

            // Real-time event-driven battery monitoring
            batteryRepo.observeBatteryMetrics().collectLatest { metrics ->
                try {
                    // Update charging session tracker
                    batteryRepo.recordMetricsUpdate(metrics)

                    val now = System.currentTimeMillis()
                    val pct = metrics.level

                    // Save snapshot only when percentage changes or at least 15 minutes have passed
                    // This satisfies "The application must not become a battery-draining battery application"
                    if (pct != lastRecordedPercentage || (now - lastSnapshotTimestamp) > 15 * 60 * 1000L) {
                        lastRecordedPercentage = pct
                        lastSnapshotTimestamp = now

                        val snapshot = BatterySnapshot(
                            timestamp = now,
                            percentage = pct,
                            temperatureCelsius = metrics.temperatureCelsius,
                            voltageMv = metrics.voltageMv,
                            currentMa = metrics.normalizedCurrentMa(),
                            isCharging = metrics.isCharging
                        )
                        historyRepo.recordSnapshot(snapshot)
                    }

                    // Evaluate notification rules safely (offline, non-spamming)
                    val settings = prefsRepo.userSettings.first()

                    // 1. High Temperature Alert
                    if (settings.notifyHighTemp && metrics.temperatureCelsius >= settings.highTempThresholdC) {
                        if (now - lastAlertedHighTempTimestamp > 30 * 60 * 1000L) { // 30 min cooldown
                            lastAlertedHighTempTimestamp = now
                            notificationMgr.showHighTemperatureAlert(metrics.temperatureCelsius)
                        }
                    }

                    // 2. Full Charge Alert
                    if (settings.notifyFullCharge && metrics.isCharging && metrics.isFull) {
                        if (now - lastAlertedFullChargeTimestamp > 60 * 60 * 1000L) { // 60 min cooldown
                            lastAlertedFullChargeTimestamp = now
                            notificationMgr.showFullChargeNotification()
                        }
                    }

                    // 3. Low Battery Alert
                    if (settings.notifyLowBattery && !metrics.isCharging && pct <= 15) {
                        if (now - lastAlertedLowBatteryTimestamp > 60 * 60 * 1000L) { // 60 min cooldown
                            lastAlertedLowBatteryTimestamp = now
                            notificationMgr.showLowBatteryNotification(pct)
                        }
                    }

                } catch (_: Throwable) {
                    // Fail silently to prevent crash in background monitor
                }
            }
        }
    }
}
