package com.letsgotoperfection.kino.feature.notifications.internal.api

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationChannelManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotificationApi
 */
@Singleton
class NotificationApiImpl @Inject constructor(
    private val context: Context,
    private val channelManager: NotificationChannelManager
) : NotificationApi {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    override suspend fun sendSmartSuggestion(
        taskId: String,
        suggestion: String
    ) {
        sendNotification(
            title = "Smart Suggestion",
            message = suggestion,
            category = NotificationCategory.SMART_SUGGESTION,
            deepLink = "kino://task/$taskId"
        )
    }
    
    override suspend fun sendNotification(
        title: String,
        message: String,
        category: NotificationCategory,
        deepLink: String?
    ) {
        val channelId = when (category) {
            NotificationCategory.TASK_REMINDER -> NotificationChannelManager.TASK_REMINDER_CHANNEL_ID
            NotificationCategory.SMART_SUGGESTION -> NotificationChannelManager.SMART_SUGGESTION_CHANNEL_ID
            NotificationCategory.PRODUCTIVITY_INSIGHT -> NotificationChannelManager.PRODUCTIVITY_CHANNEL_ID
            NotificationCategory.ACHIEVEMENT -> NotificationChannelManager.GAMIFICATION_CHANNEL_ID
            NotificationCategory.GAMIFICATION -> NotificationChannelManager.GAMIFICATION_CHANNEL_ID
            NotificationCategory.STREAK_REMINDER -> NotificationChannelManager.GAMIFICATION_CHANNEL_ID
            NotificationCategory.PROGRESS_UPDATE -> NotificationChannelManager.GAMIFICATION_CHANNEL_ID
            NotificationCategory.NOTE_REMINDER -> NotificationChannelManager.TASK_REMINDER_CHANNEL_ID
            NotificationCategory.TASK_DUE_SOON -> NotificationChannelManager.TASK_REMINDER_CHANNEL_ID
            NotificationCategory.MICRO_TASK -> NotificationChannelManager.SMART_SUGGESTION_CHANNEL_ID
        }
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .apply {
                deepLink?.let { link ->
                    val deepLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val requestCode = link.hashCode() and 0x7fffffff
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        requestCode,
                        deepLinkIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    setContentIntent(pendingIntent)
                }
            }
            .build()
        
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    
    override suspend fun sendTaskReminder(
        taskId: String,
        taskTitle: String,
        dueDate: String
    ) {
        sendNotification(
            title = "Task Reminder",
            message = "$taskTitle is due $dueDate",
            category = NotificationCategory.TASK_REMINDER,
            deepLink = "kino://task/$taskId"
        )
    }
    
    override suspend fun sendAchievement(
        achievementTitle: String,
        achievementDescription: String
    ) {
        sendNotification(
            title = "Achievement Unlocked!",
            message = "$achievementTitle: $achievementDescription",
            category = NotificationCategory.ACHIEVEMENT
        )
    }
    
    override suspend fun sendProductivityInsight(
        insight: String,
        category: String
    ) {
        sendNotification(
            title = "Productivity Insight",
            message = "$category: $insight",
            category = NotificationCategory.PRODUCTIVITY_INSIGHT,
            deepLink = "kino://insights"
        )
    }
    
    override suspend fun sendAchievementNotification(
        achievementTitle: String,
        message: String
    ) {
        sendNotification(
            title = "Achievement Unlocked!",
            message = "$achievementTitle: $message",
            category = NotificationCategory.ACHIEVEMENT
        )
    }
    
    override suspend fun sendNoteReminder(
        noteId: String,
        noteTitle: String,
        reminderText: String
    ) {
        sendNotification(
            title = "Note Reminder",
            message = "$noteTitle: $reminderText",
            category = NotificationCategory.NOTE_REMINDER,
            deepLink = "kino://note/$noteId"
        )
    }
}
