package com.letsgotoperfection.kino.feature.recurringtasks.internal.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.feature.kanban.api.KanbanApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.notification.RecurringTaskNotificationService
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Worker that creates a single task instance from a recurring task template.
 * 
 * This worker:
 * - Loads the recurring task template
 * - Creates a task on the board with all template properties
 * - Schedules the next occurrence
 * - Handles errors with retry logic
 */
@HiltWorker
class TaskInstanceCreatorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val recurringTasksApi: RecurringTasksApi,
    private val kanbanApi: KanbanApi,
    private val alarmScheduler: RecurringTaskAlarmScheduler,
    private val notificationService: RecurringTaskNotificationService
) : CoroutineWorker(appContext, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val recurringTaskId = inputData.getString(KEY_RECURRING_TASK_ID)
                ?: return Result.failure()
            val scheduledDateEpochDay = inputData.getLong(KEY_SCHEDULED_DATE, -1)
            if (scheduledDateEpochDay == -1L) return Result.failure()
            
            val scheduledDate = LocalDate.ofEpochDay(scheduledDateEpochDay)
            
            Log.i(TAG, "Creating task instance for $recurringTaskId at $scheduledDate")
            
            createTaskInstance(recurringTaskId, scheduledDate)
            
            Log.i(TAG, "Task instance created successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create task instance", e)
            // Retry with exponential backoff
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    
    /**
     * Creates a task instance from the recurring task template.
     */
    private suspend fun createTaskInstance(recurringTaskId: String, scheduledDate: LocalDate) {
        // Load the recurring task template
        val recurringTasks = recurringTasksApi.getActiveRecurringTasks().first()
        val recurringTask = recurringTasks.find { it.id == recurringTaskId }
            ?: throw IllegalStateException("Recurring task $recurringTaskId not found")
        
        // Check if task should still generate
        if (!recurringTask.isActive) {
            Log.w(TAG, "Recurring task $recurringTaskId is not active, skipping")
            return
        }
        
        if (recurringTask.endDate != null && scheduledDate.isAfter(recurringTask.endDate)) {
            Log.w(TAG, "Scheduled date $scheduledDate is after end date ${recurringTask.endDate}, skipping")
            return
        }
        
        // Calculate due date from template
        val dueDate = if (recurringTask.recurrenceRule.timeOfDay != null) {
            LocalDateTime.of(
                scheduledDate.plusDays(recurringTask.dueDateOffsetDays.toLong()),
                recurringTask.recurrenceRule.timeOfDay
            )
        } else {
            LocalDateTime.of(
                scheduledDate.plusDays(recurringTask.dueDateOffsetDays.toLong()),
                java.time.LocalTime.of(9, 0)
            )
        }
        
        // Generate title with date
        val taskTitle = "${recurringTask.title} - ${scheduledDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"))}"
        
        // Create checklist items from template
        val taskId = java.util.UUID.randomUUID().toString()
        val checklistItems = recurringTask.checklistTemplate.mapIndexed { index, item ->
            ChecklistItem(
                id = java.util.UUID.randomUUID().toString(),
                taskId = taskId,
                text = item,
                isCompleted = false,
                order = index,
                createdAt = LocalDateTime.now()
            )
        }
        
        // Create Task object
        val task = com.letsgotoperfection.kino.core.model.Task(
            id = taskId,
            title = taskTitle,
            description = recurringTask.description,
            section = recurringTask.section,
            column = recurringTask.defaultColumn, // Use the selected column from recurring task
            priority = recurringTask.priority,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            dueDate = dueDate,
            progress = 0,
            orderPosition = 0,
            labels = recurringTask.labels,
            checklist = checklistItems,
            attachments = emptyList()
        )
        
        // Create the task using KanbanApi
        kanbanApi.createTask(task).fold(
            onSuccess = { taskId ->
                Log.i(TAG, "Created task $taskId from recurring task $recurringTaskId")
                
                // Send notification about the created task
                try {
                    val columnName = recurringTask.defaultColumn.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                    val sectionName = recurringTask.section.name.lowercase().replaceFirstChar { it.uppercase() }
                    
                    notificationService.sendTaskCreated(
                        taskTitle = taskTitle,
                        columnName = columnName,
                        sectionName = sectionName,
                        taskId = taskId
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to send notification for created task: ${e.message}", e)
                }
                
                // Update last generated date
                recurringTasksApi.updateRecurringTask(
                    recurringTask.copy(lastGeneratedDate = scheduledDate)
                )
                
                // Schedule next occurrence
                scheduleNextOccurrence(recurringTask, scheduledDate)
            },
            onError = { error ->
                Log.e(TAG, "Failed to create task from recurring task $recurringTaskId: ${error.message}")
                throw error
            }
        )
    }
    
    /**
     * Schedule the next occurrence after this one.
     */
    private fun scheduleNextOccurrence(
        recurringTask: com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask,
        currentDate: LocalDate
    ) {
        var nextDate = currentDate.plusDays(1)
        val endDate = recurringTask.endDate ?: currentDate.plusYears(1)
        var attempts = 0
        
        // Find next occurrence within reasonable time (max 365 days)
        while (!nextDate.isAfter(endDate) && attempts < 365) {
            if (recurringTask.shouldGenerateOn(nextDate)) {
                alarmScheduler.scheduleTaskGeneration(recurringTask, nextDate)
                Log.i(TAG, "Scheduled next occurrence for ${recurringTask.id} at $nextDate")
                return
            }
            nextDate = nextDate.plusDays(1)
            attempts++
        }
        
        Log.i(TAG, "No more occurrences found for ${recurringTask.id}")
    }
    
    companion object {
        private const val TAG = "TaskInstanceCreator"
        const val KEY_RECURRING_TASK_ID = "recurring_task_id"
        const val KEY_SCHEDULED_DATE = "scheduled_date"
    }
}

