package com.letsgotoperfection.kino.feature.notifications.api

/**
 * Examples showing how easy it is to use the UltraSimpleNotificationApi.
 * 
 * This is exactly what you wanted - modules just provide the essentials and everything else is handled automatically!
 */
object NotificationExamples {

    /**
     * Example: How to send a simple notification
     * 
     * Before (complex): Create channel, check settings, create PendingIntent, build notification, handle grouping...
     * After (ultra simple): Just provide the essentials!
     */
    suspend fun sendSimpleNotification(notificationApi: UltraSimpleNotificationApi) {
        notificationApi.send(
            channelId = "my_feature",
            title = "Task Created",
            message = "New task added to board",
            icon = android.R.drawable.ic_dialog_info,
            deepLink = "kino://task/123"
        )
    }

    /**
     * Example: How to send a notification with custom sound
     */
    suspend fun sendNotificationWithSound(notificationApi: UltraSimpleNotificationApi) {
        notificationApi.send(
            channelId = "recurring_tasks",
            title = "🔄 Recurring Task Created",
            message = "✨ Daily standup has been added to Backlog in Work",
            icon = android.R.drawable.ic_dialog_info,
            deepLink = "kino://task/456",
            sound = "android.resource://com.letsgotoperfection.kino/raw/notification_sound"
        )
    }

    /**
     * Example: How to send a big text notification
     */
    suspend fun sendBigTextNotification(notificationApi: UltraSimpleNotificationApi) {
        notificationApi.sendBigText(
            channelId = "notes",
            title = "Note Updated",
            message = "Your note has been updated",
            bigText = "This is a long text that will be shown when the user expands the notification. " +
                    "It can contain multiple lines and detailed information about what happened.",
            icon = android.R.drawable.ic_dialog_info,
            deepLink = "kino://note/789"
        )
    }

    /**
     * Example: How to send a grouped notification
     */
    suspend fun sendGroupedNotification(notificationApi: UltraSimpleNotificationApi) {
        val tasks = listOf(
            "• Daily standup → Backlog (Work)",
            "• Weekly review → In Progress (Personal)",
            "• Team meeting → Done (Work)",
            "• Gym session → Backlog (Personal)"
        )

        notificationApi.sendGrouped(
            channelId = "recurring_tasks",
            title = "🔄 4 Recurring Tasks Created",
            summaryText = "Tasks have been added to your board",
            items = tasks,
            icon = android.R.drawable.ic_dialog_info,
            deepLink = "kino://kanban"
        )
    }

    /**
     * Example: How to send a progress notification
     */
    suspend fun sendProgressNotification(notificationApi: UltraSimpleNotificationApi) {
        notificationApi.sendProgress(
            channelId = "sync",
            title = "Syncing Data",
            message = "Uploading files to cloud",
            progress = 75,
            maxProgress = 100,
            indeterminate = false,
            icon = android.R.drawable.ic_dialog_info,
            deepLink = "kino://sync"
        )
    }

    /**
     * Example: How to send a notification with actions
     */
    suspend fun sendNotificationWithActions(notificationApi: UltraSimpleNotificationApi) {
        // Note: In real usage, you'd create PendingIntents for the actions
        // This is just to show the API structure
        notificationApi.send(
            channelId = "reminders",
            title = "Task Reminder",
            message = "Don't forget to complete your daily tasks",
            icon = android.R.drawable.ic_dialog_info,
            deepLink = "kino://tasks",
            actions = listOf(
                // UltraSimpleNotificationApi.NotificationAction(
                //     id = "complete",
                //     title = "Mark Complete",
                //     icon = android.R.drawable.ic_dialog_info,
                //     actionIntent = completeIntent
                // ),
                // UltraSimpleNotificationApi.NotificationAction(
                //     id = "snooze",
                //     title = "Snooze 1h",
                //     icon = android.R.drawable.ic_dialog_info,
                //     actionIntent = snoozeIntent
                // )
            )
        )
    }

    /**
     * Example: How to cancel notifications
     */
    fun cancelNotifications(notificationApi: UltraSimpleNotificationApi) {
        // Cancel all notifications for a specific channel
        notificationApi.cancelAll("my_feature")
        
        // Cancel a specific notification by ID
        notificationApi.cancel(12345)
    }
}

/**
 * Usage in any module:
 * 
 * ```kotlin
 * @Singleton
 * class MyFeatureService @Inject constructor(
 *     private val notificationApi: UltraSimpleNotificationApi
 * ) {
 *     suspend fun doSomething() {
 *         notificationApi.send(
 *             channelId = "my_feature",
 *             title = "Something happened",
 *             message = "Here's what happened",
 *             deepLink = "kino://my_feature/123"
 *         )
 *     }
 * }
 * ```
 * 
 * That's it! Everything else is handled automatically.
 */
