package com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-ready AlarmManager scheduler for precise recurring task generation.
 * 
 * Features:
 * - Exact timing using AlarmManager
 * - Handles device sleep/doze mode
 * - Survives app restarts
 * - Battery-efficient
 */
@Singleton
class RecurringTaskAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * Schedule an exact alarm for task generation at the specified time.
     * Uses setExactAndAllowWhileIdle for Android 6+ to work in Doze mode.
     */
    fun scheduleTaskGeneration(recurringTask: RecurringTask, scheduledDate: LocalDate) {
        try {
            val triggerTime = calculateTriggerTime(scheduledDate, recurringTask.recurrenceRule.timeOfDay)
            val now = System.currentTimeMillis()
            
            val timeUntilTrigger = triggerTime - now
            val minutes = timeUntilTrigger / (1000 * 60)
            
            // Don't schedule past times
            if (triggerTime <= now) {
                Log.w(TAG, "❌ Skipping past time: $scheduledDate at ${recurringTask.recurrenceRule.timeOfDay}")
                return
            }
            
            val pendingIntent = createPendingIntent(
                recurringTaskId = recurringTask.id,
                scheduledDate = scheduledDate,
                requestCode = generateRequestCode(recurringTask.id, scheduledDate)
            )
            
            // Use exact and allow while idle for precise timing even in Doze mode
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            
            Log.i(TAG, "✅ ALARM SCHEDULED for '${recurringTask.title}'")
            Log.i(TAG, "   📅 Date: $scheduledDate at ${recurringTask.recurrenceRule.timeOfDay}")
            Log.i(TAG, "   ⏰ Trigger in: $minutes minutes")
            Log.i(TAG, "   🆔 Request Code: ${generateRequestCode(recurringTask.id, scheduledDate)}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ FAILED to schedule alarm for ${recurringTask.id}", e)
        }
    }
    
    /**
     * Schedule alarms for the next N days of occurrences.
     * Uses a sliding window approach to avoid scheduling too many alarms.
     */
    fun scheduleUpcomingOccurrences(
        recurringTask: RecurringTask,
        occurrences: List<LocalDate>,
        maxSchedule: Int = 7 // Schedule up to 7 days ahead
    ) {
        val today = LocalDate.now()
        val endDate = today.plusDays(maxSchedule.toLong())
        
        occurrences
            .filter { !it.isBefore(today) && !it.isAfter(endDate) }
            .take(maxSchedule)
            .forEach { occurrence ->
                scheduleTaskGeneration(recurringTask, occurrence)
            }
    }
    
    /**
     * Cancel a specific scheduled task generation.
     */
    fun cancelTaskGeneration(recurringTaskId: String, scheduledDate: LocalDate) {
        try {
            val pendingIntent = createPendingIntent(
                recurringTaskId = recurringTaskId,
                scheduledDate = scheduledDate,
                requestCode = generateRequestCode(recurringTaskId, scheduledDate)
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            
            Log.i(TAG, "Cancelled task generation for $recurringTaskId at $scheduledDate")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel alarm for $recurringTaskId", e)
        }
    }
    
    /**
     * Cancel all alarms for a recurring task.
     */
    fun cancelAllForRecurringTask(recurringTaskId: String, upcomingDays: Int = 30) {
        val today = LocalDate.now()
        for (i in 0 until upcomingDays) {
            cancelTaskGeneration(recurringTaskId, today.plusDays(i.toLong()))
        }
        Log.i(TAG, "Cancelled all alarms for $recurringTaskId")
    }
    
    /**
     * Reschedule all active recurring tasks.
     * Called on boot or app update.
     */
    suspend fun rescheduleAll(activeRecurringTasks: List<RecurringTask>) {
        Log.i(TAG, "Rescheduling ${activeRecurringTasks.size} active recurring tasks")
        
        activeRecurringTasks.forEach { task ->
            try {
                // Get next occurrences
                val nextOccurrences = getNextOccurrences(task, 7)
                scheduleUpcomingOccurrences(task, nextOccurrences)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reschedule task ${task.id}", e)
            }
        }
    }
    
    /**
     * Calculate trigger time in milliseconds from epoch.
     */
    private fun calculateTriggerTime(date: LocalDate, time: java.time.LocalTime): Long {
        val dateTime = LocalDateTime.of(date, time)
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    
    /**
     * Create a PendingIntent for the alarm.
     */
    private fun createPendingIntent(
        recurringTaskId: String,
        scheduledDate: LocalDate,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, RecurringTaskAlarmReceiver::class.java).apply {
            action = ACTION_GENERATE_TASK
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
    
    /**
     * Generate unique request code from task ID and date.
     */
    private fun generateRequestCode(recurringTaskId: String, scheduledDate: LocalDate): Int {
        return ("$recurringTaskId${scheduledDate.toEpochDay()}").hashCode()
    }
    
    /**
     * Get next N occurrences for a recurring task.
     * This is a simplified version - should use RecurrenceCalculator in production.
     */
    private fun getNextOccurrences(task: RecurringTask, count: Int): List<LocalDate> {
        val occurrences = mutableListOf<LocalDate>()
        var current = LocalDate.now()
        val endDate = task.endDate ?: current.plusYears(1)
        
        while (occurrences.size < count && !current.isAfter(endDate)) {
            if (task.shouldGenerateOn(current)) {
                occurrences.add(current)
            }
            current = current.plusDays(1)
        }
        
        return occurrences
    }
    
    companion object {
        private const val TAG = "RecurringTaskAlarmScheduler"
        const val ACTION_GENERATE_TASK = "com.letsgotoperfection.kino.ACTION_GENERATE_TASK"
        const val EXTRA_RECURRING_TASK_ID = "recurring_task_id"
        const val EXTRA_SCHEDULED_DATE = "scheduled_date"
    }
}

