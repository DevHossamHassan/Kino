package com.letsgotoperfection.kino.feature.settings.internal.domain.usecase

import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.GamificationSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.PrivacySettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode
import com.letsgotoperfection.kino.feature.settings.internal.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun updateThemeMode(mode: ThemeMode) = 
        settingsRepository.updateThemeMode(mode)
    
    suspend fun updateDynamicColors(enabled: Boolean) = 
        settingsRepository.updateDynamicColors(enabled)
    
    suspend fun updateFontScale(scale: FontScale) = 
        settingsRepository.updateFontScale(scale)
    
    suspend fun updateNotificationSettings(settings: NotificationSettings) = 
        settingsRepository.updateNotificationSettings(settings)
    
    suspend fun updateNotificationEnabled(enabled: Boolean) = 
        settingsRepository.updateNotificationEnabled(enabled)
    
    suspend fun updateTaskReminders(enabled: Boolean) = 
        settingsRepository.updateTaskReminders(enabled)
    
    suspend fun updateSmartSuggestions(enabled: Boolean) = 
        settingsRepository.updateSmartSuggestions(enabled)
    
    suspend fun updateAchievements(enabled: Boolean) = 
        settingsRepository.updateAchievements(enabled)
    
    suspend fun updateNoteReminders(enabled: Boolean) = 
        settingsRepository.updateNoteReminders(enabled)
    
    suspend fun updateRecurringTasks(enabled: Boolean) = 
        settingsRepository.updateRecurringTasks(enabled)
    
    suspend fun updateQuietHours(enabled: Boolean, start: java.time.LocalTime?, end: java.time.LocalTime?) = 
        settingsRepository.updateQuietHours(enabled, start, end)
    
    suspend fun updateNotificationFrequency(frequency: NotificationFrequency) = 
        settingsRepository.updateNotificationFrequency(frequency)
    
    suspend fun updateAiSettings(settings: AiSettings) = 
        settingsRepository.updateAiSettings(settings)
    
    suspend fun updateAiEnabled(enabled: Boolean) = 
        settingsRepository.updateAiEnabled(enabled)
    
    suspend fun updateCloudAi(enabled: Boolean) = 
        settingsRepository.updateCloudAi(enabled)
    
    suspend fun updateAutoAnalyzeTasks(enabled: Boolean) = 
        settingsRepository.updateAutoAnalyzeTasks(enabled)
    
    suspend fun updateSmartTaskBreakdown(enabled: Boolean) = 
        settingsRepository.updateSmartTaskBreakdown(enabled)
    
    suspend fun updateGamificationSettings(settings: GamificationSettings) = 
        settingsRepository.updateGamificationSettings(settings)
    
    suspend fun updateGamificationEnabled(enabled: Boolean) = 
        settingsRepository.updateGamificationEnabled(enabled)
    
    suspend fun updateShowStreaks(enabled: Boolean) = 
        settingsRepository.updateShowStreaks(enabled)
    
    suspend fun updateShowAchievements(enabled: Boolean) = 
        settingsRepository.updateShowAchievements(enabled)
    
    suspend fun updateShowCelebrations(enabled: Boolean) = 
        settingsRepository.updateShowCelebrations(enabled)
    
    suspend fun updatePrivacySettings(settings: PrivacySettings) = 
        settingsRepository.updatePrivacySettings(settings)
    
    suspend fun updateAnalytics(enabled: Boolean) = 
        settingsRepository.updateAnalytics(enabled)
    
    suspend fun updateCrashReporting(enabled: Boolean) = 
        settingsRepository.updateCrashReporting(enabled)
    
    suspend fun updateBackupEnabled(enabled: Boolean) = 
        settingsRepository.updateBackupEnabled(enabled)
    
    suspend fun updateBackupFrequency(frequency: BackupFrequency) = 
        settingsRepository.updateBackupFrequency(frequency)
    
    suspend fun updateDefaultTaskSection(section: String) = 
        settingsRepository.updateDefaultTaskSection(section)
    
    suspend fun updateDefaultTaskColumn(column: String) = 
        settingsRepository.updateDefaultTaskColumn(column)
    
    suspend fun updateAutoArchive(enabled: Boolean) = 
        settingsRepository.updateAutoArchive(enabled)
    
    suspend fun updateArchiveAfterDays(days: Int) = 
        settingsRepository.updateArchiveAfterDays(days)
    
    suspend fun updateLanguage(language: String) = 
        settingsRepository.updateLanguage(language)
    
    suspend fun resetToDefaults() = 
        settingsRepository.resetToDefaults()
}
