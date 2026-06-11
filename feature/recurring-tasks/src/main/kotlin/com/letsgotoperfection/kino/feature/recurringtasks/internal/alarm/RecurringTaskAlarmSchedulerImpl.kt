package com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler.Companion.ACTION_GENERATE_TASK
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler.Companion.EXTRA_RECURRING_TASK_ID
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler.Companion.EXTRA_SCHEDULED_DATE
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.permission.AlarmPermissionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AlarmManager-backed [RecurringTaskAlarmScheduler].
 *
 * - Exact timing via [AlarmManagerCompat.setExactAndAllowWhileIdle] (works in Doze)
 * - Graceful fallback to inexact alarms when the exact-alarm permission is missing
 * - Sliding window scheduling to stay battery friendly
 */
@Singleton
class RecurringTaskAlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recurrenceCalculator: RecurrenceCalculator,
    private val alarmPermissionManager: AlarmPermissionManager
) : RecurringTaskAlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleTaskGeneration(recurringTask: RecurringTask, scheduledDate: LocalDate) {
        try {
            val triggerTime = calculateTriggerTime(scheduledDate, recurringTask.recurrenceRule.timeOfDay)
            val now = System.currentTimeMillis()

            if (triggerTime <= now) {
                Log.w(TAG, "Skipping past trigger time $scheduledDate ${recurringTask.recurrenceRule.timeOfDay} for ${recurringTask.id}")
                return
            }

            val pendingIntent = createPendingIntent(
                recurringTaskId = recurringTask.id,
                scheduledDate = scheduledDate,
                requestCode = generateRequestCode(recurringTask.id, scheduledDate)
            )

            if (alarmPermissionManager.canScheduleExactAlarms()) {
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.i(TAG, "Exact alarm scheduled for '${recurringTask.title}' at $scheduledDate ${recurringTask.recurrenceRule.timeOfDay}")
            } else {
                // Exact alarm permission revoked (Android 12+): still deliver, just inexact.
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.w(TAG, "Exact alarms not permitted; scheduled inexact alarm for '${recurringTask.title}' at $scheduledDate")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm for ${recurringTask.id}", e)
        }
    }

    override fun scheduleUpcomingOccurrences(recurringTask: RecurringTask, maxSchedule: Int) {
        val today = LocalDate.now()
        val windowEnd = today.plusDays(maxSchedule.toLong())
        val effectiveEnd = recurringTask.endDate?.let { minOf(it, windowEnd) } ?: windowEnd

        recurrenceCalculator.getNextOccurrences(
            rule = recurringTask.recurrenceRule,
            startDate = recurringTask.startDate,
            fromDate = today,
            count = maxSchedule,
            endDate = effectiveEnd
        ).forEach { occurrence ->
            scheduleTaskGeneration(recurringTask, occurrence)
        }
    }

    override fun cancelTaskGeneration(recurringTaskId: String, scheduledDate: LocalDate) {
        try {
            val pendingIntent = createPendingIntent(
                recurringTaskId = recurringTaskId,
                scheduledDate = scheduledDate,
                requestCode = generateRequestCode(recurringTaskId, scheduledDate)
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel alarm for $recurringTaskId at $scheduledDate", e)
        }
    }

    override fun cancelAllForRecurringTask(recurringTaskId: String, upcomingDays: Int) {
        val today = LocalDate.now()
        for (i in 0 until upcomingDays) {
            cancelTaskGeneration(recurringTaskId, today.plusDays(i.toLong()))
        }
        Log.i(TAG, "Cancelled pending alarms for $recurringTaskId")
    }

    override fun rescheduleAll(activeRecurringTasks: List<RecurringTask>) {
        Log.i(TAG, "Rescheduling ${activeRecurringTasks.size} active recurring tasks")
        activeRecurringTasks.forEach { task ->
            try {
                scheduleUpcomingOccurrences(task)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reschedule task ${task.id}", e)
            }
        }
    }

    private fun calculateTriggerTime(date: LocalDate, time: java.time.LocalTime): Long {
        return LocalDateTime.of(date, time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun createPendingIntent(
        recurringTaskId: String,
        scheduledDate: LocalDate,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, RecurringTaskAlarmReceiver::class.java).apply {
            action = ACTION_GENERATE_TASK
            // Unique data URI disambiguates intents beyond the request code,
            // eliminating PendingIntent collisions between task/date pairs.
            data = android.net.Uri.parse("kino://recurring/$recurringTaskId/${scheduledDate.toEpochDay()}")
            putExtra(EXTRA_RECURRING_TASK_ID, recurringTaskId)
            putExtra(EXTRA_SCHEDULED_DATE, scheduledDate.toEpochDay())
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            flags
        )
    }

    private fun generateRequestCode(recurringTaskId: String, scheduledDate: LocalDate): Int {
        return ("$recurringTaskId:${scheduledDate.toEpochDay()}").hashCode()
    }

    companion object {
        private const val TAG = "RecurringTaskAlarm"
    }
}
