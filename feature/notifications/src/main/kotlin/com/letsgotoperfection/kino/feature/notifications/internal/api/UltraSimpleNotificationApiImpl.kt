package com.letsgotoperfection.kino.feature.notifications.internal.api

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.letsgotoperfection.kino.feature.notifications.api.NotificationAction
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelConfig
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelManager
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelSettings
import com.letsgotoperfection.kino.feature.notifications.api.UltraSimpleNotificationApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UltraSimpleNotificationApi that handles everything automatically.
 * 
 * Just provide the essentials and everything else is taken care of:
 * - Channels are created automatically with sensible defaults
 * - Settings are checked automatically
 * - Grouping is handled automatically
 * - Deep links are created automatically
 * - Everything is optimized for the best user experience
 */
@Singleton
class UltraSimpleNotificationApiImpl @Inject constructor(
    private val context: Context,
    private val channelManager: NotificationChannelManager,
    private val channelSettings: NotificationChannelSettings
) : UltraSimpleNotificationApi {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Cache for created channels to avoid repeated creation
    private val createdChannels = mutableSetOf<String>()

    override suspend fun send(
        channelId: String,
        title: String,
        message: String,
        icon: Int,
        deepLink: String?,
        sound: String?,
        actions: List<NotificationAction>
    ) {
        // Ensure channel exists and is enabled
        val fullChannelId = getOrCreateChannel(channelId, sound)
        if (!channelSettings.shouldSendNotification(fullChannelId)) {
            return // Channel is disabled, don't send notification
        }

        // Create content intent if deep link is provided
        val contentIntent = createContentIntent(deepLink)

        // Build and send notification
        val notification = NotificationCompat.Builder(context, fullChannelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .apply {
                contentIntent?.let { setContentIntent(it) }
                
                // Add actions
                actions.forEach { action ->
                    addAction(action.icon, action.title, action.actionIntent)
                }
                
                // Set group for automatic grouping
                setGroup("group_$channelId")
            }
            .build()

        val notificationId = generateNotificationId(channelId)
        notificationManager.notify(notificationId, notification)
    }

    override suspend fun sendBigText(
        channelId: String,
        title: String,
        message: String,
        bigText: String,
        icon: Int,
        deepLink: String?,
        sound: String?,
        actions: List<NotificationAction>
    ) {
        val fullChannelId = getOrCreateChannel(channelId, sound)
        if (!channelSettings.shouldSendNotification(fullChannelId)) {
            return
        }

        val contentIntent = createContentIntent(deepLink)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .setBigContentTitle(title)
            .setSummaryText(message)
            .bigText(bigText)

        val notification = NotificationCompat.Builder(context, fullChannelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .apply {
                contentIntent?.let { setContentIntent(it) }
                actions.forEach { action ->
                    addAction(action.icon, action.title, action.actionIntent)
                }
                setGroup("group_$channelId")
            }
            .build()

        val notificationId = generateNotificationId(channelId)
        notificationManager.notify(notificationId, notification)
    }

    override suspend fun sendGrouped(
        channelId: String,
        title: String,
        summaryText: String,
        items: List<String>,
        icon: Int,
        deepLink: String?,
        sound: String?,
        actions: List<NotificationAction>
    ) {
        val fullChannelId = getOrCreateChannel(channelId, sound)
        if (!channelSettings.shouldSendNotification(fullChannelId)) {
            return
        }

        val contentIntent = createContentIntent(deepLink)

        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(title)
            .setSummaryText(summaryText)
        items.forEach { inboxStyle.addLine(it) }

        val notification = NotificationCompat.Builder(context, fullChannelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(summaryText)
            .setStyle(inboxStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .apply {
                contentIntent?.let { setContentIntent(it) }
                actions.forEach { action ->
                    addAction(action.icon, action.title, action.actionIntent)
                }
                setGroup("group_$channelId")
                setGroupSummary(true) // This is the group summary
            }
            .build()

        val notificationId = generateNotificationId(channelId)
        notificationManager.notify(notificationId, notification)
    }

    override suspend fun sendProgress(
        channelId: String,
        title: String,
        message: String,
        progress: Int,
        maxProgress: Int,
        indeterminate: Boolean,
        icon: Int,
        deepLink: String?,
        sound: String?,
        actions: List<NotificationAction>
    ) {
        val fullChannelId = getOrCreateChannel(channelId, sound)
        if (!channelSettings.shouldSendNotification(fullChannelId)) {
            return
        }

        val contentIntent = createContentIntent(deepLink)

        val notification = NotificationCompat.Builder(context, fullChannelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setProgress(maxProgress, progress, indeterminate)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false) // Progress notifications usually don't auto-cancel
            .setOngoing(true) // Progress notifications are usually ongoing
            .apply {
                contentIntent?.let { setContentIntent(it) }
                actions.forEach { action ->
                    addAction(action.icon, action.title, action.actionIntent)
                }
                setGroup("group_$channelId")
            }
            .build()

        val notificationId = generateNotificationId(channelId)
        notificationManager.notify(notificationId, notification)
    }

    override fun cancelAll(channelId: String) {
        // For simplicity, cancel all notifications
        // In a more sophisticated implementation, we'd track notifications by channel
        notificationManager.cancelAll()
    }

    override fun cancel(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    /**
     * Get or create a notification channel for the given channel ID.
     * Channels are created lazily with sensible defaults.
     */
    private fun getOrCreateChannel(channelId: String, customSound: String?): String {
        val fullChannelId = "feature_$channelId"
        
        if (!createdChannels.contains(fullChannelId)) {
            val channelConfig = NotificationChannelConfig.Critical(
                channelId = fullChannelId,
                channelName = getChannelDisplayName(channelId),
                channelDescription = "Notifications for ${getChannelDisplayName(channelId)}",
                customSound = customSound
            )
            channelManager.createChannel(channelConfig)
            createdChannels.add(fullChannelId)
        }
        
        return fullChannelId
    }

    /**
     * Get a user-friendly display name for a channel ID.
     */
    private fun getChannelDisplayName(channelId: String): String {
        return when (channelId) {
            "recurring_tasks" -> "Recurring Tasks"
            "kanban" -> "Task Board"
            "notes" -> "Notes"
            "media" -> "Media Manager"
            "settings" -> "Settings"
            "reminders" -> "Reminders"
            "alerts" -> "Alerts"
            else -> channelId.replace("_", " ").split(" ").joinToString(" ") { 
                it.replaceFirstChar { char -> char.uppercase() } 
            }
        }
    }

    /**
     * Create content intent for deep linking.
     */
    private fun createContentIntent(deepLink: String?): PendingIntent? {
        return deepLink?.let { uri ->
            val deepLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val requestCode = uri.hashCode() and 0x7fffffff
            PendingIntent.getActivity(
                context,
                requestCode,
                deepLinkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    /**
     * Generate a unique notification ID for a channel.
     * This ensures notifications from the same channel can be grouped properly.
     */
    private fun generateNotificationId(channelId: String): Int {
        return (channelId.hashCode() + System.currentTimeMillis()).toInt() and 0x7fffffff
    }
}
