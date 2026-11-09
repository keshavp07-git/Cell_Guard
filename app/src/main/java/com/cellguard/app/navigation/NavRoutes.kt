package com.cellguard.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Overview : Screen("overview", "Overview", Icons.Default.BatteryChargingFull)
    data object BatteryHealth : Screen("battery_health", "Health", Icons.Default.Favorite)
    data object Analytics : Screen("analytics", "Analytics", Icons.Default.Insights)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val bottomNavScreens = listOf(
    Screen.Overview,
    Screen.BatteryHealth,
    Screen.Analytics,
    Screen.Settings
)
