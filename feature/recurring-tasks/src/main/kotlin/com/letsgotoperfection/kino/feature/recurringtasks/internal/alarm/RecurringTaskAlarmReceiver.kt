package com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.letsgotoperfection.kino.feature.recurringtasks.internal.worker.TaskInstanceCreatorWorker
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject

/**
 * BroadcastReceiver that handles AlarmManager triggers for recurring task generation.
 * 
 * This receiver is triggered at the exact time specified for task generation.
 * It delegates the actual work to a Worker for robust background processing.
 */
@AndroidEntryPoint
class RecurringTaskAlarmReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var workManager: WorkManager
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            RecurringTaskAlarmScheduler.ACTION_GENERATE_TASK -> {
                handleTaskGeneration(intent)
            }
            else -> {
                Log.w(TAG, "Received unknown action: ${intent.action}")
            }
        }
    }
    
    /**
     * Handle task generation request.
     * Delegates to Worker for robust processing.
     */
    private fun handleTaskGeneration(intent: Intent) {
        val recurringTaskId = intent.getStringExtra(RecurringTaskAlarmScheduler.EXTRA_RECURRING_TASK_ID)
        val scheduledDateEpochDay = intent.getLongExtra(RecurringTaskAlarmScheduler.EXTRA_SCHEDULED_DATE, -1)
        
        if (recurringTaskId == null || scheduledDateEpochDay == -1L) {
            Log.e(TAG, "Invalid intent extras: taskId=$recurringTaskId, date=$scheduledDateEpochDay")
            return
        }
        
        val scheduledDate = LocalDate.ofEpochDay(scheduledDateEpochDay)
        Log.i(TAG, "Alarm triggered for task $recurringTaskId at $scheduledDate")
        
        // Create a WorkRequest to handle the task creation
        // This ensures the work continues even if the app is killed
        val workRequest = OneTimeWorkRequestBuilder<TaskInstanceCreatorWorker>()
            .setInputData(
                workDataOf(
                    TaskInstanceCreatorWorker.KEY_RECURRING_TASK_ID to recurringTaskId,
                    TaskInstanceCreatorWorker.KEY_SCHEDULED_DATE to scheduledDateEpochDay
                )
            )
            .build()
        
        workManager.enqueue(workRequest)
        Log.i(TAG, "Enqueued task creation work for $recurringTaskId")
    }
    
    companion object {
        private const val TAG = "RecurringTaskAlarmReceiver"
    }
}

