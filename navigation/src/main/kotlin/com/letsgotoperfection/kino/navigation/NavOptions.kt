package com.letsgotoperfection.kino.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder

/**
 * Reusable navigation options for common navigation patterns
 */

/**
 * Navigation options for bottom navigation that preserves state
 */
fun NavController.createBottomNavOptions(): NavOptions {
    return NavOptions.Builder()
        .setPopUpTo(graph.startDestinationId, true)
        .setLaunchSingleTop(true)
        .setRestoreState(true)
        .build()
}

/**
 * Navigation options for bottom navigation (builder pattern)
 */
fun NavOptionsBuilder.bottomNavOptions() {
    popUpTo(0) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

/**
 * Navigation options for back navigation
 */
fun NavController.createBackNavOptions(): NavOptions {
    return NavOptions.Builder()
        .setPopUpTo(graph.startDestinationId, inclusive = false)
        .build()
}

/**
 * Navigation options for back navigation (builder pattern)
 */
fun NavOptionsBuilder.backNavOptions() {
    popUpTo(0) {
        inclusive = false
    }
}