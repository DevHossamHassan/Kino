package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskNotFoundException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import javax.inject.Inject

/**
 * Use case for pausing/resuming a recurring task.
 *
 * Pausing cancels all pending alarms; resuming schedules the upcoming window.
 */
class SetRecurringTaskActiveUseCase @Inject constructor(
    private val repository: RecurringTasksRepository,
    private val alarmScheduler: RecurringTaskAlarmScheduler
) {

    suspend operator fun invoke(recurringTaskId: String, isActive: Boolean): Result<Unit> {
        return repository.setRecurringTaskActive(recurringTaskId, isActive).onSuccess {
            if (isActive) {
                val task = repository.getRecurringTaskById(recurringTaskId)
                    ?: return Result.failure(
                        RecurringTaskNotFoundException("Recurring task $recurringTaskId not found")
                    )
                // The repository row was just toggled; reflect that in the domain copy.
                alarmScheduler.scheduleUpcomingOccurrences(task.copy(isActive = true))
            } else {
                alarmScheduler.cancelAllForRecurringTask(recurringTaskId)
            }
        }
    }
}
