package com.letsgotoperfection.kino.feature.notifications.internal.domain.repository

import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for notification operations
 */
internal interface NotificationRepository {
    
    /**
     * Send immediate notification
     */
    suspend fun sendNotification(data: NotificationData): Result<Unit>
    
    /**
     * Schedule notification for future delivery
     */
    suspend fun scheduleNotification(
        data: NotificationData,
        scheduledTime: LocalDateTime
    ): Result<Unit>
    
    /**
     * Cancel notification
     */
    suspend fun cancelNotification(notificationId: String): Result<Unit>
    
    /**
     * Get scheduled notifications
     */
    fun getScheduledNotifications(): Flow<List<NotificationData>>
    
    /**
     * Mark notification as delivered
     */
    suspend fun markAsDelivered(notificationId: String)
    
    /**
     * Mark notification as dismissed
     */
    suspend fun markAsDismissed(notificationId: String)
    
    /**
     * Get notification statistics
     */
    suspend fun getNotificationStats(): NotificationStats
}

/**
 * Notification statistics
 */
internal data class NotificationStats(
    val pendingCount: Int,
    val deliveredToday: Int,
    val dismissedToday: Int,
    val categoryStats: Map<String, Int>
)
