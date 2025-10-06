package com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase

import com.letsgotoperfection.kino.core.common.event.EventBus
import com.letsgotoperfection.kino.core.common.event.AppEvent
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository.TaskDetailRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for toggling checklist item completion status.
 * This encapsulates the business logic for updating checklist items and task progress.
 */
@Singleton
class ToggleChecklistItemUseCase @Inject constructor(
    private val repository: TaskDetailRepository,
    private val eventBus: EventBus
) {
    /**
     * Toggle the completion status of a checklist item and update task progress.
     * 
     * @param taskId The unique identifier of the task
     * @param itemId The unique identifier of the checklist item
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        taskId: String,
        itemId: String
    ): Result<Unit> = try {
        repository.toggleChecklistItem(itemId)
        
        // Update task progress based on checklist completion
        val checklist = repository.getChecklist(taskId).first()
        val progress = calculateProgress(checklist)
        repository.updateProgress(taskId, progress)
        
        // Emit event for other features to react
        eventBus.emit(AppEvent.TaskUpdated(taskId))
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
    
    /**
     * Calculate progress percentage based on completed checklist items.
     * 
     * @param checklist List of checklist items
     * @return Progress percentage (0-100)
     */
    private fun calculateProgress(checklist: List<ChecklistItem>): Int {
        if (checklist.isEmpty()) return 0
        val completed = checklist.count { it.isCompleted }
        return ((completed.toFloat() / checklist.size) * 100).toInt()
    }
}
