package com.letsgotoperfection.kino.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Repeat
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
 * Bottom navigation bar for the main app tabs
 * 
 * Provides navigation between the main feature areas:
 * - Kanban Board (main task management)
 * - Notes (note-taking system)
 * - Media (media manager)
 * - Recurring Tasks (recurring task management)
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
        // Kanban Board Tab
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
                        Icons.Filled.Dashboard
                    } else {
                        Icons.Outlined.Dashboard
                    },
                    contentDescription = "Kanban Board"
                ) 
            },
            label = { Text("Board") }
        )
        
        // Notes Tab
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
                        Icons.Filled.Folder
                    } else {
                        Icons.Outlined.Folder
                    },
                    contentDescription = "Notes"
                ) 
            },
            label = { Text("Notes") }
        )
        
        // Media Tab
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
                        Icons.Filled.Photo
                    } else {
                        Icons.Outlined.Photo
                    },
                    contentDescription = "Media"
                ) 
            },
            label = { Text("Media") }
        )
        
        // Recurring Tasks Tab
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
                        Icons.Filled.Repeat
                    } else {
                        Icons.Outlined.Repeat
                    },
                    contentDescription = "Recurring Tasks"
                ) 
            },
            label = { Text("Recurring") }
        )
    }
}
