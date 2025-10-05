package com.letsgotoperfection.kino.feature.settings.internal.presentation.state

import androidx.compose.runtime.Immutable
import com.letsgotoperfection.kino.core.common.UiState
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings

@Immutable
data class SettingsUiState(
    val settings: AppSettings? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showResetDialog: Boolean = false
)

sealed interface SettingsAction {
    data class UpdateThemeMode(val mode: com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode) : SettingsAction
    data class UpdateDynamicColors(val enabled: Boolean) : SettingsAction
    data class UpdateFontScale(val scale: com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale) : SettingsAction
    data class UpdateNotificationEnabled(val enabled: Boolean) : SettingsAction
    data class UpdateTaskReminders(val enabled: Boolean) : SettingsAction
    data class UpdateSmartSuggestions(val enabled: Boolean) : SettingsAction
    data class UpdateAchievements(val enabled: Boolean) : SettingsAction
    data class UpdateNoteReminders(val enabled: Boolean) : SettingsAction
    data class UpdateQuietHours(val enabled: Boolean, val start: java.time.LocalTime?, val end: java.time.LocalTime?) : SettingsAction
    data class UpdateNotificationFrequency(val frequency: com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency) : SettingsAction
    data class UpdateAiEnabled(val enabled: Boolean) : SettingsAction
    data class UpdateCloudAi(val enabled: Boolean) : SettingsAction
    data class UpdateAutoAnalyzeTasks(val enabled: Boolean) : SettingsAction
    data class UpdateSmartTaskBreakdown(val enabled: Boolean) : SettingsAction
    data class UpdateGamificationEnabled(val enabled: Boolean) : SettingsAction
    data class UpdateShowStreaks(val enabled: Boolean) : SettingsAction
    data class UpdateShowAchievements(val enabled: Boolean) : SettingsAction
    data class UpdateShowCelebrations(val enabled: Boolean) : SettingsAction
    data class UpdateAnalytics(val enabled: Boolean) : SettingsAction
    data class UpdateCrashReporting(val enabled: Boolean) : SettingsAction
    data class UpdateBackupEnabled(val enabled: Boolean) : SettingsAction
    data class UpdateBackupFrequency(val frequency: com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency) : SettingsAction
    data class UpdateDefaultTaskSection(val section: String) : SettingsAction
    data class UpdateDefaultTaskColumn(val column: String) : SettingsAction
    data class UpdateAutoArchive(val enabled: Boolean) : SettingsAction
    data class UpdateArchiveAfterDays(val days: Int) : SettingsAction
    data class UpdateLanguage(val language: String) : SettingsAction
    data object ShowResetDialog : SettingsAction
    data object HideResetDialog : SettingsAction
    data object ResetToDefaults : SettingsAction
    data object OpenNotificationSettings : SettingsAction
    data class OpenChannelSettings(val channelId: String) : SettingsAction
}

sealed interface SettingsUiEvent {
    data class ShowError(val message: String) : SettingsUiEvent
    data class ShowSuccess(val message: String) : SettingsUiEvent
    data object NavigateBack : SettingsUiEvent
}
