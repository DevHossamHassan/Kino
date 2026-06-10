package com.letsgotoperfection.kino.feature.settings.api

import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeSettings
import kotlinx.coroutines.flow.Flow

/**
 * Public API for Settings feature.
 * 
 * This API allows other feature modules to:
 * - Query current settings
 * - Observe settings changes
 * - Update specific settings
 * 
 * @since 1.0.0
 */
interface SettingsApi {
    /**
     * Get complete app settings
     */
    fun getSettings(): Flow<AppSettings>
    
    /**
     * Get theme settings only
     */
    fun getThemeSettings(): Flow<ThemeSettings>
    
    /**
     * Get notification settings only
     */
    fun getNotificationSettings(): Flow<NotificationSettings>
    
    /**
     * Get AI settings only
     */
    fun getAiSettings(): Flow<AiSettings>
    
    /**
     * Check if dark theme is enabled
     */
    fun isDarkThemeEnabled(): Flow<Boolean>
    
    /**
     * Check if notifications are enabled
     */
    fun areNotificationsEnabled(): Flow<Boolean>
    
    /**
     * Check if AI features are enabled
     */
    fun isAiEnabled(): Flow<Boolean>
    
    /**
     * Check if gamification is enabled
     */
    fun isGamificationEnabled(): Flow<Boolean>
    
    /**
     * Check if recurring task notifications are enabled
     */
    fun areRecurringTaskNotificationsEnabled(): Flow<Boolean>
    
    /**
     * Update recurring task notification setting
     */
    suspend fun updateRecurringTaskNotifications(enabled: Boolean): Result<Unit>
}
