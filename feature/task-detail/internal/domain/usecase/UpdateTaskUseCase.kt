package com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase

import com.letsgotoperfection.kino.core.common.event.EventBus
import com.letsgotoperfection.kino.core.common.event.AppEvent
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository.TaskDetailRepository
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for updating task details.
 * This encapsulates the business logic for modifying task information.
 */
@Singleton
internal class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskDetailRepository,
    private val eventBus: EventBus
) {
    /**
     * Update task details with the provided information.
     * Only non-null parameters will be updated.
     * 
     * @param taskId The unique identifier of the task
     * @param title New title for the task (optional)
     * @param description New description for the task (optional)
     * @param priority New priority for the task (optional)
     * @param dueDate New due date for the task (optional)
     * @param labels New labels for the task (optional)
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        taskId: String,
        title: String? = null,
        description: String? = null,
        priority: Priority? = null,
        dueDate: LocalDateTime? = null,
        labels: List<Label>? = null
    ): Result<Unit> = runCatching {
        repository.updateTask(
            taskId = taskId,
            title = title,
            description = description,
            priority = priority,
            dueDate = dueDate,
            labels = labels
        )
        
        eventBus.emit(AppEvent.TaskUpdated(taskId))
    }
}
