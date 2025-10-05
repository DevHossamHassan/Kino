package com.letsgotoperfection.kino.core.designsystem.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * Standard previews with light and dark themes
 */
@Preview(
    name = "Light",
    showBackground = true,
    backgroundColor = 0xFFF9FAFB
)
@Preview(
    name = "Dark",
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemePreviews

/**
 * Phone device previews with different screen sizes
 */
@Preview(
    name = "Phone - Small",
    device = "spec:width=360dp,height=640dp,dpi=160"
)
@Preview(
    name = "Phone - Medium",
    device = Devices.PIXEL_4
)
@Preview(
    name = "Phone - Large",
    device = Devices.PIXEL_4_XL
)
annotation class PhonePreviews

/**
 * Tablet device previews
 */
@Preview(
    name = "Tablet - 7 inch",
    device = "spec:width=600dp,height=1024dp,dpi=240"
)
@Preview(
    name = "Tablet - 10 inch",
    device = Devices.TABLET
)
annotation class TabletPreviews

/**
 * Font scale previews for accessibility
 */
@Preview(
    name = "Font Scale - Normal",
    fontScale = 1.0f
)
@Preview(
    name = "Font Scale - Large",
    fontScale = 1.5f
)
@Preview(
    name = "Font Scale - Largest",
    fontScale = 2.0f
)
annotation class FontScalePreviews

/**
 * Locale previews for RTL support
 */
@Preview(
    name = "Locale - English",
    locale = "en"
)
@Preview(
    name = "Locale - Arabic (RTL)",
    locale = "ar"
)
@Preview(
    name = "Locale - German",
    locale = "de"
)
annotation class LocalePreviews

/**
 * Complete preview suite combining all variations
 */
@ThemePreviews
@FontScalePreviews
annotation class CompletePreviews

/**
 * Screen previews with proper configuration
 */
@Preview(
    name = "Screen - Light",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_4
)
@Preview(
    name = "Screen - Dark",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ScreenPreviews

/**
 * Component previews with different states
 */
@Preview(
    name = "Component - Light",
    showBackground = true
)
@Preview(
    name = "Component - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ComponentPreviews

/**
 * Interactive previews for testing interactions
 */
@Preview(
    name = "Interactive",
    showBackground = true
)
annotation class InteractivePreviews
