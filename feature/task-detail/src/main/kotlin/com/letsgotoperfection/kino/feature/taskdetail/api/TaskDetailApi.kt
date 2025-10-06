package com.letsgotoperfection.kino.feature.taskdetail.api

import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Public API for Task Detail feature.
 * This allows other feature modules to access task data and operations.
 * 
 * This API provides:
 * - Data queries (get, observe)
 * - Data mutations (update, delete)
 * - Business operations (status changes, assignments)
 * 
 * @since 1.0.0
 * @see com.letsgotoperfection.kino.feature.notes.api.NotesApi for note operations
 * @see com.letsgotoperfection.kino.feature.media.api.MediaApi for media operations
 */
interface TaskDetailApi {
    
    /**
     * Get task by ID.
     * 
     * @param taskId The unique task identifier
     * @return Result containing the Task or an error
     */
    suspend fun getTask(taskId: String): Result<Task>
    
    /**
     * Observe task changes.
     * 
     * @param taskId The unique task identifier
     * @return Flow of task updates
     */
    fun observeTask(taskId: String): Flow<Task?>
    
    /**
     * Update task details.
     * 
     * @param taskId The task ID to update
     * @param title Optional new title
     * @param description Optional new description
     * @param dueDate Optional new due date
     * @return Result indicating success or failure
     */
    suspend fun updateTask(
        taskId: String,
        title: String? = null,
        description: String? = null,
        dueDate: String? = null
    ): Result<Unit>
    
    /**
     * Update task status.
     * 
     * @param taskId The task ID
     * @param status The new status
     * @return Result indicating success or failure
     */
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Unit>
    
    /**
     * Assign task to user.
     * 
     * @param taskId The task ID
     * @param userId The user ID to assign to
     * @return Result indicating success or failure
     */
    suspend fun assignTask(taskId: String, userId: String): Result<Unit>
    
    /**
     * Unassign task from user.
     * 
     * @param taskId The task ID
     * @return Result indicating success or failure
     */
    suspend fun unassignTask(taskId: String): Result<Unit>
    
    /**
     * Add comment to task.
     * 
     * @param taskId The task ID
     * @param comment The comment text
     * @return Result indicating success or failure
     */
    suspend fun addComment(taskId: String, comment: String): Result<Unit>
    
    /**
     * Get task comments.
     * 
     * @param taskId The task ID
     * @return Flow of task comments
     */
    fun getTaskComments(taskId: String): Flow<List<TaskComment>>
    
    /**
     * Delete task.
     * 
     * @param taskId The task ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteTask(taskId: String): Result<Unit>
}

/**
 * Task status enumeration
 */
enum class TaskStatus {
    TODO, IN_PROGRESS, COMPLETED, CANCELLED
}

/**
 * Task comment data class
 */
data class TaskComment(
    val id: String,
    val taskId: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val createdAt: String
)


