package com.letsgotoperfection.kino.feature.kanban

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
 * @see com.letsgotoperfection.kino.feature.notes.api.NotesApi for linking notes to tasks
 * @see com.letsgotoperfection.kino.feature.media.api.MediaApi for attaching media to tasks
 */
interface KanbanApi {
    
    /**
     * Retrieves a task by its unique identifier.
     * 
     * @param taskId The unique task identifier
     * @return Result containing the Task or an error
     */
    suspend fun getTask(taskId: String): Result<Task>
    
    /**
     * Creates a new task from external feature
     * 
     * @param task The task to create
     * @return Result containing the created task ID or an error
     */
    suspend fun createTask(task: Task): Result<String>
    
    /**
     * Updates an existing task
     * 
     * @param task The task to update
     * @return Result indicating success or failure
     */
    suspend fun updateTask(task: Task): Result<Unit>
    
    /**
     * Deletes a task by ID
     * 
     * @param taskId The task ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteTask(taskId: String): Result<Unit>
    
    /**
     * Get task detail route for navigation
     * 
     * @param taskId The task ID to navigate to
     * @return Route string for task detail
     */
    fun getTaskDetailRoute(taskId: String): String
    
    /**
     * Get observable task updates (for notifications)
     * 
     * @return Flow of task update events
     */
    fun observeTaskUpdates(): Flow<TaskUpdate>
    
    /**
     * Provides the Kanban screen composable
     * 
     * @param onTaskClick Callback when a task is clicked
     * @param onCreateTask Callback when create task is clicked
     * @param modifier Modifier for the screen
     * @return Composable Kanban screen
     */
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
