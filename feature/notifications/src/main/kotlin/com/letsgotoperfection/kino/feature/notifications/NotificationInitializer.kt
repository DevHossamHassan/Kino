package com.letsgotoperfection.kino.feature.notifications

import android.content.Context
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationChannelManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Initializes the notification module
 * Call this in your Application class onCreate()
 */
@Singleton
class NotificationInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val channelManager: NotificationChannelManager
) {
    
    /**
     * Initialize notification channels
     * MUST be called on app startup
     */
    fun initialize() {
        channelManager.createNotificationChannels()
    }
}
