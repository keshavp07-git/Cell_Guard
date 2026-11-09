package com.cellguard.app.domain.repository

import com.cellguard.app.domain.model.BatterySnapshot
import com.cellguard.app.domain.model.ChargingSession
import kotlinx.coroutines.flow.Flow

enum class HistoryTimeRange {
    HOURS_24,
    DAYS_7,
    DAYS_30,
    ALL
}

interface HistoryRepository {
    fun getSnapshotsForRange(range: HistoryTimeRange): Flow<List<BatterySnapshot>>
    fun getAllSessions(): Flow<List<ChargingSession>>
    suspend fun recordSnapshot(snapshot: BatterySnapshot)
    suspend fun exportHistoryToCsv(): String
    suspend fun clearAllHistory()
}
