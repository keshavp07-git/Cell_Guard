package com.cellguard.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cellguard.app.core.designsystem.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cellguard_prefs")

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val customDesignCapacityMah: Int? = null,
    val notifyFullCharge: Boolean = true,
    val notifyHighTemp: Boolean = true,
    val highTempThresholdC: Int = 41,
    val notifyLowBattery: Boolean = true,
    val historyRetentionDays: Int = 30
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val CUSTOM_DESIGN_CAPACITY = intPreferencesKey("custom_design_capacity")
        val NOTIFY_FULL_CHARGE = booleanPreferencesKey("notify_full_charge")
        val NOTIFY_HIGH_TEMP = booleanPreferencesKey("notify_high_temp")
        val HIGH_TEMP_THRESHOLD = intPreferencesKey("high_temp_threshold")
        val NOTIFY_LOW_BATTERY = booleanPreferencesKey("notify_low_battery")
        val RETENTION_DAYS = intPreferencesKey("retention_days")
    }

    val userSettings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        val themeMode = try { ThemeMode.valueOf(themeStr) } catch (_: Throwable) { ThemeMode.SYSTEM }

        val unitStr = prefs[Keys.TEMP_UNIT] ?: TemperatureUnit.CELSIUS.name
        val tempUnit = try { TemperatureUnit.valueOf(unitStr) } catch (_: Throwable) { TemperatureUnit.CELSIUS }

        val customCap = prefs[Keys.CUSTOM_DESIGN_CAPACITY]
        val validCustomCap = if (customCap != null && customCap in 800..15000) customCap else null

        UserSettings(
            themeMode = themeMode,
            temperatureUnit = tempUnit,
            customDesignCapacityMah = validCustomCap,
            notifyFullCharge = prefs[Keys.NOTIFY_FULL_CHARGE] ?: true,
            notifyHighTemp = prefs[Keys.NOTIFY_HIGH_TEMP] ?: true,
            highTempThresholdC = prefs[Keys.HIGH_TEMP_THRESHOLD] ?: 41,
            notifyLowBattery = prefs[Keys.NOTIFY_LOW_BATTERY] ?: true,
            historyRetentionDays = prefs[Keys.RETENTION_DAYS] ?: 30
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { it[Keys.TEMP_UNIT] = unit.name }
    }

    suspend fun setCustomDesignCapacity(capacityMah: Int?) {
        context.dataStore.edit {
            if (capacityMah != null && capacityMah in 800..15000) {
                it[Keys.CUSTOM_DESIGN_CAPACITY] = capacityMah
            } else {
                it.remove(Keys.CUSTOM_DESIGN_CAPACITY)
            }
        }
    }

    suspend fun setNotifyFullCharge(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFY_FULL_CHARGE] = enabled }
    }

    suspend fun setNotifyHighTemp(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFY_HIGH_TEMP] = enabled }
    }

    suspend fun setHighTempThreshold(celsius: Int) {
        context.dataStore.edit { it[Keys.HIGH_TEMP_THRESHOLD] = celsius }
    }

    suspend fun setNotifyLowBattery(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFY_LOW_BATTERY] = enabled }
    }

    suspend fun setHistoryRetentionDays(days: Int) {
        context.dataStore.edit { it[Keys.RETENTION_DAYS] = days }
    }
}
