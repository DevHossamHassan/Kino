package com.letsgotoperfection.kino.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.letsgotoperfection.kino.feature.kanban.navigation.KanbanBoardRoute
import com.letsgotoperfection.kino.feature.media.navigation.MediaManagerRoute
import com.letsgotoperfection.kino.feature.notes.navigation.NotesListRoute
import com.letsgotoperfection.kino.feature.recurringtasks.navigation.RecurringTasksListRoute

/**
 * Modern bottom navigation bar with updated Material 3 design
 *
 * Features:
 * - Filled/outlined icon variants for selected state
 * - Smooth transitions
 * - Proper state management
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val startDestination = KanbanBoardRoute

    NavigationBar(modifier = modifier) {
        // Kanban Board Tab - Using ViewKanban icons for better representation
        NavigationBarItem(
            selected = currentRoute?.startsWith("kanban") == true ||
                    currentRoute?.startsWith("task") == true,
            onClick = {
                navController.navigate(KanbanBoardRoute) {
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
                    imageVector = if (currentRoute?.startsWith("kanban") == true ||
                        currentRoute?.startsWith("task") == true) {
                        Icons.Filled.ViewKanban
                    } else {
                        Icons.Outlined.ViewKanban
                    },
                    contentDescription = "Kanban Board"
                )
            },
            label = { Text("Board") }
        )

        // Notes Tab - Using Description icons for notes
        NavigationBarItem(
            selected = currentRoute?.startsWith("note") == true,
            onClick = {
                navController.navigate(NotesListRoute) {
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
                    imageVector = if (currentRoute?.startsWith("note") == true) {
                        Icons.Filled.Description
                    } else {
                        Icons.Outlined.Description
                    },
                    contentDescription = "Notes"
                )
            },
            label = { Text("Notes") }
        )

        // Media Tab - Using Collections for media gallery
        NavigationBarItem(
            selected = currentRoute?.startsWith("media") == true,
            onClick = {
                navController.navigate(MediaManagerRoute) {
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
                    imageVector = if (currentRoute?.startsWith("media") == true) {
                        Icons.Filled.Collections
                    } else {
                        Icons.Outlined.Collections
                    },
                    contentDescription = "Media"
                )
            },
            label = { Text("Media") }
        )

        // Recurring Tasks Tab - Using Autorenew for recurring concept
        NavigationBarItem(
            selected = currentRoute?.startsWith("recurring_tasks") == true,
            onClick = {
                navController.navigate(RecurringTasksListRoute) {
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
                    imageVector = if (currentRoute?.startsWith("recurring_tasks") == true) {
                        Icons.Filled.Autorenew
                    } else {
                        Icons.Outlined.Autorenew
                    },
                    contentDescription = "Recurring Tasks"
                )
            },
            label = { Text("Recurring") }
        )
    }
}