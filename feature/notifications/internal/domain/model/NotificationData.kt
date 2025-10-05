package com.letsgotoperfection.kino.feature.notifications.internal.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Represents a notification to be displayed
 */
@Serializable
internal data class NotificationData(
    val id: String,
    val channelId: String,
    val title: String,
    val message: String,
    val priority: NotificationPriority,
    val category: NotificationCategory,
    val deepLink: String? = null,
    val actions: List<NotificationAction> = emptyList(),
    val largeIcon: String? = null,
    val bigTextStyle: Boolean = false,
    val groupKey: String? = null,
    val autoCancel: Boolean = true,
    val scheduledTime: LocalDateTime? = null,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Notification priority levels
 */
internal enum class NotificationPriority {
    MIN,      // No sound, doesn't appear in status bar
    LOW,      // No sound, appears in status bar
    DEFAULT,  // Makes sound
    HIGH,     // Makes sound and peeks
    MAX;      // Same as HIGH, deprecated but kept for compatibility
    
    fun toAndroidPriority(): Int = when (this) {
        MIN -> android.app.NotificationCompat.PRIORITY_MIN
        LOW -> android.app.NotificationCompat.PRIORITY_LOW
        DEFAULT -> android.app.NotificationCompat.PRIORITY_DEFAULT
        HIGH -> android.app.NotificationCompat.PRIORITY_HIGH
        MAX -> android.app.NotificationCompat.PRIORITY_MAX
    }
    
    fun toChannelImportance(): Int = when (this) {
        MIN -> android.app.NotificationManager.IMPORTANCE_MIN
        LOW -> android.app.NotificationManager.IMPORTANCE_LOW
        DEFAULT -> android.app.NotificationManager.IMPORTANCE_DEFAULT
        HIGH -> android.app.NotificationManager.IMPORTANCE_HIGH
        MAX -> android.app.NotificationManager.IMPORTANCE_HIGH
    }
}

/**
 * Notification categories for different features
 */
internal enum class NotificationCategory {
    TASK_REMINDER,
    TASK_DUE_SOON,
    MICRO_TASK,
    ACHIEVEMENT,
    STREAK_REMINDER,
    NOTE_REMINDER,
    SMART_SUGGESTION,
    PROGRESS_UPDATE;
    
    fun toAndroidCategory(): String = when (this) {
        TASK_REMINDER, TASK_DUE_SOON, MICRO_TASK -> android.app.NotificationCompat.CATEGORY_REMINDER
        ACHIEVEMENT, PROGRESS_UPDATE -> android.app.NotificationCompat.CATEGORY_STATUS
        STREAK_REMINDER -> android.app.NotificationCompat.CATEGORY_RECOMMENDATION
        NOTE_REMINDER -> android.app.NotificationCompat.CATEGORY_MESSAGE
        SMART_SUGGESTION -> android.app.NotificationCompat.CATEGORY_RECOMMENDATION
    }
}

/**
 * Actions that can be added to notifications
 */
@Serializable
internal data class NotificationAction(
    val id: String,
    val title: String,
    val icon: Int,
    val type: ActionType,
    val metadata: Map<String, String> = emptyMap()
)

internal enum class ActionType {
    COMPLETE_TASK,
    COMPLETE_MICRO_TASK,
    SNOOZE,
    MARK_AS_DONE,
    OPEN_NOTE,
    DISMISS
}
