package com.letsgotoperfection.kino.feature.settings.internal.domain.usecase

import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode
import com.letsgotoperfection.kino.feature.settings.internal.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetSettingsUseCaseTest {
    
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var getSettingsUseCase: GetSettingsUseCase
    
    @BeforeEach
    fun setup() {
        settingsRepository = FakeSettingsRepository()
        getSettingsUseCase = GetSettingsUseCase(settingsRepository)
    }
    
    @Test
    fun `invoke returns settings from repository`() = runTest {
        // Given
        val expectedSettings = createTestSettings()
        (settingsRepository as FakeSettingsRepository).setSettings(expectedSettings)
        
        // When
        val result = getSettingsUseCase()
        
        // Then
        result.collect { settings ->
            assertEquals(expectedSettings, settings)
        }
    }
    
    private fun createTestSettings() = AppSettings(
        theme = com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeSettings(
            themeMode = ThemeMode.SYSTEM,
            useDynamicColors = true,
            fontSize = FontScale.NORMAL
        ),
        notifications = com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings(
            enabled = true,
            taskReminders = true,
            smartSuggestions = true,
            achievements = true,
            noteReminders = false,
            recurringTasks = true,
            quietHoursEnabled = false,
            quietHoursStart = java.time.LocalTime.of(22, 0),
            quietHoursEnd = java.time.LocalTime.of(7, 0),
            notificationFrequency = NotificationFrequency.MEDIUM
        ),
        ai = com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings(
            enableAiAnalysis = true,
            useCloudAi = false,
            autoAnalyzeTasks = true,
            smartTaskBreakdown = true
        ),
        gamification = com.letsgotoperfection.kino.feature.settings.internal.domain.model.GamificationSettings(
            enabled = true,
            showStreaks = true,
            showAchievements = true,
            showProgressCelebrations = true
        ),
        privacy = com.letsgotoperfection.kino.feature.settings.internal.domain.model.PrivacySettings(
            analyticsEnabled = true,
            crashReportingEnabled = true,
            backupEnabled = false,
            backupFrequency = com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency.WEEKLY
        ),
        general = com.letsgotoperfection.kino.feature.settings.internal.domain.model.GeneralSettings(
            defaultTaskSection = "personal",
            defaultTaskColumn = "todo_this_week",
            autoArchiveCompleted = false,
            archiveAfterDays = 30,
            language = "en"
        )
    )
}

/**
 * Fake implementation of SettingsRepository for testing
 */
class FakeSettingsRepository : SettingsRepository {
    private var settings: AppSettings = createDefaultSettings()
    
    fun setSettings(settings: AppSettings) {
        this.settings = settings
    }
    
    override fun getSettings(): kotlinx.coroutines.flow.Flow<AppSettings> {
        return flowOf(settings)
    }
    
    override suspend fun updateThemeMode(mode: com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateDynamicColors(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateFontScale(scale: com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateNotificationSettings(settings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateNotificationEnabled(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateTaskReminders(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateSmartSuggestions(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateAchievements(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateNoteReminders(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateRecurringTasks(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateQuietHours(enabled: Boolean, start: java.time.LocalTime?, end: java.time.LocalTime?): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateNotificationFrequency(frequency: com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateAiSettings(settings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateAiEnabled(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateCloudAi(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateAutoAnalyzeTasks(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateSmartTaskBreakdown(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateGamificationSettings(settings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.GamificationSettings): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateGamificationEnabled(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateShowStreaks(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateShowAchievements(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateShowCelebrations(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updatePrivacySettings(settings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.PrivacySettings): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateAnalytics(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateCrashReporting(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateBackupEnabled(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateBackupFrequency(frequency: com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateDefaultTaskSection(section: String): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateDefaultTaskColumn(column: String): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateAutoArchive(enabled: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateArchiveAfterDays(days: Int): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun updateLanguage(language: String): Result<Unit> {
        return Result.success(Unit)
    }
    
    override suspend fun resetToDefaults(): Result<Unit> {
        return Result.success(Unit)
    }
    
    private fun createDefaultSettings() = AppSettings(
        theme = com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeSettings(
            themeMode = ThemeMode.SYSTEM,
            useDynamicColors = true,
            fontSize = FontScale.NORMAL
        ),
        notifications = com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings(
            enabled = true,
            taskReminders = true,
            smartSuggestions = true,
            achievements = true,
            noteReminders = false,
            recurringTasks = true,
            quietHoursEnabled = false,
            quietHoursStart = java.time.LocalTime.of(22, 0),
            quietHoursEnd = java.time.LocalTime.of(7, 0),
            notificationFrequency = NotificationFrequency.MEDIUM
        ),
        ai = com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings(
            enableAiAnalysis = true,
            useCloudAi = false,
            autoAnalyzeTasks = true,
            smartTaskBreakdown = true
        ),
        gamification = com.letsgotoperfection.kino.feature.settings.internal.domain.model.GamificationSettings(
            enabled = true,
            showStreaks = true,
            showAchievements = true,
            showProgressCelebrations = true
        ),
        privacy = com.letsgotoperfection.kino.feature.settings.internal.domain.model.PrivacySettings(
            analyticsEnabled = true,
            crashReportingEnabled = true,
            backupEnabled = false,
            backupFrequency = com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency.WEEKLY
        ),
        general = com.letsgotoperfection.kino.feature.settings.internal.domain.model.GeneralSettings(
            defaultTaskSection = "personal",
            defaultTaskColumn = "todo_this_week",
            autoArchiveCompleted = false,
            archiveAfterDays = 30,
            language = "en"
        )
    )
}
