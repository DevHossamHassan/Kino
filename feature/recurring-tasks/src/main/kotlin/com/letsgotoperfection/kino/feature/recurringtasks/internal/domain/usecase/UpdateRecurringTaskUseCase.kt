package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.letsgotoperfection.kino.feature.recurringtasks.api.InvalidRecurrenceRuleException
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskNotFoundException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for updating an existing recurring task.
 *
 * On success, all pending alarms are cancelled and rescheduled with the new
 * rule so schedule edits take effect immediately.
 */
class UpdateRecurringTaskUseCase @Inject constructor(
    private val repository: RecurringTasksRepository,
    private val recurrenceCalculator: RecurrenceCalculator,
    private val alarmScheduler: RecurringTaskAlarmScheduler
) {
    
    suspend operator fun invoke(
        id: String,
        title: String,
        description: String,
        section: com.letsgotoperfection.kino.core.model.TaskSection,
        priority: com.letsgotoperfection.kino.core.model.Priority,
        labels: List<com.letsgotoperfection.kino.core.model.Label>,
        recurrenceRule: com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule,
        startDate: java.time.LocalDate,
        endDate: java.time.LocalDate?,
        isActive: Boolean
    ): Result<Unit> {
        // Get existing recurring task
        val existingTask = repository.getRecurringTaskById(id)
            ?: return Result.failure(RecurringTaskNotFoundException("Recurring task with ID $id not found"))
        
        // Validate the recurrence rule
        recurrenceCalculator.validateRecurrenceRule(recurrenceRule)
            .onFailure { exception ->
                return Result.failure(InvalidRecurrenceRuleException(exception.message ?: "Invalid recurrence rule"))
            }
        
        // Update the recurring task
        val updatedTask = existingTask.copy(
            title = title,
            description = description,
            section = section,
            priority = priority,
            labels = labels,
            recurrenceRule = recurrenceRule,
            startDate = startDate,
            endDate = endDate,
            isActive = isActive,
            updatedAt = LocalDateTime.now()
        )
        
        return repository.updateRecurringTask(updatedTask).onSuccess {
            resetAlarms(updatedTask)
        }
    }
    
    suspend operator fun invoke(recurringTask: RecurringTask): Result<Unit> {
        // Validate the recurrence rule
        recurrenceCalculator.validateRecurrenceRule(recurringTask.recurrenceRule)
            .onFailure { exception ->
                return Result.failure(InvalidRecurrenceRuleException(exception.message ?: "Invalid recurrence rule"))
            }
        
        // Update with current timestamp
        val updatedTask = recurringTask.copy(updatedAt = LocalDateTime.now())
        
        return repository.updateRecurringTask(updatedTask).onSuccess {
            resetAlarms(updatedTask)
        }
    }
    
    private fun resetAlarms(task: RecurringTask) {
        alarmScheduler.cancelAllForRecurringTask(task.id)
        if (task.isActive) {
            alarmScheduler.scheduleUpcomingOccurrences(task)
        }
    }
}
