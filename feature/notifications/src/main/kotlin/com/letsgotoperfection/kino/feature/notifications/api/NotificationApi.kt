package com.letsgotoperfection.kino.feature.notifications.api

import android.app.PendingIntent

/**
 * Completely generic notification API that can be used by any module
 * This API provides maximum flexibility for creating notifications
 */
interface NotificationApi {
    
    /**
     * Send a simple notification
     * @param title Notification title
     * @param message Notification message
     * @param channelId Channel ID (MUST exist and be enabled)
     * @param smallIcon Small icon resource ID
     * @param largeIcon Large icon resource ID (optional)
     * @param priority Notification priority
     * @param autoCancel Whether to auto-cancel when tapped
     * @param ongoing Whether this is an ongoing notification
     * @param showWhen Whether to show timestamp
     * @param whenTime Custom timestamp (null for current time)
     * @param tickerText Ticker text for older Android versions
     * @param contentIntent Intent to launch when notification is tapped
     * @param deleteIntent Intent to launch when notification is dismissed
     * @param groupKey Group key for notification grouping
     * @param sortKey Sort key for ordering within group
     * @param isGroupSummary Whether this is a group summary notification
     * @param actions List of notification actions
     * @param extras Custom extras bundle
     * @throws ChannelNotEnabledException if channel doesn't exist or is disabled
     */
    suspend fun sendNotification(
        title: String,
        message: String,
        channelId: String,
        smallIcon: Int,
        largeIcon: Int? = null,
        priority: NotificationPriority = NotificationPriority.DEFAULT,
        autoCancel: Boolean = true,
        ongoing: Boolean = false,
        showWhen: Boolean = true,
        whenTime: Long? = null,
        tickerText: String? = null,
        contentIntent: PendingIntent? = null,
        deleteIntent: PendingIntent? = null,
        groupKey: String? = null,
        sortKey: String? = null,
        isGroupSummary: Boolean = false,
        actions: List<NotificationAction> = emptyList(),
        extras: Map<String, Any> = emptyMap()
    )
    
    /**
     * Send a big text style notification
     */
    suspend fun sendBigTextNotification(
        title: String,
        message: String,
        bigText: String,
        channelId: String,
        smallIcon: Int,
        summaryText: String? = null,
        contentIntent: PendingIntent? = null,
        actions: List<NotificationAction> = emptyList()
    )
    
    /**
     * Send an inbox style notification (for grouped notifications)
     */
    suspend fun sendInboxNotification(
        title: String,
        summaryText: String,
        channelId: String,
        smallIcon: Int,
        lines: List<String>,
        contentIntent: PendingIntent? = null,
        actions: List<NotificationAction> = emptyList()
    )
    
    /**
     * Send a progress notification
     */
    suspend fun sendProgressNotification(
        title: String,
        message: String,
        channelId: String,
        smallIcon: Int,
        progress: Int,
        maxProgress: Int = 100,
        indeterminate: Boolean = false,
        contentIntent: PendingIntent? = null,
        actions: List<NotificationAction> = emptyList()
    )
    
    /**
     * Send a media style notification
     */
    suspend fun sendMediaNotification(
        title: String,
        message: String,
        channelId: String,
        smallIcon: Int,
        largeIcon: Int? = null,
        contentIntent: PendingIntent? = null,
        actions: List<NotificationAction> = emptyList()
    )
    
    /**
     * Cancel a notification by ID
     */
    fun cancelNotification(notificationId: Int)
    
    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications()
    
    /**
     * Cancel notifications by tag
     */
    fun cancelNotificationsByTag(tag: String)
    
    /**
     * Data class for notification actions
     */
    data class NotificationAction(
        val icon: Int,
        val title: String,
        val intent: PendingIntent
    )
    
    /**
     * Notification priority levels
     */
    enum class NotificationPriority {
        LOW, DEFAULT, HIGH, MAX
    }
}