package com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase

import com.letsgotoperfection.kino.core.common.event.EventBus
import com.letsgotoperfection.kino.core.common.event.AppEvent
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository.TaskDetailRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for adding new checklist items to a task.
 * This encapsulates the business logic for creating checklist items.
 */
@Singleton
class AddChecklistItemUseCase @Inject constructor(
    private val repository: TaskDetailRepository,
    private val eventBus: EventBus
) {
    /**
     * Add a new checklist item to a task.
     * 
     * @param taskId The unique identifier of the task
     * @param text The text content of the checklist item
     * @return Result containing the created ChecklistItem or error
     */
    suspend operator fun invoke(
        taskId: String,
        text: String
    ): Result<ChecklistItem> = try {
        require(text.isNotBlank()) { "Checklist item text cannot be blank" }
        
        val order = repository.getChecklistCount(taskId).first()
        val item = ChecklistItem(
            id = UUID.randomUUID().toString(),
            taskId = taskId,
            text = text.trim(),
            isCompleted = false,
            order = order,
            createdAt = LocalDateTime.now()
        )
        
        repository.addChecklistItem(item)
        val checklist = repository.getChecklist(taskId).first()
        repository.updateProgress(taskId, calculateProgress(checklist))
        eventBus.emit(AppEvent.TaskUpdated(taskId))
        
        Result.Success(item)
    } catch (e: Exception) {
        Result.Error(e)
    }

    private fun calculateProgress(checklist: List<ChecklistItem>): Int {
        if (checklist.isEmpty()) return 0
        val completed = checklist.count { it.isCompleted }
        return ((completed.toFloat() / checklist.size) * 100).toInt()
    }
}
