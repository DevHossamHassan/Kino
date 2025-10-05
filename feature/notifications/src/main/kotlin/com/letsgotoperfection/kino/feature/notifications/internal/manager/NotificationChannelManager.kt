package com.letsgotoperfection.kino.feature.notifications.internal.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages notification channels for the app
 */
@Singleton
class NotificationChannelManager @Inject constructor(
    private val context: Context
) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    /**
     * Creates all notification channels
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createTaskReminderChannel()
            createSmartSuggestionChannel()
            createProductivityChannel()
            createGamificationChannel()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTaskReminderChannel() {
        val channel = NotificationChannel(
            TASK_REMINDER_CHANNEL_ID,
            "Task Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for task deadlines and reminders"
            enableVibration(true)
            enableLights(true)
        }
        
        notificationManager.createNotificationChannel(channel)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createSmartSuggestionChannel() {
        val channel = NotificationChannel(
            SMART_SUGGESTION_CHANNEL_ID,
            "Smart Suggestions",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "AI-powered task suggestions and insights"
            enableVibration(false)
            enableLights(true)
        }
        
        notificationManager.createNotificationChannel(channel)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createProductivityChannel() {
        val channel = NotificationChannel(
            PRODUCTIVITY_CHANNEL_ID,
            "Productivity Insights",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Weekly productivity reports and insights"
            enableVibration(false)
            enableLights(false)
        }
        
        notificationManager.createNotificationChannel(channel)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGamificationChannel() {
        val channel = NotificationChannel(
            GAMIFICATION_CHANNEL_ID,
            "Achievements & Rewards",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Achievement unlocks and gamification rewards"
            enableVibration(true)
            enableLights(true)
        }
        
        notificationManager.createNotificationChannel(channel)
    }
    
    companion object {
        const val TASK_REMINDER_CHANNEL_ID = "task_reminders"
        const val SMART_SUGGESTION_CHANNEL_ID = "smart_suggestions"
        const val PRODUCTIVITY_CHANNEL_ID = "productivity_insights"
        const val GAMIFICATION_CHANNEL_ID = "gamification"
    }
}




