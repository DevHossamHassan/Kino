package com.letsgotoperfection.kino.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.letsgotoperfection.kino.feature.kanban.navigation.KanbanBoardRoute
import com.letsgotoperfection.kino.feature.kanban.navigation.kanbanGraph
import com.letsgotoperfection.kino.feature.kanban.navigation.CreateTaskRoute
import com.letsgotoperfection.kino.feature.media.navigation.MediaViewerRoute
import com.letsgotoperfection.kino.feature.media.navigation.mediaGraph
import com.letsgotoperfection.kino.feature.notes.navigation.NoteDetailRoute
import com.letsgotoperfection.kino.feature.notes.navigation.NoteEditorRoute
import com.letsgotoperfection.kino.feature.notes.navigation.notesGraph
import com.letsgotoperfection.kino.feature.recurringtasks.navigation.CreateRecurringTaskRoute
import com.letsgotoperfection.kino.feature.recurringtasks.navigation.EditRecurringTaskRoute
import com.letsgotoperfection.kino.feature.recurringtasks.navigation.RecurringTaskInstancesRoute
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskInstancesScreenApi
import com.letsgotoperfection.kino.feature.recurringtasks.navigation.recurringTasksGraph
import com.letsgotoperfection.kino.feature.settings.navigation.SettingsRoute
import com.letsgotoperfection.kino.feature.settings.navigation.settingsGraph
import com.letsgotoperfection.kino.feature.taskdetail.navigation.TaskDetailRoute
import com.letsgotoperfection.kino.feature.taskdetail.navigation.taskDetailGraph
import com.letsgotoperfection.kino.core.designsystem.component.TaskCreationDialog
import com.letsgotoperfection.kino.ui.BottomNavigationBar
import kotlinx.coroutines.launch

/**
 * Main navigation host for the Kino app using type-safe navigation.
 * Contains the single app-wide Scaffold with bottom navigation and snackbar.
 */
@Composable
fun KinoNavHost(
    navController: NavHostController = rememberNavController()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val taskCreationViewModel: TaskCreationViewModel = hiltViewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = KanbanBoardRoute,
            modifier = Modifier.padding(paddingValues)
        ) {
        // Kanban Feature
        kanbanGraph(
            onTaskClick = { taskId ->
                navController.navigate(TaskDetailRoute(taskId))
            },
            onCreateTask = {
                navController.navigate(CreateTaskRoute())
            },
            onNavigateToSettings = {
                navController.navigate(SettingsRoute)
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
        
        // Task Detail Feature
        taskDetailGraph(
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToMedia = { mediaId ->
                navController.navigate(MediaViewerRoute(mediaId))
            },
            onNavigateToNote = { noteId ->
                navController.navigate(NoteDetailRoute(noteId))
            }
        )
        
        // Notes Feature
        notesGraph(
            onNoteClick = { noteId ->
                navController.navigate(NoteDetailRoute(noteId))
            },
            onNavigateToEditor = { noteId ->
                navController.navigate(NoteEditorRoute(noteId))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
        
        // Media Feature
        mediaGraph(
            onNavigateToTask = { taskId ->
                navController.navigate(TaskDetailRoute(taskId))
            },
            onNavigateToNote = { noteId ->
                navController.navigate(NoteDetailRoute(noteId))
            },
            onNavigateToViewer = { mediaId ->
                navController.navigate(MediaViewerRoute(mediaId))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
        
        // Settings Feature
        settingsGraph(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
        
        // Recurring Tasks Feature
        recurringTasksGraph(
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToCreate = {
                navController.navigate(CreateRecurringTaskRoute)
            },
            onNavigateToEdit = { recurringTaskId ->
                navController.navigate(EditRecurringTaskRoute(recurringTaskId))
            },
            onNavigateToInstances = { recurringTaskId ->
                navController.navigate(RecurringTaskInstancesRoute(recurringTaskId))
            }
        )

        dialog<CreateTaskRoute> {
            TaskCreationDialog(
                isVisible = true,
                onDismiss = { navController.popBackStack() },
                onTaskCreated = { draft ->
                    coroutineScope.launch {
                        taskCreationViewModel.createTask(draft)
                            .onSuccess { newTaskId ->
                                snackbarHostState.showSnackbar("Task created")
                                navController.popBackStack()
                                navController.navigate(TaskDetailRoute(newTaskId))
                            }
                            .onFailure { error ->
                                snackbarHostState.showSnackbar(
                                    error.message ?: "Failed to create task"
                                )
                            }
                    }
                }
            )
        }

        composable<RecurringTaskInstancesRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<RecurringTaskInstancesRoute>()
            RecurringTaskInstancesScreenApi(
                recurringTaskId = route.recurringTaskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
    }
}
