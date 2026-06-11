package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import javax.inject.Inject

/**
 * Use case for deleting a recurring task.
 *
 * Cancels all pending alarms so no orphaned instances are generated after deletion.
 * Existing task instances on the board are intentionally kept.
 */
class DeleteRecurringTaskUseCase @Inject constructor(
    private val repository: RecurringTasksRepository,
    private val alarmScheduler: RecurringTaskAlarmScheduler
) {

    suspend operator fun invoke(recurringTaskId: String): Result<Unit> {
        return repository.deleteRecurringTask(recurringTaskId).onSuccess {
            alarmScheduler.cancelAllForRecurringTask(recurringTaskId)
        }
    }
}
