package com.cellguard.app

import com.cellguard.app.domain.insights.SmartInsightEngine
import com.cellguard.app.domain.model.BatteryHealthStatus
import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.ChargingSession
import com.cellguard.app.domain.model.ChargingSource
import com.cellguard.app.domain.model.ChargingStatus
import com.cellguard.app.domain.model.HealthConfidence
import com.cellguard.app.domain.model.HealthEstimate
import com.cellguard.app.domain.model.InsightType
import com.cellguard.app.domain.model.MetricSource
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartInsightEngineTest {

    @Test
    fun generateInsights_criticalTemperature_triggersHighHeatAlert() {
        val hotMetrics = BatteryMetrics(
            level = 85,
            status = ChargingStatus.CHARGING,
            source = ChargingSource.AC,
            temperatureCelsius = 43.5f, // Critical heat!
            voltageMv = 4250,
            technology = "Li-ion"
        )

        val insights = SmartInsightEngine.generateInsights(
            currentMetrics = hotMetrics,
            healthEstimate = null,
            recentSessions = emptyList()
        )

        val heatWarning = insights.find { it.id == "high_temp_critical" }
        assertTrue("High temp warning should be triggered at 43.5C", heatWarning != null)
        assertTrue(heatWarning!!.type == InsightType.WARNING)
    }

    @Test
    fun generateInsights_calibrationNeeded_triggersTip() {
        val normalMetrics = BatteryMetrics(
            level = 50,
            status = ChargingStatus.DISCHARGING,
            source = ChargingSource.NONE,
            temperatureCelsius = 28.0f,
            voltageMv = 3850,
            technology = "Li-ion"
        )

        val insights = SmartInsightEngine.generateInsights(
            currentMetrics = normalMetrics,
            healthEstimate = null, // No estimate yet
            recentSessions = emptyList()
        )

        val calibrationTip = insights.find { it.id == "calibration_needed" }
        assertTrue("Calibration tip should trigger when health estimate is null", calibrationTip != null)
        assertTrue(calibrationTip!!.type == InsightType.TIP)
    }

    @Test
    fun generateInsights_slowChargingSession_triggersSlowChargeInsight() {
        val metrics = BatteryMetrics(
            level = 60,
            status = ChargingStatus.DISCHARGING,
            source = ChargingSource.NONE,
            temperatureCelsius = 29.0f,
            voltageMv = 3900,
            technology = "Li-ion"
        )

        // Prior average duration ~ 30 minutes
        // Latest session: 120 minutes with only 15% gain
        val sessions = listOf(
            ChargingSession(
                id = 1,
                startTime = 100000L,
                endTime = 100000L + (120 * 60000L), // 120 minutes!
                startPercentage = 40,
                endPercentage = 55, // only 15% gained
                startTemperatureC = 28f,
                maxTemperatureC = 30f,
                avgTemperatureC = 29f,
                avgVoltageMv = 3950,
                source = ChargingSource.USB,
                isCompleted = true
            ),
            ChargingSession(
                id = 2,
                startTime = 50000L,
                endTime = 50000L + (30 * 60000L),
                startPercentage = 20,
                endPercentage = 80,
                startTemperatureC = 28f,
                maxTemperatureC = 34f,
                avgTemperatureC = 31f,
                avgVoltageMv = 4100,
                source = ChargingSource.AC,
                isCompleted = true
            ),
            ChargingSession(
                id = 3,
                startTime = 10000L,
                endTime = 10000L + (30 * 60000L),
                startPercentage = 20,
                endPercentage = 80,
                startTemperatureC = 28f,
                maxTemperatureC = 34f,
                avgTemperatureC = 31f,
                avgVoltageMv = 4100,
                source = ChargingSource.AC,
                isCompleted = true
            )
        )

        val insights = SmartInsightEngine.generateInsights(
            currentMetrics = metrics,
            healthEstimate = null,
            recentSessions = sessions
        )

        val slowChargeInsight = insights.find { it.id == "slow_charging" }
        assertTrue("Slow charge insight should be flagged", slowChargeInsight != null)
    }
}
