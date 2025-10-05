package com.letsgotoperfection.kino.feature.notifications.internal.api

import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.ActionType
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.AppNotificationChannel
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationAction
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationData
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationPriority
import com.letsgotoperfection.kino.feature.notifications.internal.domain.repository.NotificationRepository
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationPermissionManager
import com.letsgotoperfection.kino.feature.notifications.R
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationApiImpl @Inject constructor(
    private val repository: NotificationRepository,
    private val permissionManager: NotificationPermissionManager
) : NotificationApi {
    
    override suspend fun sendNotification(
        title: String,
        message: String,
        category: NotificationCategory,
        deepLink: String?,
        actions: List<NotificationAction>
    ): Result<Unit> {
        val channel = AppNotificationChannel.getChannelForCategory(category)
        
        val notificationData = NotificationData(
            id = UUID.randomUUID().toString(),
            channelId = channel.id,
            title = title,
            message = message,
            priority = NotificationPriority.DEFAULT,
            category = category,
            deepLink = deepLink,
            actions = actions,
            bigTextStyle = message.length > 50
        )
        
        return repository.sendNotification(notificationData)
    }
    
    override suspend fun scheduleNotification(
        title: String,
        message: String,
        category: NotificationCategory,
        scheduledTime: LocalDateTime,
        deepLink: String?,
        actions: List<NotificationAction>
    ): Result<Unit> {
        val channel = AppNotificationChannel.getChannelForCategory(category)
        
        val notificationData = NotificationData(
            id = UUID.randomUUID().toString(),
            channelId = channel.id,
            title = title,
            message = message,
            priority = NotificationPriority.DEFAULT,
            category = category,
            deepLink = deepLink,
            actions = actions,
            scheduledTime = scheduledTime
        )
        
        return repository.scheduleNotification(notificationData, scheduledTime)
    }
    
    override suspend fun cancelNotification(notificationId: String): Result<Unit> {
        return repository.cancelNotification(notificationId)
    }
    
    override fun hasPermission(): Boolean {
        return permissionManager.hasNotificationPermission()
    }
    
    override fun openSettings() {
        permissionManager.openNotificationSettings()
    }
    
    override suspend fun sendTaskReminder(
        taskId: String,
        taskTitle: String,
        message: String,
        scheduledTime: LocalDateTime?
    ): Result<Unit> {
        val actions = listOf(
            NotificationAction(
                id = "complete_task_$taskId",
                title = "Complete",
                icon = R.drawable.ic_check,
                type = ActionType.COMPLETE_TASK,
                metadata = mapOf("taskId" to taskId)
            ),
            NotificationAction(
                id = "snooze_task_$taskId",
                title = "Snooze",
                icon = R.drawable.ic_snooze,
                type = ActionType.SNOOZE,
                metadata = mapOf("taskId" to taskId)
            )
        )
        
        val deepLink = "kino://task/$taskId"
        
        return if (scheduledTime != null) {
            scheduleNotification(
                title = "Task Reminder: $taskTitle",
                message = message,
                category = NotificationCategory.TASK_REMINDER,
                scheduledTime = scheduledTime,
                deepLink = deepLink,
                actions = actions
            )
        } else {
            sendNotification(
                title = "Task Reminder: $taskTitle",
                message = message,
                category = NotificationCategory.TASK_REMINDER,
                deepLink = deepLink,
                actions = actions
            )
        }
    }
    
    override suspend fun sendAchievementNotification(
        achievementTitle: String,
        message: String
    ): Result<Unit> {
        return sendNotification(
            title = "Achievement Unlocked! 🎉",
            message = "$achievementTitle - $message",
            category = NotificationCategory.ACHIEVEMENT,
            deepLink = "kino://achievements"
        )
    }
    
    override suspend fun sendSmartSuggestion(
        taskId: String,
        suggestion: String
    ): Result<Unit> {
        val actions = listOf(
            NotificationAction(
                id = "view_task_$taskId",
                title = "View Task",
                icon = R.drawable.ic_open_in_new,
                type = ActionType.OPEN_NOTE,
                metadata = mapOf("taskId" to taskId)
            )
        )
        
        return sendNotification(
            title = "Smart Suggestion",
            message = suggestion,
            category = NotificationCategory.SMART_SUGGESTION,
            deepLink = "kino://task/$taskId",
            actions = actions
        )
    }
    
    override suspend fun sendNoteReminder(
        noteId: String,
        noteTitle: String,
        message: String,
        scheduledTime: LocalDateTime?
    ): Result<Unit> {
        val actions = listOf(
            NotificationAction(
                id = "open_note_$noteId",
                title = "Open Note",
                icon = R.drawable.ic_open_in_new,
                type = ActionType.OPEN_NOTE,
                metadata = mapOf("noteId" to noteId)
            )
        )
        
        val deepLink = "kino://note/$noteId"
        
        return if (scheduledTime != null) {
            scheduleNotification(
                title = "Note Reminder: $noteTitle",
                message = message,
                category = NotificationCategory.NOTE_REMINDER,
                scheduledTime = scheduledTime,
                deepLink = deepLink,
                actions = actions
            )
        } else {
            sendNotification(
                title = "Note Reminder: $noteTitle",
                message = message,
                category = NotificationCategory.NOTE_REMINDER,
                deepLink = deepLink,
                actions = actions
            )
        }
    }
}
