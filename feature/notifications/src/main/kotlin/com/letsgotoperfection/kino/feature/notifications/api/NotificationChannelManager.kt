package com.letsgotoperfection.kino.feature.notifications.api

/**
 * API for managing notification channels
 * Modules can use this to create their own channels with type-safe configurations
 */
interface NotificationChannelManager {
    
    /**
     * Create a type-safe notification channel
     * @param channel The channel configuration
     * @return true if channel was created successfully, false if it already exists
     */
    fun createChannel(channel: NotificationChannelConfig): Boolean
    
    /**
     * Create a notification channel with individual parameters (legacy support)
     * @param channelId Unique identifier for the channel
     * @param channelName Display name for the channel
     * @param description Description of the channel
     * @param importance Importance level (1-5, where 5 is highest)
     * @param enableVibration Whether to enable vibration
     * @param enableLights Whether to enable LED lights
     * @param soundUri Custom sound URI (null for default)
     * @return true if channel was created successfully, false if it already exists
     */
    fun createChannel(
        channelId: String,
        channelName: String,
        description: String,
        importance: Int = 3, // DEFAULT importance
        enableVibration: Boolean = true,
        enableLights: Boolean = true,
        soundUri: String? = null
    ): Boolean
    
    /**
     * Check if a channel exists
     */
    fun channelExists(channelId: String): Boolean
    
    /**
     * Delete a channel
     */
    fun deleteChannel(channelId: String)
    
    /**
     * Get channel information
     * @param channelId The channel ID
     * @return Channel info or null if not found
     */
    fun getChannelInfo(channelId: String): ChannelInfo?
    
    /**
     * Data class for channel information
     */
    data class ChannelInfo(
        val id: String,
        val name: String,
        val description: String,
        val importance: Int,
        val enabled: Boolean
    )
}
