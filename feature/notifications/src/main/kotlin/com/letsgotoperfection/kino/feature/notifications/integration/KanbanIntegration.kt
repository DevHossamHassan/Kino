package com.letsgotoperfection.kino.feature.notifications.integration

import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Integration example for Kanban feature
 * This shows how to use the notification API from other feature modules
 */
class KanbanIntegration @Inject constructor(
    private val notificationApi: NotificationApi
) {
    
    /**
     * Create a task with reminder notification
     */
    suspend fun createTaskWithReminder(
        taskId: String,
        taskTitle: String,
        reminderTime: LocalDateTime
    ) {
        notificationApi.sendTaskReminder(
            taskId = taskId,
            taskTitle = taskTitle,
            dueDate = reminderTime.toString()
        )
    }
    
    /**
     * Send task due soon notification
     */
    suspend fun sendTaskDueSoonNotification(
        taskId: String,
        taskTitle: String,
        dueInHours: Int
    ) {
        notificationApi.sendNotification(
            title = "Task Due Soon",
            message = "\"$taskTitle\" is due in $dueInHours hours",
            category = NotificationCategory.TASK_DUE_SOON,
            deepLink = "kino://task/$taskId"
        )
    }
    
    /**
     * Send micro-task completion notification
     */
    suspend fun sendMicroTaskNotification(
        taskId: String,
        microTaskTitle: String
    ) {
        notificationApi.sendNotification(
            title = "Micro-task Complete!",
            message = "Great job completing: $microTaskTitle",
            category = NotificationCategory.MICRO_TASK,
            deepLink = "kino://task/$taskId"
        )
    }
}
