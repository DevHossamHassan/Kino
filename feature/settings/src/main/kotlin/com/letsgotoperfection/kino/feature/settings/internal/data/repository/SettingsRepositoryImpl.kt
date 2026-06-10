package com.letsgotoperfection.kino.feature.settings.internal.data.repository

import com.letsgotoperfection.kino.core.common.di.IoDispatcher
import com.letsgotoperfection.kino.feature.settings.internal.data.datastore.SettingsDataStore
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.GamificationSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.PrivacySettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode
import com.letsgotoperfection.kino.feature.settings.internal.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SettingsRepository {
    
    override fun getSettings(): Flow<com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings> {
        return settingsDataStore.settingsFlow
            .flowOn(ioDispatcher)
    }
    
    override suspend fun updateThemeMode(mode: ThemeMode): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateThemeMode(mode)
            }
        }
    
    override suspend fun updateDynamicColors(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateDynamicColors(enabled)
            }
        }
    
    override suspend fun updateFontScale(scale: FontScale): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateFontScale(scale)
            }
        }
    
    override suspend fun updateNotificationSettings(
        settings: NotificationSettings
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            settingsDataStore.updateNotificationEnabled(settings.enabled)
            settingsDataStore.updateTaskReminders(settings.taskReminders)
            settingsDataStore.updateSmartSuggestions(settings.smartSuggestions)
            settingsDataStore.updateAchievements(settings.achievements)
            settingsDataStore.updateNoteReminders(settings.noteReminders)
            settingsDataStore.updateRecurringTasks(settings.recurringTasks)
            settingsDataStore.updateQuietHours(
                enabled = settings.quietHoursEnabled,
                start = settings.quietHoursStart,
                end = settings.quietHoursEnd
            )
            settingsDataStore.updateNotificationFrequency(settings.notificationFrequency)
        }
    }
    
    override suspend fun updateNotificationEnabled(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateNotificationEnabled(enabled)
            }
        }
    
    override suspend fun updateTaskReminders(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateTaskReminders(enabled)
            }
        }
    
    override suspend fun updateSmartSuggestions(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateSmartSuggestions(enabled)
            }
        }
    
    override suspend fun updateAchievements(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateAchievements(enabled)
            }
        }
    
    override suspend fun updateNoteReminders(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateNoteReminders(enabled)
            }
        }
    
    override suspend fun updateRecurringTasks(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateRecurringTasks(enabled)
            }
        }
    
    override suspend fun updateQuietHours(
        enabled: Boolean, 
        start: java.time.LocalTime?, 
        end: java.time.LocalTime?
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            settingsDataStore.updateQuietHours(enabled, start, end)
        }
    }
    
    override suspend fun updateNotificationFrequency(frequency: NotificationFrequency): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateNotificationFrequency(frequency)
            }
        }
    
    override suspend fun updateAiSettings(settings: AiSettings): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateAiEnabled(settings.enableAiAnalysis)
                settingsDataStore.updateCloudAi(settings.useCloudAi)
                settingsDataStore.updateAutoAnalyzeTasks(settings.autoAnalyzeTasks)
                settingsDataStore.updateSmartTaskBreakdown(settings.smartTaskBreakdown)
            }
        }
    
    override suspend fun updateAiEnabled(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateAiEnabled(enabled)
            }
        }
    
    override suspend fun updateCloudAi(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateCloudAi(enabled)
            }
        }
    
    override suspend fun updateAutoAnalyzeTasks(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateAutoAnalyzeTasks(enabled)
            }
        }
    
    override suspend fun updateSmartTaskBreakdown(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateSmartTaskBreakdown(enabled)
            }
        }
    
    override suspend fun updateGamificationSettings(settings: GamificationSettings): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateGamificationEnabled(settings.enabled)
                settingsDataStore.updateShowStreaks(settings.showStreaks)
                settingsDataStore.updateShowAchievements(settings.showAchievements)
                settingsDataStore.updateShowCelebrations(settings.showProgressCelebrations)
            }
        }
    
    override suspend fun updateGamificationEnabled(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateGamificationEnabled(enabled)
            }
        }
    
    override suspend fun updateShowStreaks(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateShowStreaks(enabled)
            }
        }
    
    override suspend fun updateShowAchievements(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateShowAchievements(enabled)
            }
        }
    
    override suspend fun updateShowCelebrations(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateShowCelebrations(enabled)
            }
        }
    
    override suspend fun updatePrivacySettings(settings: PrivacySettings): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateAnalytics(settings.analyticsEnabled)
                settingsDataStore.updateCrashReporting(settings.crashReportingEnabled)
                settingsDataStore.updateBackupEnabled(settings.backupEnabled)
                settingsDataStore.updateBackupFrequency(settings.backupFrequency)
            }
        }
    
    override suspend fun updateAnalytics(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateAnalytics(enabled)
            }
        }
    
    override suspend fun updateCrashReporting(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateCrashReporting(enabled)
            }
        }
    
    override suspend fun updateBackupEnabled(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateBackupEnabled(enabled)
            }
        }
    
    override suspend fun updateBackupFrequency(frequency: BackupFrequency): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateBackupFrequency(frequency)
            }
        }
    
    override suspend fun updateDefaultTaskSection(section: String): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateDefaultTaskSection(section)
            }
        }
    
    override suspend fun updateDefaultTaskColumn(column: String): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateDefaultTaskColumn(column)
            }
        }
    
    override suspend fun updateAutoArchive(enabled: Boolean): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateAutoArchive(enabled)
            }
        }
    
    override suspend fun updateArchiveAfterDays(days: Int): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateArchiveAfterDays(days)
            }
        }
    
    override suspend fun updateLanguage(language: String): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.updateLanguage(language)
            }
        }
    
    override suspend fun resetToDefaults(): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                settingsDataStore.clearAllSettings()
            }
        }
}
