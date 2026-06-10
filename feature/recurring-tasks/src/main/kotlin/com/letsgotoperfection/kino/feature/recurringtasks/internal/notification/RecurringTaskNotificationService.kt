package com.letsgotoperfection.kino.feature.recurringtasks.internal.notification

import com.letsgotoperfection.kino.feature.notifications.api.UltraSimpleNotificationApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for sending recurring task notifications
 * This service uses the ultra-simple NotificationApi - just provide the essentials!
 */
@Singleton
class RecurringTaskNotificationService @Inject constructor(
    private val notificationApi: UltraSimpleNotificationApi
) {

    companion object {
        private const val CHANNEL_ID = "recurring_tasks"
        private const val RECURRING_TASK_ICON = android.R.drawable.ic_dialog_info
    }
    
    /**
     * Send notification for a single recurring task creation
     * Ultra simple - just provide the essentials!
     */
    suspend fun sendTaskCreated(
        taskTitle: String,
        columnName: String,
        sectionName: String,
        taskId: String
    ) {
        val message = "✨ $taskTitle has been added to $columnName in $sectionName"
        
        notificationApi.send(
            channelId = CHANNEL_ID,
            title = "🔄 Recurring Task Created",
            message = message,
            icon = RECURRING_TASK_ICON,
            deepLink = "kino://task/$taskId"
        )
    }
    
    /**
     * Send grouped notifications for multiple recurring tasks created at the same time
     * Ultra simple - just provide the essentials!
     */
    suspend fun sendTasksGrouped(
        tasks: List<RecurringTaskInfo>
    ) {
        if (tasks.isEmpty()) return
        
        if (tasks.size == 1) {
            // Single task - send individual notification
            val task = tasks.first()
            sendTaskCreated(
                taskTitle = task.taskTitle,
                columnName = task.columnName,
                sectionName = task.sectionName,
                taskId = task.taskId
            )
        } else {
            // Multiple tasks - send grouped notification
            val lines = tasks.take(5).map { task ->
                "• ${task.taskTitle} → ${task.columnName} (${task.sectionName})"
            }
            
            val summaryText = if (tasks.size > 5) {
                "Tasks have been added to your board (+${tasks.size - 5} more)"
            } else {
                "Tasks have been added to your board"
            }
            
            notificationApi.sendGrouped(
                channelId = CHANNEL_ID,
                title = "🔄 ${tasks.size} Recurring Tasks Created",
                summaryText = summaryText,
                items = lines,
                icon = RECURRING_TASK_ICON,
                deepLink = "kino://kanban"
            )
        }
    }
    
    /**
     * Data class for recurring task information
     */
    data class RecurringTaskInfo(
        val taskTitle: String,
        val columnName: String,
        val sectionName: String,
        val taskId: String
    )
}
