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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.letsgotoperfection.kino.core.designsystem.*

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,
    
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnSecondary,
    
    tertiary = Accent,
    onTertiary = OnAccent,
    
    error = Error,
    onError = OnError,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,
    
    background = Color(0xFF0F0F0F), // Dark background
    onBackground = Color(0xFFE5E5E5), // Light text on dark
    surface = Color(0xFF1C1C1C), // Dark surface
    onSurface = Color(0xFFE5E5E5), // Light text on dark surface
    surfaceVariant = Color(0xFF2A2A2A), // Dark surface variant
    onSurfaceVariant = Color(0xFFB3B3B3), // Muted text on dark surface
    
    outline = Color(0xFF404040), // Dark outline
    outlineVariant = Color(0xFF2A2A2A), // Dark outline variant
    
    scrim = Overlay, // Dark overlay
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Primary.copy(alpha = 0.1f),
    onPrimaryContainer = Primary,
    
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = Secondary.copy(alpha = 0.1f),
    onSecondaryContainer = Secondary,
    
    tertiary = Accent,
    onTertiary = OnAccent,
    
    error = Error,
    onError = OnError,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,
    
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    
    outline = Border,
    outlineVariant = Border.copy(alpha = 0.5f),
    
    scrim = OverlayLight, // Light overlay
)

@Composable
fun KinoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
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
            // Modern edge-to-edge theming approach
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