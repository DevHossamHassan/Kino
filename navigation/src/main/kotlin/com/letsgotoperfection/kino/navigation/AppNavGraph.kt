package com.letsgotoperfection.kino.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.letsgotoperfection.kino.navigation.ui.KanbanBoardRoute
import com.letsgotoperfection.kino.feature.recurringtasks.api.CreateRecurringTaskScreenApi
import com.letsgotoperfection.kino.feature.recurringtasks.api.EditRecurringTaskScreenApi
import com.letsgotoperfection.kino.feature.media.MediaManagerScreen
import com.letsgotoperfection.kino.feature.media.MediaViewerScreen
import com.letsgotoperfection.kino.feature.notes.NoteEditorScreen
import com.letsgotoperfection.kino.feature.notes.NotesListScreen
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksListScreenApi
import com.letsgotoperfection.kino.feature.settings.api.SettingsScreenApi
import com.letsgotoperfection.kino.navigation.ui.CreateTaskDialogRoute
import com.letsgotoperfection.kino.navigation.ui.NoteDetailScreen
import com.letsgotoperfection.kino.navigation.ui.TaskDetailScreen

/**
 * Main navigation destinations for the Kino app.
 */
object AppDestinations {
    // Main tabs
    const val KANBAN = "kanban"
    const val NOTES = "notes"
    const val MEDIA = "media"
    const val SETTINGS = "settings"
    const val RECURRING_TASKS = "recurring_tasks"

    // Detail screens
    const val TASK_DETAIL = "task/{taskId}"
    const val NOTE_DETAIL = "note/{noteId}"
    const val NOTE_EDITOR = "note/editor?noteId={noteId}"
    const val MEDIA_VIEWER = "media/viewer/{mediaId}"
    const val CREATE_TASK = "task/create"
    const val CREATE_RECURRING_TASK = "recurring_tasks/create"
    const val EDIT_RECURRING_TASK = "recurring_tasks/edit/{recurringTaskId}"

    // Route builders
    fun taskDetailRoute(taskId: String) = "task/$taskId"
    fun noteDetailRoute(noteId: String) = "note/$noteId"
    fun editRecurringTaskRoute(recurringTaskId: String) = "recurring_tasks/edit/$recurringTaskId"
    fun noteEditorRoute(noteId: String? = null): String =
        if (noteId.isNullOrBlank()) "note/editor" else "note/editor?noteId=$noteId"
    fun mediaViewerRoute(mediaId: String) = "media/viewer/$mediaId"
}

/**
 * Main navigation graph for the Kino app.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.KANBAN,
        modifier = modifier
    ) {
        composable(AppDestinations.KANBAN) {
            KanbanBoardRoute(
                onTaskClick = { taskId ->
                    navController.navigate(AppDestinations.taskDetailRoute(taskId))
                },
                onCreateTask = {
                    navController.navigate(AppDestinations.CREATE_TASK)
                }
            )
        }

        dialog(AppDestinations.CREATE_TASK) {
            CreateTaskDialogRoute(navController = navController)
        }

        composable(
            route = AppDestinations.TASK_DETAIL,
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "kino://app/task/{taskId}" }
            )
        ) { backStackEntry ->
            val taskId = requireNotNull(backStackEntry.arguments?.getString("taskId"))
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMedia = { mediaId ->
                    navController.navigate(AppDestinations.mediaViewerRoute(mediaId))
                }
            )
        }

        composable(AppDestinations.NOTES) {
            NotesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNoteDetail = { noteId ->
                    navController.navigate(AppDestinations.noteDetailRoute(noteId))
                },
                onNavigateToNoteEditor = { noteId ->
                    navController.navigate(AppDestinations.noteEditorRoute(noteId))
                }
            )
        }

        composable(
            route = AppDestinations.NOTE_DETAIL,
            arguments = listOf(
                navArgument("noteId") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "kino://app/note/{noteId}" }
            )
        ) { backStackEntry ->
            val noteId = requireNotNull(backStackEntry.arguments?.getString("noteId"))
            NoteDetailScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestinations.NOTE_EDITOR,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            NoteEditorScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.MEDIA) {
            MediaManagerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTask = { taskId ->
                    navController.navigate(AppDestinations.taskDetailRoute(taskId))
                },
                onNavigateToNote = { noteId ->
                    navController.navigate(AppDestinations.noteDetailRoute(noteId))
                },
                onNavigateToViewer = { mediaId ->
                    navController.navigate(AppDestinations.mediaViewerRoute(mediaId))
                }
            )
        }

        composable(
            route = AppDestinations.MEDIA_VIEWER,
            arguments = listOf(
                navArgument("mediaId") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "kino://app/media/viewer/{mediaId}" }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("mediaId")
                ?: throw IllegalArgumentException("mediaId is required")
            MediaViewerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTask = { taskId ->
                    navController.navigate(AppDestinations.taskDetailRoute(taskId))
                },
                onNavigateToNote = { noteId ->
                    navController.navigate(AppDestinations.noteDetailRoute(noteId))
                }
            )
        }

        composable(AppDestinations.SETTINGS) {
            SettingsScreenApi(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.RECURRING_TASKS) {
            RecurringTasksListScreenApi(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = {
                    navController.navigate(AppDestinations.CREATE_RECURRING_TASK)
                },
                onNavigateToEdit = { recurringTaskId ->
                    navController.navigate(AppDestinations.editRecurringTaskRoute(recurringTaskId))
                },
                onNavigateToInstances = { recurringTaskId ->
                    // TODO: Implement instances navigation
                    // For now, stay on the list screen
                }
            )
        }

        composable(AppDestinations.CREATE_RECURRING_TASK) {
            CreateRecurringTaskScreenApi(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = AppDestinations.EDIT_RECURRING_TASK,
            arguments = listOf(
                navArgument("recurringTaskId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val recurringTaskId = requireNotNull(backStackEntry.arguments?.getString("recurringTaskId"))
            EditRecurringTaskScreenApi(
                recurringTaskId = recurringTaskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
