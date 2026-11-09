package com.cellguard.app.data.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.ChargingSource
import com.cellguard.app.domain.model.ChargingStatus
import com.cellguard.app.domain.model.HardwareCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.abs

class BatteryHardwareDataSourceImpl(
    private val context: Context
) : BatteryHardwareDataSource {

    private val batteryManager: BatteryManager by lazy {
        context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    override fun observeBatteryMetrics(): Flow<BatteryMetrics> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent != null && intent.action == Intent.ACTION_BATTERY_CHANGED) {
                    val metrics = parseIntentToMetrics(intent)
                    trySend(metrics)
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val initialIntent = context.registerReceiver(receiver, filter)
        if (initialIntent != null) {
            trySend(parseIntentToMetrics(initialIntent))
        }

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: IllegalArgumentException) {
                // Ignore if already unregistered
            }
        }
    }

    override fun getCurrentMetrics(): BatteryMetrics {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return if (intent != null) {
            parseIntentToMetrics(intent)
        } else {
            // Fallback to direct BatteryManager properties
            val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            BatteryMetrics(
                level = if (level in 0..100) level else 50,
                status = ChargingStatus.UNKNOWN,
                source = ChargingSource.UNKNOWN,
                temperatureCelsius = 25.0f,
                voltageMv = 3800,
                technology = "Li-ion",
                timestamp = System.currentTimeMillis()
            )
        }
    }

    private fun parseIntentToMetrics(intent: Intent): BatteryMetrics {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val percentage = if (scale > 0 && level >= 0) {
            ((level.toFloat() / scale.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100)
        }

        val statusInt = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val status = when (statusInt) {
            BatteryManager.BATTERY_STATUS_CHARGING -> ChargingStatus.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> ChargingStatus.DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> ChargingStatus.FULL
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> ChargingStatus.NOT_CHARGING
            else -> ChargingStatus.UNKNOWN
        }

        val pluggedInt = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val source = when (pluggedInt) {
            BatteryManager.BATTERY_PLUGGED_AC -> ChargingSource.AC
            BatteryManager.BATTERY_PLUGGED_USB -> ChargingSource.USB
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> ChargingSource.WIRELESS
            4 -> ChargingSource.DOCK // BATTERY_PLUGGED_DOCK (API 33)
            0 -> ChargingSource.NONE
            else -> ChargingSource.UNKNOWN
        }

        // Temperature is in tenths of a degree Celsius (e.g. 300 = 30.0 C)
        val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val temperatureCelsius = if (rawTemp > 0) rawTemp / 10.0f else 25.0f

        val voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

        // Charge counter: sanitize and normalize to microampere-hours (uAh)
        val chargeCounter = queryChargeCounterUah()

        // Current now: sanitize to microamperes (uA)
        val currentNow = queryCurrentNowUa()

        // Current average:
        val currentAvg = queryCurrentAverageUa()

        // Energy counter in nWh
        val energyCounter = queryEnergyCounterNwh()

        // Hardware Cycle Count (Android 14+ / vendor intent key)
        val cycleCount = queryCycleCount(intent)

        // Design capacity in mAh
        val designCapacity = PowerProfileHelper.getDesignCapacityMah(context)

        return BatteryMetrics(
            level = percentage,
            status = status,
            source = source,
            temperatureCelsius = temperatureCelsius,
            voltageMv = voltageMv,
            technology = technology,
            chargeCounterUah = chargeCounter,
            currentNowUa = currentNow,
            currentAverageUa = currentAvg,
            energyCounterNwh = energyCounter,
            hardwareCycleCount = cycleCount,
            designCapacityMah = designCapacity,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun queryChargeCounterUah(): Long? {
        val raw = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        if (raw <= 0 || raw == Long.MIN_VALUE || raw == Long.MAX_VALUE) {
            return null
        }
        // If reported in mAh (e.g., 500 to 15000), normalize to uAh
        return if (raw in 500L..15000L) {
            raw * 1000L
        } else if (raw in 100_000L..15_000_000L) {
            raw
        } else {
            null
        }
    }

    private fun queryCurrentNowUa(): Long? {
        val raw = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        if (raw == Long.MIN_VALUE || raw == Long.MAX_VALUE || raw == 0L) {
            return null
        }
        val absVal = abs(raw)
        // Range: 1000 uA (1mA) to 20,000,000 uA (20A)
        return if (absVal in 1000L..20_000_000L) raw else null
    }

    private fun queryCurrentAverageUa(): Long? {
        val raw = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
        if (raw == Long.MIN_VALUE || raw == Long.MAX_VALUE || raw == 0L) {
            return null
        }
        val absVal = abs(raw)
        return if (absVal in 1000L..20_000_000L) raw else null
    }

    private fun queryEnergyCounterNwh(): Long? {
        val raw = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)
        return if (raw > 0 && raw != Long.MIN_VALUE && raw != Long.MAX_VALUE) raw else null
    }

    private fun queryCycleCount(intent: Intent): Int? {
        // Android 14 (API 34) adds BatteryManager.EXTRA_CYCLE_COUNT
        val cycleExtra = intent.getIntExtra("android.os.extra.CYCLE_COUNT", -1)
        if (cycleExtra > 0) return cycleExtra

        // Some Samsung / Xiaomi OEMs use custom extra keys
        val oemKeys = listOf("battery_cycle", "cycle_count", "sec_battery_cycle")
        for (key in oemKeys) {
            val count = intent.getIntExtra(key, -1)
            if (count > 0) return count
        }
        return null
    }

    override fun getHardwareCapabilities(): HardwareCapabilities {
        val current = getCurrentMetrics()
        return HardwareCapabilities(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.SDK_INT,
            supportsChargeCounter = current.chargeCounterUah != null,
            supportsCurrentNow = current.currentNowUa != null,
            supportsCurrentAverage = current.currentAverageUa != null,
            supportsEnergyCounter = current.energyCounterNwh != null,
            supportsHardwareCycleCount = current.hardwareCycleCount != null,
            supportsDesignCapacityProfile = current.designCapacityMah != null
        )
    }
}
