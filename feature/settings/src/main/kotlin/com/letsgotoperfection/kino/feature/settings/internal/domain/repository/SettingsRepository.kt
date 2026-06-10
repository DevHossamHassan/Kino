package com.letsgotoperfection.kino.feature.settings.internal.domain.repository

import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.GamificationSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.PrivacySettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    
    suspend fun updateThemeMode(mode: ThemeMode): Result<Unit>
    suspend fun updateDynamicColors(enabled: Boolean): Result<Unit>
    suspend fun updateFontScale(scale: FontScale): Result<Unit>
    
    suspend fun updateNotificationSettings(settings: NotificationSettings): Result<Unit>
    suspend fun updateNotificationEnabled(enabled: Boolean): Result<Unit>
    suspend fun updateTaskReminders(enabled: Boolean): Result<Unit>
    suspend fun updateSmartSuggestions(enabled: Boolean): Result<Unit>
    suspend fun updateAchievements(enabled: Boolean): Result<Unit>
    suspend fun updateNoteReminders(enabled: Boolean): Result<Unit>
    suspend fun updateRecurringTasks(enabled: Boolean): Result<Unit>
    suspend fun updateQuietHours(enabled: Boolean, start: java.time.LocalTime?, end: java.time.LocalTime?): Result<Unit>
    suspend fun updateNotificationFrequency(frequency: NotificationFrequency): Result<Unit>
    
    suspend fun updateAiSettings(settings: AiSettings): Result<Unit>
    suspend fun updateAiEnabled(enabled: Boolean): Result<Unit>
    suspend fun updateCloudAi(enabled: Boolean): Result<Unit>
    suspend fun updateAutoAnalyzeTasks(enabled: Boolean): Result<Unit>
    suspend fun updateSmartTaskBreakdown(enabled: Boolean): Result<Unit>
    
    suspend fun updateGamificationSettings(settings: GamificationSettings): Result<Unit>
    suspend fun updateGamificationEnabled(enabled: Boolean): Result<Unit>
    suspend fun updateShowStreaks(enabled: Boolean): Result<Unit>
    suspend fun updateShowAchievements(enabled: Boolean): Result<Unit>
    suspend fun updateShowCelebrations(enabled: Boolean): Result<Unit>
    
    suspend fun updatePrivacySettings(settings: PrivacySettings): Result<Unit>
    suspend fun updateAnalytics(enabled: Boolean): Result<Unit>
    suspend fun updateCrashReporting(enabled: Boolean): Result<Unit>
    suspend fun updateBackupEnabled(enabled: Boolean): Result<Unit>
    suspend fun updateBackupFrequency(frequency: BackupFrequency): Result<Unit>
    
    suspend fun updateDefaultTaskSection(section: String): Result<Unit>
    suspend fun updateDefaultTaskColumn(column: String): Result<Unit>
    suspend fun updateAutoArchive(enabled: Boolean): Result<Unit>
    suspend fun updateArchiveAfterDays(days: Int): Result<Unit>
    suspend fun updateLanguage(language: String): Result<Unit>
    
    suspend fun resetToDefaults(): Result<Unit>
}
