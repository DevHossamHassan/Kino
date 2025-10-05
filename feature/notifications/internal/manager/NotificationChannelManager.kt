package com.letsgotoperfection.kino.feature.notifications.internal.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.letsgotoperfection.kino.feature.notifications.R
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.AppNotificationChannel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager
) {
    
    /**
     * Create all notification channels
     * MUST be called on app startup
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppNotificationChannel.values().forEach { channelConfig ->
                createChannel(channelConfig)
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(config: AppNotificationChannel) {
        val channel = NotificationChannel(
            config.id,
            context.getString(config.nameResId),
            config.importance
        ).apply {
            description = context.getString(config.descriptionResId)
            
            // Configure based on channel type
            when (config) {
                AppNotificationChannel.TASK_REMINDERS -> {
                    enableVibration(true)
                    enableLights(true)
                    lightColor = Color.BLUE
                    setShowBadge(true)
                }
                AppNotificationChannel.ACHIEVEMENTS -> {
                    enableVibration(true)
                    enableLights(true)
                    lightColor = Color.GREEN
                    setShowBadge(true)
                }
                AppNotificationChannel.SMART_SUGGESTIONS -> {
                    enableVibration(false)
                    setShowBadge(false)
                }
                AppNotificationChannel.NOTES -> {
                    enableVibration(false)
                    setShowBadge(true)
                }
            }
        }
        
        notificationManager.createNotificationChannel(channel)
    }
    
    /**
     * Check if notifications are enabled for a channel
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun isChannelEnabled(channelId: String): Boolean {
        val channel = notificationManager.getNotificationChannel(channelId)
        return channel?.importance != NotificationManager.IMPORTANCE_NONE
    }
    
    /**
     * Open channel settings
     */
    fun openChannelSettings(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = android.content.Intent(android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                putExtra(android.provider.Settings.EXTRA_CHANNEL_ID, channelId)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
