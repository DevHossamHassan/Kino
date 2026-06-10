package com.letsgotoperfection.kino.feature.notifications.api

import android.app.PendingIntent

/**
 * Ultra-simple notification API - just provide the essentials and everything else is handled automatically!
 * 
 * This is exactly what you wanted - modules just provide:
 * - Channel ID (for grouping)
 * - Title
 * - Message  
 * - Icon (optional)
 * - Deep link (optional)
 * - Sound (optional)
 * - Actions (optional)
 * 
 * Everything else (channel creation, settings checks, grouping, etc.) is handled automatically.
 */
interface UltraSimpleNotificationApi {

    /**
     * Send a simple notification with minimal required data.
     * Everything else is handled automatically!
     * 
     * @param channelId Unique identifier for this type of notification (e.g., "recurring_tasks", "kanban")
     * @param title Notification title
     * @param message Notification message
     * @param icon Icon resource ID (optional, defaults to app icon)
     * @param deepLink Deep link URI when notification is tapped (optional)
     * @param sound Custom sound URI (optional, uses default if not provided)
     * @param actions List of action buttons (optional)
     */
    suspend fun send(
        channelId: String,
        title: String,
        message: String,
        icon: Int = android.R.drawable.ic_dialog_info,
        deepLink: String? = null,
        sound: String? = null,
        actions: List<NotificationAction> = emptyList()
    )

    /**
     * Send a notification with big text (expandable).
     * 
     * @param channelId Unique identifier for this type of notification
     * @param title Notification title
     * @param message Short message (shown when collapsed)
     * @param bigText Long text (shown when expanded)
     * @param icon Icon resource ID (optional)
     * @param deepLink Deep link URI when notification is tapped (optional)
     * @param sound Custom sound URI (optional)
     * @param actions List of action buttons (optional)
     */
    suspend fun sendBigText(
        channelId: String,
        title: String,
        message: String,
        bigText: String,
        icon: Int = android.R.drawable.ic_dialog_info,
        deepLink: String? = null,
        sound: String? = null,
        actions: List<NotificationAction> = emptyList()
    )

    /**
     * Send a grouped notification (inbox style) for multiple items.
     * 
     * @param channelId Unique identifier for this type of notification
     * @param title Group title
     * @param summaryText Summary text
     * @param items List of items to show in the inbox
     * @param icon Icon resource ID (optional)
     * @param deepLink Deep link URI when notification is tapped (optional)
     * @param sound Custom sound URI (optional)
     * @param actions List of action buttons (optional)
     */
    suspend fun sendGrouped(
        channelId: String,
        title: String,
        summaryText: String,
        items: List<String>,
        icon: Int = android.R.drawable.ic_dialog_info,
        deepLink: String? = null,
        sound: String? = null,
        actions: List<NotificationAction> = emptyList()
    )

    /**
     * Send a progress notification.
     * 
     * @param channelId Unique identifier for this type of notification
     * @param title Notification title
     * @param message Notification message
     * @param progress Current progress (0-100)
     * @param maxProgress Maximum progress (defaults to 100)
     * @param indeterminate Whether progress is indeterminate
     * @param icon Icon resource ID (optional)
     * @param deepLink Deep link URI when notification is tapped (optional)
     * @param sound Custom sound URI (optional)
     * @param actions List of action buttons (optional)
     */
    suspend fun sendProgress(
        channelId: String,
        title: String,
        message: String,
        progress: Int,
        maxProgress: Int = 100,
        indeterminate: Boolean = false,
        icon: Int = android.R.drawable.ic_dialog_info,
        deepLink: String? = null,
        sound: String? = null,
        actions: List<NotificationAction> = emptyList()
    )

    /**
     * Cancel all notifications for a specific channel.
     * 
     * @param channelId The channel ID to cancel notifications for
     */
    fun cancelAll(channelId: String)

    /**
     * Cancel a specific notification by ID.
     * 
     * @param notificationId The notification ID to cancel
     */
    fun cancel(notificationId: Int)

}
