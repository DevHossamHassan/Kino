package com.letsgotoperfection.kino.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Shared bottom navigation used by modules that embed the navigation component directly.
 */
@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val startDestination = AppDestinations.KANBAN

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute?.startsWith("kanban") == true ||
                    currentRoute?.startsWith("task") == true,
            onClick = {
                navController.navigate(AppDestinations.KANBAN) {
                    popUpTo(startDestination) {
                        inclusive = false
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Kanban Board"
                )
            },
            label = { Text("Board") }
        )

        NavigationBarItem(
            selected = currentRoute?.startsWith("note") == true,
            onClick = {
                navController.navigate(AppDestinations.NOTES) {
                    popUpTo(startDestination) {
                        inclusive = false
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Notes"
                )
            },
            label = { Text("Notes") }
        )

        NavigationBarItem(
            selected = currentRoute?.startsWith("media") == true,
            onClick = {
                navController.navigate(AppDestinations.MEDIA) {
                    popUpTo(startDestination) {
                        inclusive = false
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Media"
                )
            },
            label = { Text("Media") }
        )

        NavigationBarItem(
            selected = currentRoute?.startsWith("recurring_tasks") == true,
            onClick = {
                navController.navigate(AppDestinations.RECURRING_TASKS) {
                    popUpTo(startDestination) {
                        inclusive = false
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Recurring Tasks"
                )
            },
            label = { Text("Recurring") }
        )
    }
}
