package com.cellguard.app.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cellguard.app.core.designsystem.components.ChartPoint
import com.cellguard.app.data.preferences.TemperatureUnit
import com.cellguard.app.data.preferences.UserPreferencesRepository
import com.cellguard.app.data.preferences.UserSettings
import com.cellguard.app.domain.model.BatteryMetrics
import com.cellguard.app.domain.model.BatterySnapshot
import com.cellguard.app.domain.model.ChargingSession
import com.cellguard.app.domain.repository.BatteryRepository
import com.cellguard.app.domain.repository.HistoryRepository
import com.cellguard.app.domain.repository.HistoryTimeRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val timeRange: HistoryTimeRange = HistoryTimeRange.HOURS_24,
    val percentageChartPoints: List<ChartPoint> = emptyList(),
    val temperatureChartPoints: List<ChartPoint> = emptyList(),
    val voltageChartPoints: List<ChartPoint> = emptyList(),
    val sessions: List<ChargingSession> = emptyList(),
    val totalSessionsCount: Int = 0,
    val avgDurationMinutes: Int = 0,
    val avgMaxTempC: Float = 0f,
    val currentMetrics: BatteryMetrics,
    val settings: UserSettings
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AnalyticsViewModel(
    private val batteryRepository: BatteryRepository,
    private val historyRepository: HistoryRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedRange = MutableStateFlow(HistoryTimeRange.HOURS_24)
    val selectedRange: StateFlow<HistoryTimeRange> = _selectedRange.asStateFlow()

    val uiState: StateFlow<AnalyticsUiState> = combine(
        _selectedRange.flatMapLatest { range ->
            historyRepository.getSnapshotsForRange(range)
        },
        historyRepository.getAllSessions(),
        batteryRepository.observeBatteryMetrics(),
        preferencesRepository.userSettings,
        _selectedRange
    ) { snapshots, sessions, metrics, settings, range ->
        val pctPoints = snapshots.map { s ->
            ChartPoint(timestamp = s.timestamp, value = s.percentage.toFloat())
        }

        val tempPoints = snapshots.map { s ->
            val tempVal = if (settings.temperatureUnit == TemperatureUnit.FAHRENHEIT) {
                (s.temperatureCelsius * 9f / 5f) + 32f
            } else {
                s.temperatureCelsius
            }
            ChartPoint(timestamp = s.timestamp, value = tempVal)
        }

        val voltPoints = snapshots.map { s ->
            ChartPoint(timestamp = s.timestamp, value = s.voltageMv / 1000f)
        }

        val completedSessions = sessions.filter { it.isCompleted }
        val avgDuration = if (completedSessions.isNotEmpty()) {
            completedSessions.map { it.durationMinutes }.average().toInt()
        } else 0

        val avgTemp = if (completedSessions.isNotEmpty()) {
            completedSessions.map { it.maxTemperatureC }.average().toFloat()
        } else 0f

        AnalyticsUiState(
            timeRange = range,
            percentageChartPoints = pctPoints,
            temperatureChartPoints = tempPoints,
            voltageChartPoints = voltPoints,
            sessions = sessions,
            totalSessionsCount = sessions.size,
            avgDurationMinutes = avgDuration,
            avgMaxTempC = avgTemp,
            currentMetrics = metrics,
            settings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState(
            currentMetrics = batteryRepository.getCurrentMetrics(),
            settings = UserSettings()
        )
    )

    fun setTimeRange(range: HistoryTimeRange) {
        _selectedRange.value = range
    }
}
