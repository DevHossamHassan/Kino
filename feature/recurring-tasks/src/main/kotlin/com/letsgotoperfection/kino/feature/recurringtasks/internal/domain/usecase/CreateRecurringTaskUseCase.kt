package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.letsgotoperfection.kino.feature.recurringtasks.api.InvalidRecurrenceRuleException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for creating a new recurring task.
 *
 * On success, alarms for the upcoming occurrences are scheduled so generation
 * starts without any further action from the caller.
 */
class CreateRecurringTaskUseCase @Inject constructor(
    private val repository: RecurringTasksRepository,
    private val recurrenceCalculator: RecurrenceCalculator,
    private val alarmScheduler: RecurringTaskAlarmScheduler
) {
    
    suspend operator fun invoke(
        title: String,
        description: String,
        section: com.letsgotoperfection.kino.core.model.TaskSection,
        priority: com.letsgotoperfection.kino.core.model.Priority,
        labels: List<com.letsgotoperfection.kino.core.model.Label>,
        recurrenceRule: com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule,
        startDate: java.time.LocalDate,
        endDate: java.time.LocalDate?,
        isActive: Boolean = true,
        defaultColumn: com.letsgotoperfection.kino.core.model.TaskColumn = com.letsgotoperfection.kino.core.model.TaskColumn.TODO_THIS_WEEK,
        checklistTemplate: List<String> = emptyList(),
        dueDateOffsetDays: Int = 0
    ): Result<String> {
        // Validate the recurrence rule
        recurrenceCalculator.validateRecurrenceRule(recurrenceRule)
            .onFailure { exception ->
                return Result.failure(InvalidRecurrenceRuleException(exception.message ?: "Invalid recurrence rule"))
            }
        
        // Create the recurring task
        val recurringTask = RecurringTask(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            section = section,
            priority = priority,
            labels = labels,
            recurrenceRule = recurrenceRule,
            startDate = startDate,
            endDate = endDate,
            isActive = isActive,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            lastGeneratedDate = null,
            defaultColumn = defaultColumn,
            checklistTemplate = checklistTemplate,
            dueDateOffsetDays = dueDateOffsetDays
        )
        
        return repository.createRecurringTask(recurringTask).onSuccess {
            if (recurringTask.isActive) {
                alarmScheduler.scheduleUpcomingOccurrences(recurringTask)
            }
        }
    }
}
