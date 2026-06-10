package com.letsgotoperfection.kino.feature.recurringtasks.internal.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Worker that reschedules all active recurring task alarms.
 * 
 * This worker is triggered:
 * - After device boot
 * - After app update
 * - When user creates/modifies a recurring task
 * 
 * It ensures all alarms are properly scheduled even after system events.
 */
@HiltWorker
class RescheduleAlarmsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val recurringTasksApi: RecurringTasksApi,
    private val alarmScheduler: RecurringTaskAlarmScheduler
) : CoroutineWorker(appContext, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            Log.i(TAG, "Starting alarm rescheduling")
            
            // Get all active recurring tasks
            val activeRecurringTasks = recurringTasksApi.getActiveRecurringTasks().first()
            
            Log.i(TAG, "Found ${activeRecurringTasks.size} active recurring tasks")
            
            // Reschedule alarms for all active tasks
            alarmScheduler.rescheduleAll(activeRecurringTasks)
            
            Log.i(TAG, "Alarm rescheduling completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reschedule alarms", e)
            // Retry once
            if (runAttemptCount < 1) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    
    companion object {
        private const val TAG = "RescheduleAlarmsWorker"
    }
}





