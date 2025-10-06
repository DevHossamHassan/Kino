package com.letsgotoperfection.kino.feature.kanban.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letsgotoperfection.kino.feature.kanban.KanbanBoardScreen
import kotlinx.serialization.Serializable

/**
 * Kanban feature routes - Type-safe navigation
 */
@Serializable
object KanbanBoardRoute

@Serializable
data class CreateTaskRoute(
    val returnToBoard: Boolean = true
)

/**
 * Kanban navigation graph
 * Exposes composable screens for app module to wire
 */
fun NavGraphBuilder.kanbanGraph(
    onTaskClick: (String) -> Unit,
    onCreateTask: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onBackClick: () -> Unit
) {
    composable<KanbanBoardRoute> {
        KanbanBoardScreen(
            onTaskClick = onTaskClick,
            onCreateTask = onCreateTask,
            onNavigateToSettings = onNavigateToSettings,
            onBackClick = onBackClick
        )
    }
}

/**
 * Deep link patterns for kanban feature
 */
object KanbanDeepLinks {
    const val KANBAN_BOARD = "kino://app/kanban"
    const val CREATE_TASK = "kino://app/kanban/create"
    
    fun createKanbanBoardDeepLink() = "kino://app/kanban"
    fun createCreateTaskDeepLink() = "kino://app/kanban/create"
}
