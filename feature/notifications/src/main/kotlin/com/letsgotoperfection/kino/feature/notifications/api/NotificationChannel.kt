package com.letsgotoperfection.kino.feature.notifications.api

/**
 * Type-safe notification channel definitions
 * This allows modules to create channels with predefined configurations
 */
sealed class NotificationChannelConfig(
    val id: String,
    val name: String,
    val description: String,
    val importance: Int,
    open val enableVibration: Boolean = true,
    open val enableLights: Boolean = true,
    val soundUri: String? = null
) {
    
    /**
     * High priority channel for critical notifications
     */
    data class Critical(
        val channelId: String,
        val channelName: String,
        val channelDescription: String,
        val customSound: String? = null
    ) : NotificationChannelConfig(
        id = channelId,
        name = channelName,
        description = channelDescription,
        importance = 4, // HIGH
        enableVibration = true,
        enableLights = true,
        soundUri = customSound
    )
    
    /**
     * Default priority channel for regular notifications
     */
    data class Default(
        val channelId: String,
        val channelName: String,
        val channelDescription: String,
        val customSound: String? = null
    ) : NotificationChannelConfig(
        id = channelId,
        name = channelName,
        description = channelDescription,
        importance = 3, // DEFAULT
        enableVibration = true,
        enableLights = false,
        soundUri = customSound
    )
    
    /**
     * Low priority channel for background notifications
     */
    data class Low(
        val channelId: String,
        val channelName: String,
        val channelDescription: String,
        val customSound: String? = null
    ) : NotificationChannelConfig(
        id = channelId,
        name = channelName,
        description = channelDescription,
        importance = 2, // LOW
        enableVibration = false,
        enableLights = false,
        soundUri = customSound
    )
    
    /**
     * Silent channel for system notifications
     */
    data class Silent(
        val channelId: String,
        val channelName: String,
        val channelDescription: String
    ) : NotificationChannelConfig(
        id = channelId,
        name = channelName,
        description = channelDescription,
        importance = 1, // MIN
        enableVibration = false,
        enableLights = false,
        soundUri = null
    )
    
    /**
     * Custom channel with full control
     */
    data class Custom(
        val channelId: String,
        val channelName: String,
        val channelDescription: String,
        val channelImportance: Int,
        override val enableVibration: Boolean = true,
        override val enableLights: Boolean = true,
        val customSound: String? = null
    ) : NotificationChannelConfig(
        id = channelId,
        name = channelName,
        description = channelDescription,
        importance = channelImportance,
        enableVibration = enableVibration,
        enableLights = enableLights,
        soundUri = customSound
    )
}
