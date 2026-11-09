package com.cellguard.app.feature.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cellguard.app.core.designsystem.components.AppleCard
import com.cellguard.app.core.designsystem.components.InteractiveChart
import com.cellguard.app.core.designsystem.components.SectionHeader
import com.cellguard.app.core.designsystem.components.StatusBadge
import com.cellguard.app.core.designsystem.theme.LocalCellGuardColors
import com.cellguard.app.data.preferences.TemperatureUnit
import com.cellguard.app.domain.model.ChargingSession
import com.cellguard.app.domain.repository.HistoryTimeRange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalCellGuardColors.current

    val tempUnitLabel = if (uiState.settings.temperatureUnit == TemperatureUnit.FAHRENHEIT) "°F" else "°C"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Charging Analytics",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.bg)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Time Range Filter Segmented Control
            item {
                TimeRangeSelector(
                    selected = uiState.timeRange,
                    onSelect = { viewModel.setTimeRange(it) }
                )
            }

            // Summary Stats Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppleCard(
                        modifier = Modifier.weight(1f),
                        contentPadding = 12.dp
                    ) {
                        Text(
                            text = "Sessions",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.totalSessionsCount}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = colors.textPrimary
                        )
                    }

                    AppleCard(
                        modifier = Modifier.weight(1f),
                        contentPadding = 12.dp
                    ) {
                        Text(
                            text = "Avg Duration",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.avgDurationMinutes}m",
                            style = MaterialTheme.typography.headlineLarge,
                            color = colors.accentBlue
                        )
                    }

                    AppleCard(
                        modifier = Modifier.weight(1f),
                        contentPadding = 12.dp
                    ) {
                        Text(
                            text = "Avg Peak Heat",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val formattedPeak = if (uiState.settings.temperatureUnit == TemperatureUnit.FAHRENHEIT) {
                            val f = (uiState.avgMaxTempC * 9f / 5f) + 32f
                            String.format(Locale.US, "%.0f°", f)
                        } else {
                            String.format(Locale.US, "%.0f°", uiState.avgMaxTempC)
                        }
                        Text(
                            text = formattedPeak,
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (uiState.avgMaxTempC >= 40f) colors.accentRed else colors.accentOrange
                        )
                    }
                }
            }

            // 1. Battery Percentage Over Time Chart
            item {
                SectionHeader(title = "Battery Level History")
                AppleCard(modifier = Modifier.fillMaxWidth()) {
                    InteractiveChart(
                        points = uiState.percentageChartPoints,
                        lineColor = colors.accentGreen,
                        unit = "%",
                        minY = 0f,
                        maxY = 100f
                    )
                }
            }

            // 2. Battery Temperature Chart
            item {
                SectionHeader(title = "Battery Temperature Trend")
                AppleCard(modifier = Modifier.fillMaxWidth()) {
                    InteractiveChart(
                        points = uiState.temperatureChartPoints,
                        lineColor = colors.accentOrange,
                        unit = tempUnitLabel
                    )
                }
            }

            // 3. Battery Voltage Curve
            item {
                SectionHeader(title = "Voltage Discharge/Charge Curve")
                AppleCard(modifier = Modifier.fillMaxWidth()) {
                    InteractiveChart(
                        points = uiState.voltageChartPoints,
                        lineColor = colors.accentYellow,
                        unit = "V"
                    )
                }
            }

            // 4. Recent Charging Sessions List Header
            item {
                SectionHeader(title = "Logged Charging Sessions")
            }

            if (uiState.sessions.isEmpty()) {
                item {
                    AppleCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 24.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = colors.textTertiary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No charging sessions recorded yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Connect your device to a charger to automatically log sessions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            } else {
                items(uiState.sessions) { session ->
                    ChargingSessionCard(
                        session = session,
                        tempUnit = uiState.settings.temperatureUnit
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun TimeRangeSelector(
    selected: HistoryTimeRange,
    onSelect: (HistoryTimeRange) -> Unit
) {
    val colors = LocalCellGuardColors.current
    val ranges = listOf(
        Pair(HistoryTimeRange.HOURS_24, "24H"),
        Pair(HistoryTimeRange.DAYS_7, "7D"),
        Pair(HistoryTimeRange.DAYS_30, "30D"),
        Pair(HistoryTimeRange.ALL, "All")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceElevated)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ranges.forEach { (range, label) ->
            val isSelected = range == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) colors.accentGreen else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(range) }
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) Color.Black else colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ChargingSessionCard(
    session: ChargingSession,
    tempUnit: TemperatureUnit
) {
    val colors = LocalCellGuardColors.current
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM d • HH:mm", Locale.getDefault()) }

    AppleCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateFormatter.format(Date(session.startTime)),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary
            )
            StatusBadge(
                text = session.source.displayName(),
                color = colors.accentBlue,
                showDot = false
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${session.startPercentage}% → ${session.endPercentage}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.accentGreen
                )
                Text(
                    text = "+${session.percentageGained}% gained in ${session.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val peakTemp = if (tempUnit == TemperatureUnit.FAHRENHEIT) {
                    val f = (session.maxTemperatureC * 9f / 5f) + 32f
                    String.format(Locale.US, "%.1f°F", f)
                } else {
                    String.format(Locale.US, "%.1f°C", session.maxTemperatureC)
                }
                Text(
                    text = "Peak: $peakTemp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (session.maxTemperatureC >= 40f) colors.accentRed else colors.textPrimary
                )
                if (session.estimatedCapacityContribution != null) {
                    Text(
                        text = "Est. ${String.format(Locale.US, "%.0f", session.estimatedCapacityContribution)} mAh",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.accentGreen
                    )
                }
            }
        }
    }
}
