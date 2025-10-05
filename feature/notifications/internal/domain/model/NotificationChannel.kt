package com.letsgotoperfection.kino.feature.notifications.internal.domain.model

/**
 * Notification channels configuration
 */
internal enum class AppNotificationChannel(
    val id: String,
    val nameResId: Int,
    val descriptionResId: Int,
    val importance: Int
) {
    TASK_REMINDERS(
        "task_reminders",
        R.string.channel_task_reminders_name,
        R.string.channel_task_reminders_description,
        android.app.NotificationManager.IMPORTANCE_HIGH
    ),
    
    SMART_SUGGESTIONS(
        "smart_suggestions",
        R.string.channel_smart_suggestions_name,
        R.string.channel_smart_suggestions_description,
        android.app.NotificationManager.IMPORTANCE_DEFAULT
    ),
    
    ACHIEVEMENTS(
        "achievements",
        R.string.channel_achievements_name,
        R.string.channel_achievements_description,
        android.app.NotificationManager.IMPORTANCE_DEFAULT
    ),
    
    NOTES(
        "notes",
        R.string.channel_notes_name,
        R.string.channel_notes_description,
        android.app.NotificationManager.IMPORTANCE_LOW
    );
    
    companion object {
        fun getChannelForCategory(category: NotificationCategory): AppNotificationChannel {
            return when (category) {
                NotificationCategory.TASK_REMINDER,
                NotificationCategory.TASK_DUE_SOON,
                NotificationCategory.MICRO_TASK -> TASK_REMINDERS
                
                NotificationCategory.ACHIEVEMENT,
                NotificationCategory.STREAK_REMINDER,
                NotificationCategory.PROGRESS_UPDATE -> ACHIEVEMENTS
                
                NotificationCategory.SMART_SUGGESTION -> SMART_SUGGESTIONS
                
                NotificationCategory.NOTE_REMINDER -> NOTES
            }
        }
    }
}
