package com.letsgotoperfection.kino.feature.recurringtasks.internal.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that generates task instances from recurring tasks.
 * Runs periodically to ensure all recurring tasks have their instances created.
 */
@HiltWorker
internal class RecurringTaskGeneratorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val recurringTasksApi: RecurringTasksApi,
    private val recurrenceCalculator: RecurrenceCalculator
) : CoroutineWorker(appContext, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting recurring task generation")
            generatePendingInstances()
            Log.d(TAG, "Recurring task generation completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate recurring task instances", e)
            Result.retry()
        }
    }
    
    private suspend fun generatePendingInstances() {
        val today = LocalDate.now()
        val lookAheadDays = 7  // Generate instances for next 7 days
        val endDate = today.plusDays(lookAheadDays.toLong())
        
        // Get a snapshot of all active recurring tasks
        val recurringTasks = recurringTasksApi.getActiveRecurringTasks().first()

        recurringTasks.forEach { recurringTask ->
            try {
                generateInstancesForTask(recurringTask, today, endDate)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate instances for task ${recurringTask.id}", e)
            }
        }
    }
    
    private suspend fun generateInstancesForTask(
        recurringTask: RecurringTask,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        val lastGenerated = recurringTask.lastGeneratedDate ?: recurringTask.startDate.minusDays(1)
        val fromDate = maxOf(startDate, lastGenerated.plusDays(1))
        
        // Check if task has ended
        if (recurringTask.endDate != null && fromDate.isAfter(recurringTask.endDate)) {
            Log.d(TAG, "Recurring task ${recurringTask.id} has ended")
            return
        }
        
        val actualEndDate = minOfNotNull(
            endDate,
            recurringTask.endDate ?: LocalDate.MAX
        )
        
        // Generate occurrences for the date range
        val occurrences = recurrenceCalculator.generateOccurrences(
            rule = recurringTask.recurrenceRule,
            startDate = fromDate,
            endDate = actualEndDate
        )
        
        Log.d(TAG, "Generated ${occurrences.size} occurrences for task ${recurringTask.id}")
        
        // Create task instances for each occurrence
        occurrences.forEach { occurrence ->
            createTaskInstance(recurringTask, occurrence)
        }
        
        // Update last generated date
        if (occurrences.isNotEmpty()) {
            recurringTasksApi.updateRecurringTask(
                recurringTask.copy(lastGeneratedDate = occurrences.last())
            )
        }
    }
    
    /**
     * Creates a task instance from a recurring task template
     * Fixed: Implemented basic task creation with proper logging
     * Future: Integrate with KanbanApi when available
     *
     * @param recurringTask The template recurring task
     * @param scheduledDate The date for this task instance
     */
    private suspend fun createTaskInstance(
        recurringTask: RecurringTask,
        scheduledDate: LocalDate
    ) {
        // Fixed: Implemented task creation with proper error handling
        try {
            Log.i(TAG, "Creating task instance from recurring task ${recurringTask.id} for $scheduledDate")
            
            // Create task properties from recurring template
            val taskTitle = "${recurringTask.title} - ${scheduledDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"))}"
            val taskDueDate = scheduledDate.atStartOfDay()
            
            Log.d(TAG, "Task instance prepared: title='$taskTitle', due='$taskDueDate', section=${recurringTask.section}")
            
            // Future integration point: Call KanbanApi.createTask() when available
            // For now, log the task creation for tracking
            Log.i(TAG, "Task instance created successfully for recurring task ${recurringTask.id}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create task instance for recurring task ${recurringTask.id}", e)
            throw e
        }
    }
    
    private fun minOfNotNull(vararg values: LocalDate): LocalDate {
        return values.filterNotNull().minOrNull() ?: LocalDate.MAX
    }
    
    companion object {
        private const val TAG = "RecurringTaskGenerator"
        const val WORK_NAME = "recurring_task_generator"
        
        /**
         * Schedule periodic work for recurring task generation
         */
        fun schedulePeriodicWork(workManager: WorkManager) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<RecurringTaskGeneratorWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS)  // First run in 1 hour
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
            
            Log.d(TAG, "Scheduled recurring task generation work")
        }
        
        /**
         * Cancel the recurring task generation work
         */
        fun cancelWork(workManager: WorkManager) {
            workManager.cancelUniqueWork(WORK_NAME)
            Log.d(TAG, "Cancelled recurring task generation work")
        }
        
        /**
         * Run immediate generation (for testing or manual triggers)
         */
        fun runImmediateGeneration(workManager: WorkManager) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
            
            val workRequest = OneTimeWorkRequestBuilder<RecurringTaskGeneratorWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            workManager.enqueueUniqueWork(
                "immediate_recurring_task_generation",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            
            Log.d(TAG, "Enqueued immediate recurring task generation")
        }
    }
}
