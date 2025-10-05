package com.letsgotoperfection.kino.feature.settings.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.feature.settings.internal.domain.usecase.GetSettingsUseCase
import com.letsgotoperfection.kino.feature.settings.internal.domain.usecase.UpdateSettingsUseCase
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsAction
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsUiEvent
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState(isLoading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent: SharedFlow<SettingsUiEvent> = _uiEvent.asSharedFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            getSettingsUseCase()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load settings"
                    )
                }
                .collect { settings ->
                    _uiState.value = _uiState.value.copy(
                        settings = settings,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }
    
    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.UpdateThemeMode -> updateThemeMode(action.mode)
            is SettingsAction.UpdateDynamicColors -> updateDynamicColors(action.enabled)
            is SettingsAction.UpdateFontScale -> updateFontScale(action.scale)
            is SettingsAction.UpdateNotificationEnabled -> updateNotificationEnabled(action.enabled)
            is SettingsAction.UpdateTaskReminders -> updateTaskReminders(action.enabled)
            is SettingsAction.UpdateSmartSuggestions -> updateSmartSuggestions(action.enabled)
            is SettingsAction.UpdateAchievements -> updateAchievements(action.enabled)
            is SettingsAction.UpdateNoteReminders -> updateNoteReminders(action.enabled)
            is SettingsAction.UpdateQuietHours -> updateQuietHours(action.enabled, action.start, action.end)
            is SettingsAction.UpdateNotificationFrequency -> updateNotificationFrequency(action.frequency)
            is SettingsAction.UpdateAiEnabled -> updateAiEnabled(action.enabled)
            is SettingsAction.UpdateCloudAi -> updateCloudAi(action.enabled)
            is SettingsAction.UpdateAutoAnalyzeTasks -> updateAutoAnalyzeTasks(action.enabled)
            is SettingsAction.UpdateSmartTaskBreakdown -> updateSmartTaskBreakdown(action.enabled)
            is SettingsAction.UpdateGamificationEnabled -> updateGamificationEnabled(action.enabled)
            is SettingsAction.UpdateShowStreaks -> updateShowStreaks(action.enabled)
            is SettingsAction.UpdateShowAchievements -> updateShowAchievements(action.enabled)
            is SettingsAction.UpdateShowCelebrations -> updateShowCelebrations(action.enabled)
            is SettingsAction.UpdateAnalytics -> updateAnalytics(action.enabled)
            is SettingsAction.UpdateCrashReporting -> updateCrashReporting(action.enabled)
            is SettingsAction.UpdateBackupEnabled -> updateBackupEnabled(action.enabled)
            is SettingsAction.UpdateBackupFrequency -> updateBackupFrequency(action.frequency)
            is SettingsAction.UpdateDefaultTaskSection -> updateDefaultTaskSection(action.section)
            is SettingsAction.UpdateDefaultTaskColumn -> updateDefaultTaskColumn(action.column)
            is SettingsAction.UpdateAutoArchive -> updateAutoArchive(action.enabled)
            is SettingsAction.UpdateArchiveAfterDays -> updateArchiveAfterDays(action.days)
            is SettingsAction.UpdateLanguage -> updateLanguage(action.language)
            is SettingsAction.ShowResetDialog -> showResetDialog()
            is SettingsAction.HideResetDialog -> hideResetDialog()
            is SettingsAction.ResetToDefaults -> resetToDefaults()
            is SettingsAction.OpenNotificationSettings -> openNotificationSettings()
            is SettingsAction.OpenChannelSettings -> openChannelSettings(action.channelId)
        }
    }
    
    private fun updateThemeMode(mode: com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode) {
        viewModelScope.launch {
            updateSettingsUseCase.updateThemeMode(mode)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update theme"))
                }
        }
    }
    
    private fun updateDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateDynamicColors(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update dynamic colors"))
                }
        }
    }
    
    private fun updateFontScale(scale: com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale) {
        viewModelScope.launch {
            updateSettingsUseCase.updateFontScale(scale)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update font scale"))
                }
        }
    }
    
    private fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateNotificationEnabled(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update notifications"))
                }
        }
    }
    
    private fun updateTaskReminders(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateTaskReminders(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update task reminders"))
                }
        }
    }
    
    private fun updateSmartSuggestions(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateSmartSuggestions(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update smart suggestions"))
                }
        }
    }
    
    private fun updateAchievements(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateAchievements(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update achievements"))
                }
        }
    }
    
    private fun updateNoteReminders(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateNoteReminders(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update note reminders"))
                }
        }
    }
    
    private fun updateQuietHours(enabled: Boolean, start: java.time.LocalTime?, end: java.time.LocalTime?) {
        viewModelScope.launch {
            updateSettingsUseCase.updateQuietHours(enabled, start, end)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update quiet hours"))
                }
        }
    }
    
    private fun updateNotificationFrequency(frequency: com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency) {
        viewModelScope.launch {
            updateSettingsUseCase.updateNotificationFrequency(frequency)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update notification frequency"))
                }
        }
    }
    
    private fun updateAiEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateAiEnabled(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update AI settings"))
                }
        }
    }
    
    private fun updateCloudAi(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateCloudAi(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update cloud AI"))
                }
        }
    }
    
    private fun updateAutoAnalyzeTasks(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateAutoAnalyzeTasks(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update auto analyze"))
                }
        }
    }
    
    private fun updateSmartTaskBreakdown(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateSmartTaskBreakdown(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update smart task breakdown"))
                }
        }
    }
    
    private fun updateGamificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateGamificationEnabled(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update gamification"))
                }
        }
    }
    
    private fun updateShowStreaks(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateShowStreaks(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update streaks"))
                }
        }
    }
    
    private fun updateShowAchievements(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateShowAchievements(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update achievements"))
                }
        }
    }
    
    private fun updateShowCelebrations(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateShowCelebrations(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update celebrations"))
                }
        }
    }
    
    private fun updateAnalytics(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateAnalytics(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update analytics"))
                }
        }
    }
    
    private fun updateCrashReporting(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateCrashReporting(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update crash reporting"))
                }
        }
    }
    
    private fun updateBackupEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateBackupEnabled(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update backup"))
                }
        }
    }
    
    private fun updateBackupFrequency(frequency: com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency) {
        viewModelScope.launch {
            updateSettingsUseCase.updateBackupFrequency(frequency)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update backup frequency"))
                }
        }
    }
    
    private fun updateDefaultTaskSection(section: String) {
        viewModelScope.launch {
            updateSettingsUseCase.updateDefaultTaskSection(section)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update default task section"))
                }
        }
    }
    
    private fun updateDefaultTaskColumn(column: String) {
        viewModelScope.launch {
            updateSettingsUseCase.updateDefaultTaskColumn(column)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update default task column"))
                }
        }
    }
    
    private fun updateAutoArchive(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateAutoArchive(enabled)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update auto archive"))
                }
        }
    }
    
    private fun updateArchiveAfterDays(days: Int) {
        viewModelScope.launch {
            updateSettingsUseCase.updateArchiveAfterDays(days)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update archive days"))
                }
        }
    }
    
    private fun updateLanguage(language: String) {
        viewModelScope.launch {
            updateSettingsUseCase.updateLanguage(language)
                .onFailure { error ->
                    _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to update language"))
                }
        }
    }
    
    private fun showResetDialog() {
        _uiState.value = _uiState.value.copy(showResetDialog = true)
    }
    
    private fun hideResetDialog() {
        _uiState.value = _uiState.value.copy(showResetDialog = false)
    }
    
    private fun resetToDefaults() {
        viewModelScope.launch {
            updateSettingsUseCase.resetToDefaults()
                .fold(
                    onSuccess = {
                        _uiEvent.emit(SettingsUiEvent.ShowSuccess("Settings reset to defaults"))
                        hideResetDialog()
                    },
                    onFailure = { error ->
                        _uiEvent.emit(SettingsUiEvent.ShowError(error.message ?: "Failed to reset settings"))
                    }
                )
        }
    }
    
    private fun openNotificationSettings() {
        // This would typically open the system notification settings
        // Implementation depends on the notification module
    }
    
    private fun openChannelSettings(channelId: String) {
        // This would typically open the specific notification channel settings
        // Implementation depends on the notification module
    }
}
