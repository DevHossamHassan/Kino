package com.letsgotoperfection.kino.feature.settings.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.letsgotoperfection.kino.feature.settings.internal.presentation.ui.SettingsScreen

/**
 * Public API composables for Settings feature.
 * 
 * These composables expose the internal UI screens to the navigation module
 * while maintaining proper modularization boundaries.
 */

/**
 * Settings Screen - Public API
 */
@Composable
fun SettingsScreenApi(
    onNavigateBack: () -> Unit
) {
    SettingsScreen(
        onNavigateBack = onNavigateBack
    )
}
