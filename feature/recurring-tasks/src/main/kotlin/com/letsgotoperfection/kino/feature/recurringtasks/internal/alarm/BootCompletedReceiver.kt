package com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.letsgotoperfection.kino.feature.recurringtasks.internal.worker.RescheduleAlarmsWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BroadcastReceiver that handles device boot completion.
 * 
 * Reschedules all active recurring task alarms after device restart.
 * This is crucial because AlarmManager alarms don't survive reboots.
 */
@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var workManager: WorkManager
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON" // Some manufacturers
            -> {
                Log.i(TAG, "Device boot completed, rescheduling recurring task alarms")
                rescheduleAlarms()
            }
        }
    }
    
    /**
     * Reschedule all alarms using a Worker.
     * This ensures the work completes even if the receiver is killed.
     */
    private fun rescheduleAlarms() {
        val workRequest = OneTimeWorkRequestBuilder<RescheduleAlarmsWorker>()
            .build()
        
        workManager.enqueue(workRequest)
        Log.i(TAG, "Enqueued alarm rescheduling work")
    }
    
    companion object {
        private const val TAG = "BootCompletedReceiver"
    }
}





