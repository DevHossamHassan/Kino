package com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository

import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model.TaskDetail
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for task detail operations.
 * Provides access to detailed task information and related data.
 */
internal interface TaskDetailRepository {
    
    /**
     * Get detailed task information with all related data.
     */
    fun getTaskDetail(taskId: String): Flow<TaskDetail>
    
    /**
     * Get checklist items for a task.
     */
    fun getChecklist(taskId: String): Flow<List<ChecklistItem>>
    
    /**
     * Toggle completion status of a checklist item.
     */
    suspend fun toggleChecklistItem(itemId: String)
    
    /**
     * Add a new checklist item to a task.
     */
    suspend fun addChecklistItem(item: ChecklistItem)
    
    /**
     * Delete a checklist item.
     */
    suspend fun deleteChecklistItem(itemId: String)
    
    /**
     * Update task progress based on checklist completion.
     */
    suspend fun updateProgress(taskId: String, progress: Int)
    
    /**
     * Get the count of checklist items for a task.
     */
    fun getChecklistCount(taskId: String): Flow<Int>
    
    /**
     * Update task details.
     */
    suspend fun updateTask(
        taskId: String,
        title: String? = null,
        description: String? = null,
        priority: Priority? = null,
        dueDate: LocalDateTime? = null,
        labels: List<Label>? = null
    )
}
