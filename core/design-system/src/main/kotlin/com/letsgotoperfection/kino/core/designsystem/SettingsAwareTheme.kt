package com.letsgotoperfection.kino.core.designsystem

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
// Settings API will be injected at app level

/**
 * Settings-aware theme that uses user preferences for theming
 * This version will be used at the app level with proper dependency injection
 */
@Composable
fun SettingsAwareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColors: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}


// Color Schemes
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1),
    secondary = Color(0xFF10B981),
    tertiary = Color(0xFFF59E0B),
    error = Color(0xFFEF4444),
    background = Color(0xFFF9FAFB),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onError = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1F2937),
    onSurface = Color(0xFF1F2937)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    secondary = Color(0xFF34D399),
    tertiary = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    background = Color(0xFF111827),
    surface = Color(0xFF1F2937),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onError = Color(0xFF000000),
    onBackground = Color(0xFFF9FAFB),
    onSurface = Color(0xFFF9FAFB)
)
