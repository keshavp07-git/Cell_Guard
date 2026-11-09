package com.cellguard.app.feature.batteryhealth

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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cellguard.app.core.designsystem.components.AppleCard
import com.cellguard.app.core.designsystem.components.MetricRow
import com.cellguard.app.core.designsystem.components.SectionHeader
import com.cellguard.app.core.designsystem.components.StatusBadge
import com.cellguard.app.core.designsystem.theme.LocalCellGuardColors
import com.cellguard.app.domain.model.BatteryHealthStatus
import com.cellguard.app.domain.model.HealthConfidence
import com.cellguard.app.domain.model.MetricSource
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryHealthScreen(
    viewModel: BatteryHealthViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalCellGuardColors.current
    val scrollState = rememberScrollState()

    var showMethodologyDialog by remember { mutableStateOf(false) }

    val estimate = uiState.estimate
    val healthPct = estimate?.healthPercentage
    val designCap = uiState.settings.customDesignCapacityMah ?: uiState.currentMetrics.designCapacityMah
    val condition = estimate?.condition ?: BatteryHealthStatus.INSUFFICIENT_DATA
    val confidence = estimate?.confidence ?: HealthConfidence.NOT_ENOUGH_DATA

    val conditionColor = when (condition) {
        BatteryHealthStatus.EXCELLENT -> colors.accentGreen
        BatteryHealthStatus.GOOD -> colors.accentGreen
        BatteryHealthStatus.FAIR -> colors.accentYellow
        BatteryHealthStatus.DEGRADED -> colors.accentOrange
        BatteryHealthStatus.CRITICAL -> colors.accentRed
        BatteryHealthStatus.INSUFFICIENT_DATA -> colors.textTertiary
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Battery Health",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { showMethodologyDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Methodology",
                            tint = colors.accentBlue
                        )
                    }
                    IconButton(onClick = { viewModel.forceRecalculate() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recalculate",
                            tint = colors.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.bg
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Introductory Callout (Apple-inspired)
            Text(
                text = "Phone batteries, like all rechargeable batteries, are consumable components that become less effective as they chemically age. Learn how your battery is performing.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Section A: Maximum Capacity Card
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Maximum Capacity",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Estimated capacity relative to factory new",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                    if (healthPct != null) {
                        Text(
                            text = "${String.format(Locale.US, "%.0f", healthPct)}%",
                            style = MaterialTheme.typography.displayMedium,
                            color = conditionColor
                        )
                    } else {
                        StatusBadge(
                            text = "Calibrating",
                            color = colors.accentYellow
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "This is an honest algorithmic estimate of battery capacity relative to when it was new. Lower capacity results in fewer hours of usage between charges.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            // Section B: Battery Condition & Confidence
            SectionHeader(title = "Battery Condition")
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current Condition",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = condition.displayName(),
                            style = MaterialTheme.typography.titleMedium,
                            color = conditionColor
                        )
                    }
                    StatusBadge(
                        text = confidence.displayName(),
                        color = when (confidence) {
                            HealthConfidence.HIGH -> colors.accentGreen
                            HealthConfidence.MEDIUM -> colors.accentBlue
                            HealthConfidence.LOW -> colors.accentOrange
                            HealthConfidence.NOT_ENOUGH_DATA -> colors.textTertiary
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = estimate?.confidenceReason ?: "CellGuard is waiting for additional charging sessions to confirm capacity retention.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            // Section C, D, E, F: Detailed Capacity Metrics
            SectionHeader(title = "Capacity & Cycle Analytics")
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                MetricRow(
                    label = "Original Design Capacity",
                    value = designCap?.let { "$it mAh" } ?: "",
                    subtext = if (uiState.settings.customDesignCapacityMah != null) "Verified by user in Settings" else "Detected from system profile",
                    icon = Icons.Default.Shield,
                    iconTint = colors.accentBlue,
                    isAvailable = designCap != null,
                    showDivider = true
                )

                val estimatedFull = estimate?.estimatedFullCapacityMah
                MetricRow(
                    label = "Estimated Full Capacity",
                    value = estimatedFull?.let { String.format(Locale.US, "%.0f mAh", it) } ?: "",
                    subtext = "Calculated from ${estimate?.validSessionsCount ?: 0} charge cycles",
                    icon = Icons.Default.ElectricMeter,
                    iconTint = colors.accentGreen,
                    isAvailable = estimatedFull != null,
                    showDivider = true
                )

                val lossPct = estimate?.estimatedCapacityLossPct
                MetricRow(
                    label = "Estimated Degradation Loss",
                    value = lossPct?.let { String.format(Locale.US, "%.1f%%", it) } ?: "",
                    subtext = "Estimated chemical wear over time",
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    iconTint = if (lossPct != null && lossPct > 20f) colors.accentRed else colors.accentOrange,
                    isAvailable = lossPct != null,
                    showDivider = true
                )

                val cycles = estimate?.cycleCount
                val cycleSource = estimate?.cycleCountSource ?: MetricSource.UNSUPPORTED
                val cycleLabel = when (cycleSource) {
                    MetricSource.HARDWARE_REPORTED, MetricSource.SYSTEM_REPORTED -> "Android 14+ Hardware Counter"
                    MetricSource.ESTIMATED -> "Calculated from logged charge sessions"
                    MetricSource.UNSUPPORTED -> "Not supported by hardware"
                }

                MetricRow(
                    label = "Battery Cycle Count",
                    value = cycles?.toString() ?: "",
                    subtext = cycleLabel,
                    icon = Icons.Default.Repeat,
                    iconTint = colors.accentYellow,
                    isAvailable = cycles != null,
                    showDivider = false
                )
            }

            // Methodology Action Link
            Spacer(modifier = Modifier.height(16.dp))
            AppleCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showMethodologyDialog = true }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.accentBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = colors.accentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "How Health is Calculated",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Learn about our honest algorithmic approach and Android limitations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (showMethodologyDialog) {
        MethodologyDialog(onDismiss = { showMethodologyDialog = false })
    }
}

@Composable
fun MethodologyDialog(onDismiss: () -> Unit) {
    val colors = LocalCellGuardColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "CellGuard Health Methodology",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                color = colors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "1. Real Hardware Charge Counter",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.accentGreen
                )
                Text(
                    text = "When available, CellGuard reads the hardware microampere-hour (µAh) charge counter directly from the kernel battery driver during charging sessions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Text(
                    text = "2. Mathematical Delta Integration",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.accentGreen
                )
                Text(
                    text = "Capacity is derived by measuring the charge injected (ΔQ) across a percentage gain (ΔP):\nEstimated Capacity = (ΔQ / ΔP) × 100",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Text(
                    text = "3. Outlier Safeguards & Honest Estimation",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.accentGreen
                )
                Text(
                    text = "Android does not provide a universal OEM health metric. CellGuard never fabricates numbers or claims 100% health when data is absent. We discard erratic counter jumps and require at least 3 qualifying sessions before scoring confidence.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got It", color = colors.accentGreen)
            }
        },
        containerColor = colors.surfaceElevated
    )
}
