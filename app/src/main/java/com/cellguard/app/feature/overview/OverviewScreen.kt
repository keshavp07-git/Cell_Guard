package com.cellguard.app.feature.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cellguard.app.core.designsystem.components.AppleCard
import com.cellguard.app.core.designsystem.components.BatteryVisualizer
import com.cellguard.app.core.designsystem.components.MetricRow
import com.cellguard.app.core.designsystem.components.SectionHeader
import com.cellguard.app.core.designsystem.components.StatusBadge
import com.cellguard.app.core.designsystem.theme.LocalCellGuardColors
import com.cellguard.app.data.preferences.TemperatureUnit
import com.cellguard.app.domain.model.BatteryHealthStatus
import com.cellguard.app.domain.model.InsightType
import java.util.Locale

@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel,
    onNavigateToHealth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalCellGuardColors.current
    val scrollState = rememberScrollState()

    val metrics = uiState.metrics
    val settings = uiState.settings
    val capabilities = uiState.capabilities
    val health = uiState.healthEstimate

    val formattedTemp = if (settings.temperatureUnit == TemperatureUnit.FAHRENHEIT) {
        val f = (metrics.temperatureCelsius * 9f / 5f) + 32f
        String.format(Locale.US, "%.1f°F", f)
    } else {
        String.format(Locale.US, "%.1f°C", metrics.temperatureCelsius)
    }

    val statusText = buildString {
        append(metrics.status.displayName())
        if (metrics.isCharging) {
            append(" • ")
            append(metrics.source.displayName())
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Screen Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CellGuard",
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 30.sp),
                        color = colors.textPrimary
                    )
                    Text(
                        text = "${capabilities.manufacturer} ${capabilities.model}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                StatusBadge(
                    text = if (metrics.isCharging) "Charging" else "On Battery",
                    color = if (metrics.isCharging) colors.accentGreen else colors.accentBlue
                )
            }

            // Animated Apple-inspired Battery Visualizer
            BatteryVisualizer(
                percentage = metrics.level,
                isCharging = metrics.isCharging,
                statusText = statusText
            )

            // Smart Insight Card if available
            val topInsight = uiState.insights.firstOrNull()
            if (topInsight != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val insightColor = when (topInsight.type) {
                    InsightType.WARNING -> colors.accentOrange
                    InsightType.POSITIVE -> colors.accentGreen
                    InsightType.INFO -> colors.accentBlue
                    InsightType.TIP -> colors.accentYellow
                }
                AppleCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = colors.surfaceElevated,
                    borderColor = insightColor.copy(alpha = 0.4f)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(insightColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (topInsight.type == InsightType.WARNING) Icons.Default.Warning else Icons.Default.Info,
                                contentDescription = null,
                                tint = insightColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = topInsight.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = topInsight.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }

            // Battery Health Summary Card (Clickable to Health Screen)
            SectionHeader(title = "Battery Condition")
            AppleCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToHealth
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.accentGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = colors.accentGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Battery Health & Degradation",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (health?.healthPercentage != null) {
                                    "${String.format(Locale.US, "%.0f%%", health.healthPercentage)} Maximum Capacity"
                                } else {
                                    "Collecting calibration data..."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Details",
                        tint = colors.textTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Live Diagnostics Card
            SectionHeader(title = "Live Hardware Readings")
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                MetricRow(
                    label = "Temperature",
                    value = formattedTemp,
                    icon = Icons.Default.Thermostat,
                    iconTint = if (metrics.temperatureCelsius >= 40f) colors.accentRed else colors.accentOrange,
                    isAvailable = true,
                    showDivider = true
                )

                MetricRow(
                    label = "Voltage",
                    value = String.format(Locale.US, "%.2f", metrics.voltageMv / 1000f),
                    unit = "V",
                    subtext = "${metrics.voltageMv} mV",
                    icon = Icons.Default.ElectricBolt,
                    iconTint = colors.accentYellow,
                    isAvailable = metrics.voltageMv > 0,
                    showDivider = true
                )

                val currentMa = metrics.normalizedCurrentMa()
                MetricRow(
                    label = if (metrics.isCharging) "Charging Current" else "Discharge Rate",
                    value = currentMa?.let { String.format(Locale.US, "%.0f", it) } ?: "",
                    unit = "mA",
                    subtext = metrics.calculatedWattage()?.let { String.format(Locale.US, "%.1f W", it) },
                    icon = Icons.Default.Power,
                    iconTint = colors.accentGreen,
                    isAvailable = currentMa != null,
                    showDivider = true
                )

                MetricRow(
                    label = "Battery Technology",
                    value = metrics.technology,
                    icon = Icons.Default.Memory,
                    iconTint = colors.accentBlue,
                    isAvailable = metrics.technology.isNotBlank(),
                    showDivider = false
                )
            }

            // Hardware Transparency Card
            SectionHeader(title = "Hardware Interface Support")
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Android Hardware Capabilities",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "CellGuard reports true hardware metrics without fabricating missing values.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                CapabilityChipRow(
                    label = "Charge Counter (µAh)",
                    supported = capabilities.supportsChargeCounter,
                    showDivider = true
                )
                CapabilityChipRow(
                    label = "Instantaneous Current (µA)",
                    supported = capabilities.supportsCurrentNow,
                    showDivider = true
                )
                CapabilityChipRow(
                    label = "Average Current (µA)",
                    supported = capabilities.supportsCurrentAverage,
                    showDivider = true
                )
                CapabilityChipRow(
                    label = "Android 14+ Cycle Counter",
                    supported = capabilities.supportsHardwareCycleCount,
                    showDivider = true
                )
                CapabilityChipRow(
                    label = "System Power Profile",
                    supported = capabilities.supportsDesignCapacityProfile,
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun CapabilityChipRow(
    label: String,
    supported: Boolean,
    showDivider: Boolean = true
) {
    val colors = LocalCellGuardColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            if (supported) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(colors.accentGreen.copy(alpha = 0.14f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = colors.accentGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Supported",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        ),
                        color = colors.accentGreen
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(colors.surfaceSecondary.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Unsupported",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.5.sp
                        ),
                        color = colors.textTertiary
                    )
                }
            }
        }
        if (showDivider) {
            HorizontalDivider(
                color = colors.border,
                thickness = 0.6.dp
            )
        }
    }
}
