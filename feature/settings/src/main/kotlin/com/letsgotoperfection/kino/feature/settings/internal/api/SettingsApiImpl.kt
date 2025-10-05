package com.letsgotoperfection.kino.feature.settings.internal.api

import com.letsgotoperfection.kino.feature.settings.api.SettingsApi
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsApiImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : SettingsApi {
    
    override fun getSettings(): Flow<AppSettings> {
        return settingsRepository.getSettings()
    }
    
    override fun getThemeSettings(): Flow<ThemeSettings> {
        return settingsRepository.getSettings()
            .map { it.theme }
    }
    
    override fun getNotificationSettings(): Flow<NotificationSettings> {
        return settingsRepository.getSettings()
            .map { it.notifications }
    }
    
    override fun getAiSettings(): Flow<AiSettings> {
        return settingsRepository.getSettings()
            .map { it.ai }
    }
    
    override fun isDarkThemeEnabled(): Flow<Boolean> {
        return settingsRepository.getSettings()
            .map { it.theme.themeMode == com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode.DARK }
    }
    
    override fun areNotificationsEnabled(): Flow<Boolean> {
        return settingsRepository.getSettings()
            .map { it.notifications.enabled }
    }
    
    override fun isAiEnabled(): Flow<Boolean> {
        return settingsRepository.getSettings()
            .map { it.ai.enableAiAnalysis }
    }
    
    override fun isGamificationEnabled(): Flow<Boolean> {
        return settingsRepository.getSettings()
            .map { it.gamification.enabled }
    }
}
