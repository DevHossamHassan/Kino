package com.letsgotoperfection.kino.feature.notifications.internal.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelConfig
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotificationChannelManager
 * Handles creation and management of notification channels with type-safe configurations
 */
@Singleton
class NotificationChannelManagerImpl @Inject constructor(
    private val context: Context
) : NotificationChannelManager {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    override fun createChannel(channel: NotificationChannelConfig): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Check if channel already exists
            if (channelExists(channel.id)) {
                return false // Channel already exists
            }
            
            val notificationChannel = NotificationChannel(
                channel.id,
                channel.name,
                channel.importance
            ).apply {
                description = channel.description
                enableVibration(channel.enableVibration)
                enableLights(channel.enableLights)
                
                channel.soundUri?.let { uri ->
                    setSound(Uri.parse(uri), null)
                }
            }
            
            notificationManager.createNotificationChannel(notificationChannel)
            return true
        }
        return false // Channels not supported on older versions
    }
    
    override fun createChannel(
        channelId: String,
        channelName: String,
        description: String,
        importance: Int,
        enableVibration: Boolean,
        enableLights: Boolean,
        soundUri: String?
    ): Boolean {
        val customChannel = NotificationChannelConfig.Custom(
            channelId = channelId,
            channelName = channelName,
            channelDescription = description,
            channelImportance = importance,
            enableVibration = enableVibration,
            enableLights = enableLights,
            customSound = soundUri
        )
        return createChannel(customChannel)
    }
    
    override fun channelExists(channelId: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.getNotificationChannel(channelId) != null
        } else {
            true // Channels don't exist on older versions
        }
    }
    
    override fun deleteChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId)
        }
    }
    
    override fun getChannelInfo(channelId: String): NotificationChannelManager.ChannelInfo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            if (channel != null) {
                NotificationChannelManager.ChannelInfo(
                    id = channel.id,
                    name = channel.name.toString(),
                    description = channel.description ?: "",
                    importance = channel.importance,
                    enabled = channel.importance != NotificationManager.IMPORTANCE_NONE
                )
            } else {
                null
            }
        } else {
            null
        }
    }
}
