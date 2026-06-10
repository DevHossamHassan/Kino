package com.letsgotoperfection.kino.feature.notifications.api

import android.content.Context

/**
 * API for initializing the notification system
 * This should be called once by the app module during app startup
 */
interface NotificationInitializer {
    
    /**
     * Initialize the notification system
     * This should be called once during app startup
     */
    fun initialize(context: Context)
    
    /**
     * Check if the notification system is initialized
     */
    fun isInitialized(): Boolean
}



