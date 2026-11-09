package com.cellguard.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cellguard.app.core.database.entity.BatterySnapshotEntity
import com.cellguard.app.core.database.entity.ChargingSessionEntity
import com.cellguard.app.core.database.entity.HealthEstimateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BatterySnapshotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: BatterySnapshotEntity): Long

    @Query("SELECT * FROM battery_snapshots WHERE timestamp >= :sinceTimestamp ORDER BY timestamp ASC")
    fun getSnapshotsSince(sinceTimestamp: Long): Flow<List<BatterySnapshotEntity>>

    @Query("SELECT * FROM battery_snapshots ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentSnapshots(limit: Int): List<BatterySnapshotEntity>

    @Query("DELETE FROM battery_snapshots WHERE timestamp < :beforeTimestamp")
    suspend fun deleteSnapshotsOlderThan(beforeTimestamp: Long)

    @Query("DELETE FROM battery_snapshots")
    suspend fun clearAll()
}

@Dao
interface ChargingSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChargingSessionEntity): Long

    @Query("SELECT * FROM charging_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<ChargingSessionEntity>>

    @Query("SELECT * FROM charging_sessions WHERE startTime >= :sinceTimestamp ORDER BY startTime DESC")
    fun getSessionsSince(sinceTimestamp: Long): Flow<List<ChargingSessionEntity>>

    @Query("SELECT * FROM charging_sessions WHERE isCompleted = 1 AND estimatedCapacityContribution IS NOT NULL ORDER BY startTime DESC")
    suspend fun getValidSessionsForEstimation(): List<ChargingSessionEntity>

    @Query("SELECT * FROM charging_sessions ORDER BY startTime DESC LIMIT 1")
    suspend fun getLatestSession(): ChargingSessionEntity?

    @Query("DELETE FROM charging_sessions WHERE startTime < :beforeTimestamp")
    suspend fun deleteSessionsOlderThan(beforeTimestamp: Long)

    @Query("DELETE FROM charging_sessions")
    suspend fun clearAll()
}

@Dao
interface HealthEstimateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimate(estimate: HealthEstimateEntity): Long

    @Query("SELECT * FROM health_estimates ORDER BY timestamp DESC LIMIT 1")
    fun getLatestEstimate(): Flow<HealthEstimateEntity?>

    @Query("SELECT * FROM health_estimates ORDER BY timestamp ASC")
    fun getAllEstimates(): Flow<List<HealthEstimateEntity>>

    @Query("DELETE FROM health_estimates")
    suspend fun clearAll()
}
