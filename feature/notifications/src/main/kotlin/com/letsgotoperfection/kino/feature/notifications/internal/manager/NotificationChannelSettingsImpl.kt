package com.letsgotoperfection.kino.feature.notifications.internal.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelSettings
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotificationChannelSettings
 * Provides channel settings management functionality
 */
@Singleton
class NotificationChannelSettingsImpl @Inject constructor(
    private val context: Context
) : NotificationChannelSettings {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    override fun isChannelEnabled(channelId: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            channel?.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            // On older versions, channels don't exist, so they're always "enabled"
            true
        }
    }
    
    override fun channelExists(channelId: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.getNotificationChannel(channelId) != null
        } else {
            // On older versions, channels don't exist
            false
        }
    }
    
    override fun getChannelImportance(channelId: String): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            channel?.importance ?: -1
        } else {
            // On older versions, return default importance
            3
        }
    }
    
    override fun shouldSendNotification(channelId: String): Boolean {
        // Check if notifications are enabled system-wide
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!notificationManager.areNotificationsEnabled()) {
                return false
            }
        }
        
        // Check if channel exists and is enabled
        return channelExists(channelId) && isChannelEnabled(channelId)
    }
    
    override fun setChannelEnabled(channelId: String, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            if (channel != null) {
                channel.enableVibration(enabled)
                channel.enableLights(enabled)
                // Note: We can't directly enable/disable channels, but we can modify their properties
                // The user controls channel enable/disable through system settings
            }
        }
    }
    
    override fun getAllChannels(): Map<String, String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.notificationChannels.associate { channel ->
                channel.id to channel.name.toString()
            }
        } else {
            emptyMap()
        }
    }
    
    override fun deleteChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId)
        }
    }
}
