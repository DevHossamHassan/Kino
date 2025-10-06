package com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase

import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.common.event.AppEvent
import com.letsgotoperfection.kino.core.common.event.EventBus
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository.TaskDetailRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for deleting checklist items from a task while keeping task progress
 * in sync with the remaining items.
 */
@Singleton
class DeleteChecklistItemUseCase @Inject constructor(
    private val repository: TaskDetailRepository,
    private val eventBus: EventBus
) {

    suspend operator fun invoke(taskId: String, itemId: String): Result<Unit> = try {
        repository.deleteChecklistItem(itemId)
        val checklist = repository.getChecklist(taskId).first()
        repository.updateProgress(taskId, calculateProgress(checklist))
        eventBus.emit(AppEvent.TaskUpdated(taskId))
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    private fun calculateProgress(checklist: List<ChecklistItem>): Int {
        if (checklist.isEmpty()) return 0
        val completed = checklist.count { it.isCompleted }
        return ((completed.toFloat() / checklist.size) * 100).toInt()
    }
}
