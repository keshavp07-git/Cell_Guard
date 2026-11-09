package com.cellguard.app.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Immutable
data class ExtendedColors(
    val bg: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceSecondary: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accentGreen: Color,
    val accentGreenGlow: Color,
    val accentOrange: Color,
    val accentOrangeGlow: Color,
    val accentRed: Color,
    val accentRedGlow: Color,
    val accentBlue: Color,
    val accentYellow: Color,
    val isDark: Boolean
)

val DarkExtendedColors = ExtendedColors(
    bg = DarkBackground,
    surface = DarkSurface,
    surfaceElevated = DarkSurfaceElevated,
    surfaceSecondary = DarkSurfaceSecondary,
    border = DarkBorder,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextTertiary,
    accentGreen = AppleGreenDark,
    accentGreenGlow = AppleGreenGlow,
    accentOrange = AppleOrangeDark,
    accentOrangeGlow = AppleOrangeGlow,
    accentRed = AppleRedDark,
    accentRedGlow = AppleRedGlow,
    accentBlue = AppleBlueDark,
    accentYellow = AppleYellowDark,
    isDark = true
)

val LightExtendedColors = ExtendedColors(
    bg = LightBackground,
    surface = LightSurface,
    surfaceElevated = LightSurfaceElevated,
    surfaceSecondary = LightSurfaceSecondary,
    border = LightBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    accentGreen = AppleGreen,
    accentGreenGlow = AppleGreenGlow,
    accentOrange = AppleOrange,
    accentOrangeGlow = AppleOrangeGlow,
    accentRed = AppleRed,
    accentRedGlow = AppleRedGlow,
    accentBlue = AppleBlue,
    accentYellow = AppleYellow,
    isDark = false
)

val LocalCellGuardColors = staticCompositionLocalOf { DarkExtendedColors }

private val DarkColorScheme = darkColorScheme(
    primary = AppleGreenDark,
    onPrimary = Color.Black,
    primaryContainer = AppleGreenGlow,
    onPrimaryContainer = AppleGreenDark,
    secondary = AppleBlueDark,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = AppleRedDark,
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = AppleGreen,
    onPrimary = Color.White,
    primaryContainer = AppleGreenGlow,
    onPrimaryContainer = AppleGreen,
    secondary = AppleBlue,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = AppleRed,
    onError = Color.White
)

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

@Composable
fun CellGuardTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = extendedColors.bg.toArgb()
                window.navigationBarColor = extendedColors.bg.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalCellGuardColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CellGuardTypography,
            shapes = CellGuardShapes,
            content = content
        )
    }
}
