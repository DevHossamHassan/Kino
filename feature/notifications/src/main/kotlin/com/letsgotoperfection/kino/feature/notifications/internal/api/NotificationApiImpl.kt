package com.letsgotoperfection.kino.feature.notifications.internal.api

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelManager
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelSettings
import com.letsgotoperfection.kino.feature.notifications.api.NotificationInitializer
import com.letsgotoperfection.kino.feature.notifications.api.ChannelNotEnabledException
import com.letsgotoperfection.kino.feature.notifications.api.NotificationSystemNotInitializedException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Completely generic implementation of NotificationApi
 * This implementation is feature-agnostic and can be used by any module
 */
@Singleton
class NotificationApiImpl @Inject constructor(
    private val context: Context,
    private val channelManager: NotificationChannelManager,
    private val channelSettings: NotificationChannelSettings,
    private val initializer: NotificationInitializer
) : NotificationApi {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    override suspend fun sendNotification(
        title: String,
        message: String,
        channelId: String,
        smallIcon: Int,
        largeIcon: Int?,
        priority: NotificationApi.NotificationPriority,
        autoCancel: Boolean,
        ongoing: Boolean,
        showWhen: Boolean,
        whenTime: Long?,
        tickerText: String?,
        contentIntent: PendingIntent?,
        deleteIntent: PendingIntent?,
        groupKey: String?,
        sortKey: String?,
        isGroupSummary: Boolean,
        actions: List<NotificationApi.NotificationAction>,
        extras: Map<String, Any>
    ) {
        // Ensure system is initialized
        if (!initializer.isInitialized()) {
            throw NotificationSystemNotInitializedException()
        }
        
        // Validate channel exists and is enabled
        if (!channelSettings.shouldSendNotification(channelId)) {
            throw ChannelNotEnabledException(channelId)
        }
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(convertPriority(priority))
            .setAutoCancel(autoCancel)
            .setOngoing(ongoing)
            .setShowWhen(showWhen)
            .apply {
                largeIcon?.let { setLargeIcon(android.graphics.BitmapFactory.decodeResource(context.resources, it)) }
                whenTime?.let { setWhen(it) }
                tickerText?.let { setTicker(it) }
                contentIntent?.let { setContentIntent(it) }
                deleteIntent?.let { setDeleteIntent(it) }
                groupKey?.let { setGroup(it) }
                sortKey?.let { setSortKey(it) }
                if (isGroupSummary) setGroupSummary(true)
                
                // Add actions
                actions.forEach { action ->
                    addAction(action.icon, action.title, action.intent)
                }
                
                // Add extras
                if (extras.isNotEmpty()) {
                    val bundle = Bundle()
                    extras.forEach { (key, value) ->
                        when (value) {
                            is String -> bundle.putString(key, value)
                            is Int -> bundle.putInt(key, value)
                            is Long -> bundle.putLong(key, value)
                            is Boolean -> bundle.putBoolean(key, value)
                            is Float -> bundle.putFloat(key, value)
                            is Double -> bundle.putDouble(key, value)
                        }
                    }
                    setExtras(bundle)
                }
            }
            .build()
        
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
    
    override suspend fun sendBigTextNotification(
        title: String,
        message: String,
        bigText: String,
        channelId: String,
        smallIcon: Int,
        summaryText: String?,
        contentIntent: PendingIntent?,
        actions: List<NotificationApi.NotificationAction>
    ) {
        val style = NotificationCompat.BigTextStyle()
            .bigText(bigText)
            .setBigContentTitle(title)
        
        summaryText?.let { style.setSummaryText(it) }
        
        sendNotification(
            title = title,
            message = message,
            channelId = channelId,
            smallIcon = smallIcon,
            contentIntent = contentIntent,
            actions = actions
        )
    }
    
    override suspend fun sendInboxNotification(
        title: String,
        summaryText: String,
        channelId: String,
        smallIcon: Int,
        lines: List<String>,
        contentIntent: PendingIntent?,
        actions: List<NotificationApi.NotificationAction>
    ) {
        val style = NotificationCompat.InboxStyle()
            .setBigContentTitle(title)
            .setSummaryText(summaryText)
        
        lines.forEach { line ->
            style.addLine(line)
        }
        
        sendNotification(
            title = title,
            message = summaryText,
            channelId = channelId,
            smallIcon = smallIcon,
            contentIntent = contentIntent,
            actions = actions
        )
    }
    
    override suspend fun sendProgressNotification(
        title: String,
        message: String,
        channelId: String,
        smallIcon: Int,
        progress: Int,
        maxProgress: Int,
        indeterminate: Boolean,
        contentIntent: PendingIntent?,
        actions: List<NotificationApi.NotificationAction>
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setProgress(maxProgress, progress, indeterminate)
            .setOngoing(true)
            .apply {
                contentIntent?.let { setContentIntent(it) }
                actions.forEach { action ->
                    addAction(action.icon, action.title, action.intent)
                }
            }
            .build()
        
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
    
    override suspend fun sendMediaNotification(
        title: String,
        message: String,
        channelId: String,
        smallIcon: Int,
        largeIcon: Int?,
        contentIntent: PendingIntent?,
        actions: List<NotificationApi.NotificationAction>
    ) {
        // For now, just send a regular notification
        // Media style notifications require additional setup
        sendNotification(
            title = title,
            message = message,
            channelId = channelId,
            smallIcon = smallIcon,
            largeIcon = largeIcon,
            contentIntent = contentIntent,
            actions = actions
        )
    }
    
    override fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
    
    override fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
    
    override fun cancelNotificationsByTag(tag: String) {
        notificationManager.cancel(tag, 0)
    }
    
    private fun convertPriority(priority: NotificationApi.NotificationPriority): Int {
        return when (priority) {
            NotificationApi.NotificationPriority.LOW -> NotificationCompat.PRIORITY_LOW
            NotificationApi.NotificationPriority.DEFAULT -> NotificationCompat.PRIORITY_DEFAULT
            NotificationApi.NotificationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
            NotificationApi.NotificationPriority.MAX -> NotificationCompat.PRIORITY_MAX
        }
    }
}