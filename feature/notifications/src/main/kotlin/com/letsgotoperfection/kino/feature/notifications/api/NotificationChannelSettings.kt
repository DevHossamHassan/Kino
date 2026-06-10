package com.letsgotoperfection.kino.feature.notifications.api

/**
 * API for managing notification channel settings
 * Allows modules to check and control their channel settings
 */
interface NotificationChannelSettings {
    
    /**
     * Check if a channel is enabled
     * @param channelId The channel ID to check
     * @return true if channel exists and is enabled, false otherwise
     */
    fun isChannelEnabled(channelId: String): Boolean
    
    /**
     * Check if a channel exists
     * @param channelId The channel ID to check
     * @return true if channel exists, false otherwise
     */
    fun channelExists(channelId: String): Boolean
    
    /**
     * Get channel importance level
     * @param channelId The channel ID to check
     * @return importance level (1-5) or -1 if channel doesn't exist
     */
    fun getChannelImportance(channelId: String): Int
    
    /**
     * Check if notifications are enabled for a channel
     * This combines channel existence, enablement, and system notification settings
     * @param channelId The channel ID to check
     * @return true if notifications should be sent for this channel
     */
    fun shouldSendNotification(channelId: String): Boolean
    
    /**
     * Enable/disable a channel
     * @param channelId The channel ID to modify
     * @param enabled Whether to enable or disable the channel
     */
    fun setChannelEnabled(channelId: String, enabled: Boolean)
    
    /**
     * Get all registered channels
     * @return Map of channel ID to channel name
     */
    fun getAllChannels(): Map<String, String>
    
    /**
     * Delete a channel
     * @param channelId The channel ID to delete
     */
    fun deleteChannel(channelId: String)
}



