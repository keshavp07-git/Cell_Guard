package com.cellguard.app.feature.settings

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cellguard.app.core.designsystem.components.AppleCard
import com.cellguard.app.core.designsystem.components.CellGuardButton
import com.cellguard.app.core.designsystem.components.SectionHeader
import com.cellguard.app.core.designsystem.theme.LocalCellGuardColors
import com.cellguard.app.core.designsystem.theme.ThemeMode
import com.cellguard.app.data.preferences.TemperatureUnit
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val colors = LocalCellGuardColors.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showCapacityDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.bg)
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
            // Section 1: Appearance
            SectionHeader(title = "Appearance")
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Interface Theme",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceSecondary)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val themes = listOf(
                        Pair(ThemeMode.SYSTEM, "System"),
                        Pair(ThemeMode.DARK, "Dark"),
                        Pair(ThemeMode.LIGHT, "Light")
                    )
                    themes.forEach { (mode, label) ->
                        val isSelected = settings.themeMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) colors.accentGreen else Color.Transparent)
                                .clickable { viewModel.setThemeMode(mode) }
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

            // Section 2: Units & Calibration
            SectionHeader(title = "Units & Battery Capacity")
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                // Temperature unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Temperature Unit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceSecondary)
                            .padding(2.dp)
                    ) {
                        val isC = settings.temperatureUnit == TemperatureUnit.CELSIUS
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isC) colors.accentGreen else Color.Transparent)
                                .clickable { viewModel.setTemperatureUnit(TemperatureUnit.CELSIUS) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("°C", color = if (isC) Color.Black else colors.textSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!isC) colors.accentGreen else Color.Transparent)
                                .clickable { viewModel.setTemperatureUnit(TemperatureUnit.FAHRENHEIT) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("°F", color = if (!isC) Color.Black else colors.textSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Design Capacity verification
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCapacityDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Verified Design Capacity",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = settings.customDesignCapacityMah?.let { "$it mAh (Custom)" } ?: "Auto-detect from system",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.accentBlue
                        )
                    }
                    Text(
                        text = "Edit",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.accentGreen
                    )
                }
            }

            // Section 3: Smart Notifications
            SectionHeader(title = "Notifications & Alerts")
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                NotificationToggleRow(
                    title = "Elevated Temperature Warning",
                    subtitle = "Alert when battery exceeds ${settings.highTempThresholdC}°C",
                    checked = settings.notifyHighTemp,
                    onCheckedChange = { viewModel.setNotifyHighTemp(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                NotificationToggleRow(
                    title = "Full Charge Alert (100%)",
                    subtitle = "Notify when battery reaches 100% to unplug charger",
                    checked = settings.notifyFullCharge,
                    onCheckedChange = { viewModel.setNotifyFullCharge(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                NotificationToggleRow(
                    title = "Low Battery Reminder (15%)",
                    subtitle = "Alert before battery reaches deep discharge",
                    checked = settings.notifyLowBattery,
                    onCheckedChange = { viewModel.setNotifyLowBattery(it) }
                )
            }

            // Section 4: Data Management & Export
            SectionHeader(title = "Data Management")
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                val csv = viewModel.exportCsvData()
                                shareCsvData(context, csv)
                            }
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = colors.accentGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Export Battery History (CSV)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Save or share full snapshot and session records",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showClearDataDialog = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = colors.accentRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Clear Local Battery Data",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.accentRed
                        )
                        Text(
                            text = "Erase all snapshots, charging sessions, and health estimates",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // Section 5: About & Privacy
            SectionHeader(title = "About CellGuard")
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPrivacyDialog = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = colors.accentBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Privacy & Offline Architecture",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "No analytics, no cloud, no background battery drain",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Version",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "1.0.0 (Production)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Capacity Edit Dialog
    if (showCapacityDialog) {
        var inputCapacity by remember {
            mutableStateOf(settings.customDesignCapacityMah?.toString() ?: "")
        }
        AlertDialog(
            onDismissRequest = { showCapacityDialog = false },
            title = { Text("Set Rated Battery Capacity", color = colors.textPrimary) },
            text = {
                Column {
                    Text(
                        text = "Enter your phone manufacturer's rated design capacity in mAh (usually found in phone specs e.g. 5000):",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputCapacity,
                        onValueChange = { inputCapacity = it.filter { char -> char.isDigit() } },
                        label = { Text("Design Capacity (mAh)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cap = inputCapacity.toIntOrNull()
                        viewModel.setCustomDesignCapacity(cap)
                        showCapacityDialog = false
                    }
                ) {
                    Text("Save", color = colors.accentGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCapacityDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surfaceElevated
        )
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Erase All Battery Records?", color = colors.accentRed) },
            text = {
                Text(
                    text = "This will permanently delete all stored snapshots, charging session logs, and calibration calculations. This action cannot be undone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                    }
                ) {
                    Text("Erase Everything", color = colors.accentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surfaceElevated
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("CellGuard Privacy Charter", color = colors.textPrimary) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "• 100% Offline by Design\nCellGuard contains zero internet permissions. Not a single byte ever leaves your phone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "• No Trackers or SDKs\nNo Google Analytics, Firebase, Facebook SDK, or third-party ad networks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "• Local Storage Only\nAll historical measurements are stored in a private SQLite database inside the app's sandboxed storage.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", color = colors.accentGreen)
                }
            },
            containerColor = colors.surfaceElevated
        )
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalCellGuardColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = colors.accentGreen,
                uncheckedTrackColor = colors.surfaceSecondary
            )
        )
    }
}

private fun shareCsvData(context: Context, csvData: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_SUBJECT, "CellGuard Battery Intelligence Export")
        putExtra(Intent.EXTRA_TEXT, csvData)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(Intent.createChooser(intent, "Share Battery Records (CSV)"))
}
