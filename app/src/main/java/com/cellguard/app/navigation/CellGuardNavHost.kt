package com.cellguard.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cellguard.app.core.designsystem.theme.LocalCellGuardColors
import com.cellguard.app.di.AppContainer
import com.cellguard.app.di.ViewModelFactory
import com.cellguard.app.feature.analytics.AnalyticsScreen
import com.cellguard.app.feature.analytics.AnalyticsViewModel
import com.cellguard.app.feature.batteryhealth.BatteryHealthScreen
import com.cellguard.app.feature.batteryhealth.BatteryHealthViewModel
import com.cellguard.app.feature.overview.OverviewScreen
import com.cellguard.app.feature.overview.OverviewViewModel
import com.cellguard.app.feature.settings.SettingsScreen
import com.cellguard.app.feature.settings.SettingsViewModel

@Composable
fun CellGuardMainApp(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    val colors = LocalCellGuardColors.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Overview.route
    val viewModelFactory = ViewModelFactory(appContainer)

    Scaffold(
        containerColor = colors.bg,
        bottomBar = {
            NavigationBar(
                containerColor = colors.surfaceElevated,
                tonalElevation = 0.dp
            ) {
                bottomNavScreens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.accentGreen,
                            selectedTextColor = colors.accentGreen,
                            unselectedIconColor = colors.textTertiary,
                            unselectedTextColor = colors.textTertiary,
                            indicatorColor = colors.accentGreen.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Overview.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Overview.route) {
                val vm: OverviewViewModel = viewModel(factory = viewModelFactory)
                OverviewScreen(
                    viewModel = vm,
                    onNavigateToHealth = {
                        navController.navigate(Screen.BatteryHealth.route)
                    }
                )
            }
            composable(Screen.BatteryHealth.route) {
                val vm: BatteryHealthViewModel = viewModel(factory = viewModelFactory)
                BatteryHealthScreen(viewModel = vm)
            }
            composable(Screen.Analytics.route) {
                val vm: AnalyticsViewModel = viewModel(factory = viewModelFactory)
                AnalyticsScreen(viewModel = vm)
            }
            composable(Screen.Settings.route) {
                val vm: SettingsViewModel = viewModel(factory = viewModelFactory)
                SettingsScreen(viewModel = vm)
            }
        }
    }
}
