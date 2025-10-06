package com.letsgotoperfection.kino.feature.taskdetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letsgotoperfection.kino.feature.taskdetail.TaskDetailScreen
import kotlinx.serialization.Serializable

/**
 * Task Detail feature routes - Type-safe navigation
 */
@Serializable
data class TaskDetailRoute(
    val taskId: String
)

/**
 * Task Detail navigation graph
 * Exposes composable screens for app module to wire
 */
fun NavGraphBuilder.taskDetailGraph(
    onNavigateBack: () -> Unit,
    onNavigateToMedia: (String) -> Unit,
    onNavigateToNote: (String) -> Unit
) {
    composable<TaskDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<TaskDetailRoute>()
        TaskDetailScreen(
            taskId = route.taskId,
            onNavigateBack = onNavigateBack,
            onNavigateToMedia = onNavigateToMedia,
            onNavigateToNote = onNavigateToNote
        )
    }
}

/**
 * Deep link patterns for task detail feature
 */
object TaskDetailDeepLinks {
    const val TASK_DETAIL = "kino://app/task/{taskId}"
    
    fun createTaskDetailDeepLink(taskId: String) = "kino://app/task/$taskId"
}
