package com.letsgotoperfection.kino.feature.taskdetail.api

import androidx.navigation.NavController
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Public API for Task Detail feature.
 * 
 * This API allows other feature modules to:
 * - View detailed task information
 * - Update task details
 * - Manage task checklists
 * 
 * @since 1.0.0
 * @see com.letsgotoperfection.kino.feature.kanban.api.KanbanApi for basic task operations
 */
interface TaskDetailApi {
    
    /**
     * Retrieves detailed task information by ID
     * 
     * @param taskId The unique task identifier
     * @return Result containing the detailed Task or an error
     */
    suspend fun getTaskDetails(taskId: String): Result<Task>
    
    /**
     * Updates task progress
     * 
     * @param taskId The task ID to update
     * @param progress The new progress value (0-100)
     * @return Result indicating success or failure
     */
    suspend fun updateTaskProgress(taskId: String, progress: Int): Result<Unit>
    
    /**
     * Updates task status
     * 
     * @param taskId The task ID to update
     * @param status The new status
     * @return Result indicating success or failure
     */
    suspend fun updateTaskStatus(taskId: String, status: com.letsgotoperfection.kino.core.model.TaskStatus): Result<Unit>
    
    /**
     * Updates task priority
     * 
     * @param taskId The task ID to update
     * @param priority The new priority
     * @return Result indicating success or failure
     */
    suspend fun updateTaskPriority(taskId: String, priority: com.letsgotoperfection.kino.core.model.TaskPriority): Result<Unit>
    
    /**
     * Navigate to task detail screen
     * 
     * @param navController The navigation controller
     * @param taskId The task ID to view
     */
    fun navigateToTaskDetail(navController: NavController, taskId: String)
    
    /**
     * Get observable task detail updates
     * 
     * @return Flow of task detail update events
     */
    fun observeTaskDetailUpdates(): Flow<TaskDetailUpdate>
}

/**
 * Task detail update event for cross-feature communication
 */
data class TaskDetailUpdate(
    val taskId: String,
    val type: UpdateType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class UpdateType {
    PROGRESS_UPDATED, STATUS_CHANGED, PRIORITY_CHANGED, CHECKLIST_UPDATED, DETAILS_UPDATED
}

