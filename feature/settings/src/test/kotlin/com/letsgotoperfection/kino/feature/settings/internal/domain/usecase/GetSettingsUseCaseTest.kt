package com.letsgotoperfection.kino.feature.settings.internal.domain.usecase

import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode
import com.letsgotoperfection.kino.feature.settings.internal.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.mockk
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
        settingsRepository = mockk()
        getSettingsUseCase = GetSettingsUseCase(settingsRepository)
    }
    
    @Test
    fun `invoke returns settings from repository`() = runTest {
        // Given
        val expectedSettings = createTestSettings()
        coEvery { settingsRepository.getSettings() } returns flowOf(expectedSettings)
        
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
