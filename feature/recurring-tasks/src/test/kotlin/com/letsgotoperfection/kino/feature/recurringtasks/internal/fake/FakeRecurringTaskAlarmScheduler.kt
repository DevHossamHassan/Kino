package com.letsgotoperfection.kino.feature.recurringtasks.internal.fake

import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import java.time.LocalDate

/**
 * Recording fake of [RecurringTaskAlarmScheduler] for unit tests.
 */
internal class FakeRecurringTaskAlarmScheduler : RecurringTaskAlarmScheduler {

    val scheduledGenerations = mutableListOf<Pair<RecurringTask, LocalDate>>()
    val scheduledWindows = mutableListOf<RecurringTask>()
    val cancelledGenerations = mutableListOf<Pair<String, LocalDate>>()
    val cancelledAll = mutableListOf<String>()
    val rescheduledBatches = mutableListOf<List<RecurringTask>>()

    override fun scheduleTaskGeneration(recurringTask: RecurringTask, scheduledDate: LocalDate) {
        scheduledGenerations += recurringTask to scheduledDate
    }

    override fun scheduleUpcomingOccurrences(recurringTask: RecurringTask, maxSchedule: Int) {
        scheduledWindows += recurringTask
    }

    override fun cancelTaskGeneration(recurringTaskId: String, scheduledDate: LocalDate) {
        cancelledGenerations += recurringTaskId to scheduledDate
    }

    override fun cancelAllForRecurringTask(recurringTaskId: String, upcomingDays: Int) {
        cancelledAll += recurringTaskId
    }

    override fun rescheduleAll(activeRecurringTasks: List<RecurringTask>) {
        rescheduledBatches += activeRecurringTasks
    }
}
