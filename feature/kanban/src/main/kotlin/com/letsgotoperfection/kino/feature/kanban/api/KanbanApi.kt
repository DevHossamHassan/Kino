package com.letsgotoperfection.kino.feature.kanban.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Public API for Kanban feature.
 * 
 * This API allows other feature modules to:
 * - Query tasks
 * - Create/update tasks
 * - Navigate to task screens
 * 
 * @since 1.0.0
 */
interface KanbanApi {
    suspend fun getTask(taskId: String): Result<Task>
    suspend fun createTask(task: Task): Result<String>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
    fun getTaskDetailRoute(taskId: String): String
    fun observeTaskUpdates(): Flow<TaskUpdate>
    
    @Composable
    fun KanbanScreen(
        onTaskClick: (String) -> Unit = {},
        onCreateTask: () -> Unit = {},
        modifier: Modifier = Modifier
    )
}

/**
 * Task update event for cross-feature communication
 */
data class TaskUpdate(
    val taskId: String,
    val type: UpdateType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class UpdateType {
    CREATED, UPDATED, DELETED, STATUS_CHANGED, PRIORITY_CHANGED
}






