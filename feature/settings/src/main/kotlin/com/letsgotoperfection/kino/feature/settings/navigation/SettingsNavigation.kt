package com.letsgotoperfection.kino.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.letsgotoperfection.kino.feature.settings.api.SettingsScreenApi
import kotlinx.serialization.Serializable

/**
 * Settings feature routes - Type-safe navigation
 */
@Serializable
object SettingsRoute

/**
 * Settings navigation graph
 * Exposes composable screens for app module to wire
 */
fun NavGraphBuilder.settingsGraph(
    onNavigateBack: () -> Unit
) {
    composable<SettingsRoute> {
        SettingsScreenApi(
            onNavigateBack = onNavigateBack
        )
    }
}

/**
 * Deep link patterns for settings feature
 */
object SettingsDeepLinks {
    const val SETTINGS = "kino://app/settings"
    
    fun createSettingsDeepLink() = "kino://app/settings"
}
