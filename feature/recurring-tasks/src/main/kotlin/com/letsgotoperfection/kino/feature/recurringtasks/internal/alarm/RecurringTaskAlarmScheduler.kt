package com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm

import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import java.time.LocalDate

/**
 * Scheduler contract for precise recurring task generation.
 *
 * Abstracted from the Android [android.app.AlarmManager] implementation so
 * use cases depending on scheduling stay unit-testable on the JVM.
 *
 * @see RecurringTaskAlarmSchedulerImpl for the AlarmManager-backed implementation
 */
interface RecurringTaskAlarmScheduler {

    /**
     * Schedule an alarm for task generation at the specified date and the
     * rule's time of day. Implementations must skip past trigger times.
     */
    fun scheduleTaskGeneration(recurringTask: RecurringTask, scheduledDate: LocalDate)

    /**
     * Schedule alarms for upcoming occurrences within the sliding window.
     */
    fun scheduleUpcomingOccurrences(
        recurringTask: RecurringTask,
        maxSchedule: Int = SLIDING_WINDOW_DAYS
    )

    /**
     * Cancel a specific scheduled task generation.
     */
    fun cancelTaskGeneration(recurringTaskId: String, scheduledDate: LocalDate)

    /**
     * Cancel all pending alarms for a recurring task (covers the sliding window
     * plus margin so edits/deletes never leave stale alarms behind).
     */
    fun cancelAllForRecurringTask(recurringTaskId: String, upcomingDays: Int = CANCEL_WINDOW_DAYS)

    /**
     * Reschedule all active recurring tasks. Called on boot or app update.
     */
    fun rescheduleAll(activeRecurringTasks: List<RecurringTask>)

    companion object {
        const val SLIDING_WINDOW_DAYS = 7
        const val CANCEL_WINDOW_DAYS = 30
        const val ACTION_GENERATE_TASK = "com.letsgotoperfection.kino.ACTION_GENERATE_TASK"
        const val EXTRA_RECURRING_TASK_ID = "recurring_task_id"
        const val EXTRA_SCHEDULED_DATE = "scheduled_date"
    }
}
