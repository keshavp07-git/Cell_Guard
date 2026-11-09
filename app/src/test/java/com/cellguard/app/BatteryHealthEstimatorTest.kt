package com.cellguard.app

import com.cellguard.app.domain.estimator.BatteryHealthEstimator
import com.cellguard.app.domain.model.BatteryHealthStatus
import com.cellguard.app.domain.model.ChargingSession
import com.cellguard.app.domain.model.ChargingSource
import com.cellguard.app.domain.model.HealthConfidence
import com.cellguard.app.domain.model.MetricSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BatteryHealthEstimatorTest {

    @Test
    fun estimateSessionCapacity_validSession_calculatesCorrectly() {
        // Charging from 20% to 70% (+50%) with 2,400,000 uAh (2400 mAh) gained
        // Expected full capacity = (2400 / 50) * 100 = 4800 mAh
        val session = ChargingSession(
            id = 1,
            startTime = 1000L,
            endTime = 5000L,
            startPercentage = 20,
            endPercentage = 70,
            startChargeCounterUah = 1_000_000L,
            endChargeCounterUah = 3_400_000L,
            startTemperatureC = 28f,
            maxTemperatureC = 34f,
            avgTemperatureC = 31f,
            avgVoltageMv = 4100,
            source = ChargingSource.AC
        )

        val estimatedCap = BatteryHealthEstimator.estimateSessionCapacityMah(session)
        assertNotNull(estimatedCap)
        assertEquals(4800f, estimatedCap!!, 1.0f)
    }

    @Test
    fun estimateSessionCapacity_shortSession_rejectedAsInsufficientDelta() {
        // Only 5% gain (from 50% to 55%) -> Should be rejected to avoid mathematical noise
        val session = ChargingSession(
            id = 2,
            startTime = 1000L,
            endTime = 2000L,
            startPercentage = 50,
            endPercentage = 55,
            startChargeCounterUah = 2_500_000L,
            endChargeCounterUah = 2_750_000L,
            startTemperatureC = 28f,
            maxTemperatureC = 30f,
            avgTemperatureC = 29f,
            avgVoltageMv = 4000,
            source = ChargingSource.USB
        )

        val estimatedCap = BatteryHealthEstimator.estimateSessionCapacityMah(session)
        assertNull("Sessions with < 20% delta must be rejected for capacity calibration", estimatedCap)
    }

    @Test
    fun estimateSessionCapacity_counterReset_rejected() {
        // Counter reset where end counter is less than start counter
        val session = ChargingSession(
            id = 3,
            startTime = 1000L,
            endTime = 5000L,
            startPercentage = 20,
            endPercentage = 80,
            startChargeCounterUah = 4_000_000L,
            endChargeCounterUah = 500_000L, // Reset occurred!
            startTemperatureC = 28f,
            maxTemperatureC = 34f,
            avgTemperatureC = 31f,
            avgVoltageMv = 4100,
            source = ChargingSource.AC
        )

        val estimatedCap = BatteryHealthEstimator.estimateSessionCapacityMah(session)
        assertNull("Negative delta / counter reset must be rejected", estimatedCap)
    }

    @Test
    fun computeHealthEstimate_missingDesignCapacity_returnsInsufficientData() {
        val sessions = listOf(
            createValidSession(1, 4800f),
            createValidSession(2, 4850f),
            createValidSession(3, 4790f)
        )

        val estimate = BatteryHealthEstimator.computeHealthEstimate(
            sessions = sessions,
            verifiedDesignCapacityMah = null, // Missing design capacity
            hardwareCycleCount = 50
        )

        assertEquals(BatteryHealthStatus.INSUFFICIENT_DATA, estimate.condition)
        assertEquals(HealthConfidence.NOT_ENOUGH_DATA, estimate.confidence)
        assertNull(estimate.healthPercentage)
    }

    @Test
    fun computeHealthEstimate_fewerThanThreeSessions_returnsInsufficientData() {
        val sessions = listOf(
            createValidSession(1, 4800f),
            createValidSession(2, 4850f)
        )

        val estimate = BatteryHealthEstimator.computeHealthEstimate(
            sessions = sessions,
            verifiedDesignCapacityMah = 5000,
            hardwareCycleCount = 30
        )

        assertEquals(BatteryHealthStatus.INSUFFICIENT_DATA, estimate.condition)
        assertEquals(HealthConfidence.NOT_ENOUGH_DATA, estimate.confidence)
        assertNull(estimate.healthPercentage)
    }

    @Test
    fun computeHealthEstimate_consistentSessions_calculatesHighHealthAndConfidence() {
        // 5000 mAh design capacity, estimates around 4750 mAh (95% retention)
        val sessions = (1..10).map { id ->
            createValidSession(id.toLong(), 4750f + (id % 3) * 10f)
        }

        val estimate = BatteryHealthEstimator.computeHealthEstimate(
            sessions = sessions,
            verifiedDesignCapacityMah = 5000,
            hardwareCycleCount = 120
        )

        assertNotNull(estimate.healthPercentage)
        assertEquals(95f, estimate.healthPercentage!!, 1.5f)
        assertEquals(BatteryHealthStatus.EXCELLENT, estimate.condition)
        assertEquals(HealthConfidence.HIGH, estimate.confidence)
        assertEquals(120, estimate.cycleCount)
        assertEquals(MetricSource.SYSTEM_REPORTED, estimate.cycleCountSource)
        assertTrue(estimate.estimatedCapacityLossPct!! > 0f)
    }

    @Test
    fun computeHealthEstimate_degradedBattery_classifiesCorrectly() {
        // 5000 mAh design capacity, estimates around 3700 mAh (74% retention -> FAIR)
        val sessions = (1..5).map { id ->
            createValidSession(id.toLong(), 3700f)
        }

        val estimate = BatteryHealthEstimator.computeHealthEstimate(
            sessions = sessions,
            verifiedDesignCapacityMah = 5000,
            hardwareCycleCount = null
        )

        assertNotNull(estimate.healthPercentage)
        assertEquals(74f, estimate.healthPercentage!!, 1.0f)
        assertEquals(BatteryHealthStatus.FAIR, estimate.condition)
        // Check fallback cycle count calculation
        assertEquals(MetricSource.ESTIMATED, estimate.cycleCountSource)
    }

    private fun createValidSession(id: Long, estimatedContribution: Float): ChargingSession {
        return ChargingSession(
            id = id,
            startTime = id * 10000L,
            endTime = id * 10000L + 3600000L,
            startPercentage = 20,
            endPercentage = 80,
            startChargeCounterUah = 1_000_000L,
            endChargeCounterUah = 3_850_000L,
            startTemperatureC = 27f,
            maxTemperatureC = 33f,
            avgTemperatureC = 30f,
            avgVoltageMv = 4150,
            source = ChargingSource.AC,
            isCompleted = true,
            estimatedCapacityContribution = estimatedContribution
        )
    }
}
