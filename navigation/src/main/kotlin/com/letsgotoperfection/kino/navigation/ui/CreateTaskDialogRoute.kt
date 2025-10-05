package com.letsgotoperfection.kino.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.kino.core.designsystem.component.TaskCreationDialog
import com.letsgotoperfection.kino.core.model.Task

/**
 * Dialog destination that wraps [TaskCreationDialog] and reports the created task through the
 * previous back stack entry's saved state.
 */
@Composable
fun CreateTaskDialogRoute(
    navController: NavController,
    viewModel: TaskCreationViewModel = hiltViewModel()
) {
    var isVisible by remember { mutableStateOf(true) }

    fun close(result: Task? = null) {
        val previousBackStackEntry = navController.previousBackStackEntry
        if (result != null && previousBackStackEntry != null) {
            // Only persist primitive-safe data in SavedStateHandle
            // Store the created task's ID so listeners can fetch full data if needed
            previousBackStackEntry.savedStateHandle[CREATED_TASK_KEY] = result.id
        }
        if (navController.currentBackStackEntry != null) {
            navController.popBackStack()
        }
    }

    TaskCreationDialog(
        isVisible = isVisible,
        onDismiss = {
            isVisible = false
            close(null)
        },
        onTaskCreated = { task ->
            isVisible = false
            viewModel.createTask(task) { taskId ->
                val previousBackStackEntry = navController.previousBackStackEntry
                if (previousBackStackEntry != null) {
                    previousBackStackEntry.savedStateHandle[CREATED_TASK_KEY] = taskId
                }
                if (navController.currentBackStackEntry != null) {
                    navController.popBackStack()
                }
            }
        }
    )
}

const val CREATED_TASK_KEY = "createdTask"
