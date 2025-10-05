package com.letsgotoperfection.kino.feature.notifications.api

import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory

/**
 * Public API for sending notifications
 */
interface NotificationApi {
    
    /**
     * Send a smart suggestion notification
     */
    suspend fun sendSmartSuggestion(
        taskId: String,
        suggestion: String
    )
    
    /**
     * Send a general notification
     */
    suspend fun sendNotification(
        title: String,
        message: String,
        category: NotificationCategory,
        deepLink: String? = null
    )
    
    /**
     * Send task reminder notification
     */
    suspend fun sendTaskReminder(
        taskId: String,
        taskTitle: String,
        dueDate: String
    )
    
    /**
     * Send achievement notification
     */
    suspend fun sendAchievement(
        achievementTitle: String,
        achievementDescription: String
    )
    
    /**
     * Send achievement notification (alternative method name)
     */
    suspend fun sendAchievementNotification(
        achievementTitle: String,
        message: String
    )
    
    /**
     * Send productivity insight notification
     */
    suspend fun sendProductivityInsight(
        insight: String,
        category: String
    )
    
    /**
     * Send note reminder notification
     */
    suspend fun sendNoteReminder(
        noteId: String,
        noteTitle: String,
        reminderText: String
    )
}
