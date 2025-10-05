package com.letsgotoperfection.kino.feature.notifications.api

import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.ActionType
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationAction
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory
import java.time.LocalDateTime

/**
 * Public API for notification module
 */
interface NotificationApi {
    
    /**
     * Send immediate notification
     */
    suspend fun sendNotification(
        title: String,
        message: String,
        category: NotificationCategory,
        deepLink: String? = null,
        actions: List<NotificationAction> = emptyList()
    ): Result<Unit>
    
    /**
     * Schedule notification for future delivery
     */
    suspend fun scheduleNotification(
        title: String,
        message: String,
        category: NotificationCategory,
        scheduledTime: LocalDateTime,
        deepLink: String? = null,
        actions: List<NotificationAction> = emptyList()
    ): Result<Unit>
    
    /**
     * Cancel notification
     */
    suspend fun cancelNotification(notificationId: String): Result<Unit>
    
    /**
     * Check if notification permission is granted
     */
    fun hasPermission(): Boolean
    
    /**
     * Open notification settings
     */
    fun openSettings()
    
    /**
     * Send task reminder notification
     */
    suspend fun sendTaskReminder(
        taskId: String,
        taskTitle: String,
        message: String,
        scheduledTime: LocalDateTime? = null
    ): Result<Unit>
    
    /**
     * Send achievement notification
     */
    suspend fun sendAchievementNotification(
        achievementTitle: String,
        message: String
    ): Result<Unit>
    
    /**
     * Send smart suggestion notification
     */
    suspend fun sendSmartSuggestion(
        taskId: String,
        suggestion: String
    ): Result<Unit>
    
    /**
     * Send note reminder notification
     */
    suspend fun sendNoteReminder(
        noteId: String,
        noteTitle: String,
        message: String,
        scheduledTime: LocalDateTime? = null
    ): Result<Unit>
}
