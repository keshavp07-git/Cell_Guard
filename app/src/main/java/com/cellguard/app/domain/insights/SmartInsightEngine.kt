package com.cellguard.app.domain.insights

import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.ChargingSession
import com.cellguard.app.domain.model.HealthEstimate
import com.cellguard.app.domain.model.InsightType
import com.cellguard.app.domain.model.SmartInsight

object SmartInsightEngine {

    fun generateInsights(
        currentMetrics: BatteryMetrics,
        healthEstimate: HealthEstimate?,
        recentSessions: List<ChargingSession>
    ): List<SmartInsight> {
        val insights = mutableListOf<SmartInsight>()

        // 1. Current Temperature Insight
        if (currentMetrics.temperatureCelsius >= 42.0f) {
            insights.add(
                SmartInsight(
                    id = "high_temp_critical",
                    title = "Elevated Battery Temperature",
                    description = "Battery is currently ${String.format("%.1f", currentMetrics.temperatureCelsius)}°C. High heat accelerates electrolyte degradation. Consider unplugging or removing any heavy case while charging.",
                    type = InsightType.WARNING
                )
            )
        } else if (currentMetrics.temperatureCelsius in 38.0f..41.9f && currentMetrics.isCharging) {
            insights.add(
                SmartInsight(
                    id = "warm_charging",
                    title = "Warm Charging Session",
                    description = "Battery temperature is ${String.format("%.1f", currentMetrics.temperatureCelsius)}°C. Fast charging naturally generates heat; keeping the phone in a ventilated area preserves long-term capacity.",
                    type = InsightType.INFO
                )
            )
        }

        // 2. Health Calibration Insight
        if (healthEstimate == null || healthEstimate.healthPercentage == null) {
            insights.add(
                SmartInsight(
                    id = "calibration_needed",
                    title = "Battery Calibration in Progress",
                    description = "CellGuard needs 3 or more substantial charge sessions (e.g., gain of 25%+ per charge) to build an honest capacity estimate.",
                    type = InsightType.TIP
                )
            )
        } else if (healthEstimate.healthPercentage >= 92f) {
            insights.add(
                SmartInsight(
                    id = "health_excellent",
                    title = "Optimal Battery Retention",
                    description = "Your battery capacity is holding near factory specifications (${String.format("%.0f", healthEstimate.healthPercentage)}%). Current charging practices are maintaining good cell longevity.",
                    type = InsightType.POSITIVE
                )
            )
        }

        // 3. Charging Session Trends
        if (recentSessions.isNotEmpty()) {
            val completedSessions = recentSessions.filter { it.isCompleted }
            if (completedSessions.size >= 3) {
                val avgDuration = completedSessions.map { it.durationMinutes }.average()
                val latest = completedSessions.first()

                if (latest.durationMinutes > (avgDuration * 1.6) && latest.percentageGained < 30) {
                    insights.add(
                        SmartInsight(
                            id = "slow_charging",
                            title = "Unusually Slow Charge Detected",
                            description = "Your recent charge took ${latest.durationMinutes} minutes, which is longer than your average (${avgDuration.toInt()} min). Inspect your cable, adapter, or port connection.",
                            type = InsightType.INFO
                        )
                    )
                }

                val highHeatSessions = completedSessions.take(5).count { it.maxTemperatureC >= 40.0f }
                if (highHeatSessions >= 2) {
                    insights.add(
                        SmartInsight(
                            id = "repeated_heat",
                            title = "Repeated Warm Charging Observed",
                            description = "Several recent charging sessions peaked above 40°C. Avoiding intensive 3D gaming or direct sunlight during fast charging protects chemical stability.",
                            type = InsightType.WARNING
                        )
                    )
                }
            }
        }

        // 4. Good Habit Advice
        insights.add(
            SmartInsight(
                id = "charging_tip_range",
                title = "The 20% - 80% Principle",
                description = "Lithium-ion cells experience the lowest mechanical stress between 20% and 80% charge levels. Avoiding prolonged stays at 0% or 100% slows down aging.",
                type = InsightType.TIP
            )
        )

        return insights
    }
}
