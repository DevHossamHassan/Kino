package com.letsgotoperfection.kino.feature.notifications.api

import com.letsgotoperfection.kino.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Public API for Notifications feature.
 * 
 * This API allows other feature modules to:
 * - Schedule notifications
 * - Manage notification preferences
 * - Handle smart task breakdown notifications
 * 
 * @since 1.0.0
 * @see com.letsgotoperfection.kino.feature.kanban.api.KanbanApi for task operations
 */
interface NotificationsApi {
    
    /**
     * Schedules a notification
     * 
     * @param notification The notification to schedule
     * @return Result indicating success or failure
     */
    suspend fun scheduleNotification(notification: NotificationRequest): Result<Unit>
    
    /**
     * Cancels a scheduled notification
     * 
     * @param notificationId The notification ID to cancel
     * @return Result indicating success or failure
     */
    suspend fun cancelNotification(notificationId: String): Result<Unit>
    
    /**
     * Schedules a smart task breakdown notification
     * 
     * @param taskId The task ID to analyze
     * @param dueDate The task due date
     * @return Result indicating success or failure
     */
    suspend fun scheduleTaskBreakdownNotification(taskId: String, dueDate: Long): Result<Unit>
    
    /**
     * Updates notification preferences
     * 
     * @param preferences The new notification preferences
     * @return Result indicating success or failure
     */
    suspend fun updateNotificationPreferences(preferences: NotificationPreferences): Result<Unit>
    
    /**
     * Get observable notification events
     * 
     * @return Flow of notification events
     */
    fun observeNotificationEvents(): Flow<NotificationEvent>
}

/**
 * Notification request data class
 */
data class NotificationRequest(
    val id: String,
    val title: String,
    val message: String,
    val scheduledTime: Long,
    val type: NotificationType,
    val priority: NotificationPriority = NotificationPriority.NORMAL
)

/**
 * Notification preferences data class
 */
data class NotificationPreferences(
    val taskReminders: Boolean = true,
    val smartBreakdown: Boolean = true,
    val dailyDigest: Boolean = true,
    val weeklyReport: Boolean = true,
    val quietHoursStart: Int = 22, // 10 PM
    val quietHoursEnd: Int = 7     // 7 AM
)

/**
 * Notification event for cross-feature communication
 */
data class NotificationEvent(
    val type: EventType,
    val notificationId: String? = null,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationType {
    TASK_REMINDER, SMART_BREAKDOWN, DAILY_DIGEST, WEEKLY_REPORT, ACHIEVEMENT
}

enum class NotificationPriority {
    LOW, NORMAL, HIGH, URGENT
}

enum class EventType {
    SCHEDULED, SENT, CANCELLED, FAILED, PREFERENCES_UPDATED
}

