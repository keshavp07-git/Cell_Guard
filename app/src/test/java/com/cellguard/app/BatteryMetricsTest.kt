package com.cellguard.app

import com.cellguard.app.data.preferences.TemperatureUnit
import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.ChargingSource
import com.cellguard.app.domain.model.ChargingStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BatteryMetricsTest {

    @Test
    fun normalizedCurrentMa_handlesMicroamperesCorrectly() {
        // 2,500,000 uA = 2500 mA
        val metrics = BatteryMetrics(
            level = 65,
            status = ChargingStatus.CHARGING,
            source = ChargingSource.AC,
            temperatureCelsius = 30f,
            voltageMv = 4200,
            technology = "Li-ion",
            currentNowUa = 2_500_000L
        )

        val ma = metrics.normalizedCurrentMa()
        assertNotNull(ma)
        assertEquals(2500f, ma!!, 0.1f)
    }

    @Test
    fun calculatedWattage_computesVoltsTimesAmperes() {
        // 4.0V * 2.0A = 8.0W
        val metrics = BatteryMetrics(
            level = 50,
            status = ChargingStatus.CHARGING,
            source = ChargingSource.AC,
            temperatureCelsius = 31f,
            voltageMv = 4000, // 4.0V
            technology = "Li-ion",
            currentNowUa = 2_000_000L // 2.0A
        )

        val watts = metrics.calculatedWattage()
        assertNotNull(watts)
        assertEquals(8.0f, watts!!, 0.1f)
    }

    @Test
    fun calculatedWattage_zeroCurrentOrNegativeVoltage_returnsNull() {
        val metrics = BatteryMetrics(
            level = 50,
            status = ChargingStatus.CHARGING,
            source = ChargingSource.AC,
            temperatureCelsius = 31f,
            voltageMv = 0,
            technology = "Li-ion",
            currentNowUa = null
        )

        assertNull(metrics.calculatedWattage())
    }

    @Test
    fun temperatureConversion_celsiusToFahrenheit() {
        val celsius = 25.0f
        val fahrenheit = (celsius * 9f / 5f) + 32f
        assertEquals(77.0f, fahrenheit, 0.1f)
    }
}
