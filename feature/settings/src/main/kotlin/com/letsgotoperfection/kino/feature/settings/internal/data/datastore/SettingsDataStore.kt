package com.letsgotoperfection.kino.feature.settings.internal.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
    
    val settingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapPreferencesToSettings(preferences)
        }
    
    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }
    
    suspend fun updateDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_COLORS] = enabled
        }
    }
    
    suspend fun updateFontScale(scale: FontScale) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SCALE] = scale.scale
        }
    }
    
    suspend fun updateNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    suspend fun updateTaskReminders(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TASK_REMINDERS] = enabled
        }
    }
    
    suspend fun updateSmartSuggestions(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SMART_SUGGESTIONS] = enabled
        }
    }
    
    suspend fun updateAchievements(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACHIEVEMENTS] = enabled
        }
    }
    
    suspend fun updateNoteReminders(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTE_REMINDERS] = enabled
        }
    }
    
    suspend fun updateQuietHours(
        enabled: Boolean,
        start: LocalTime? = null,
        end: LocalTime? = null
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.QUIET_HOURS_ENABLED] = enabled
            start?.let {
                preferences[PreferencesKeys.QUIET_HOURS_START] = it.toString()
            }
            end?.let {
                preferences[PreferencesKeys.QUIET_HOURS_END] = it.toString()
            }
        }
    }
    
    suspend fun updateNotificationFrequency(frequency: NotificationFrequency) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_FREQUENCY] = frequency.name
        }
    }
    
    suspend fun updateAiEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AI_ENABLED] = enabled
        }
    }
    
    suspend fun updateCloudAi(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_CLOUD_AI] = enabled
        }
    }
    
    suspend fun updateAutoAnalyzeTasks(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_ANALYZE_TASKS] = enabled
        }
    }
    
    suspend fun updateSmartTaskBreakdown(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SMART_TASK_BREAKDOWN] = enabled
        }
    }
    
    suspend fun updateGamificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GAMIFICATION_ENABLED] = enabled
        }
    }
    
    suspend fun updateShowStreaks(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_STREAKS] = enabled
        }
    }
    
    suspend fun updateShowAchievements(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ACHIEVEMENTS] = enabled
        }
    }
    
    suspend fun updateShowCelebrations(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_CELEBRATIONS] = enabled
        }
    }
    
    suspend fun updateAnalytics(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANALYTICS_ENABLED] = enabled
        }
    }
    
    suspend fun updateCrashReporting(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CRASH_REPORTING] = enabled
        }
    }
    
    suspend fun updateBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKUP_ENABLED] = enabled
        }
    }
    
    suspend fun updateBackupFrequency(frequency: BackupFrequency) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKUP_FREQUENCY] = frequency.name
        }
    }
    
    suspend fun updateDefaultTaskSection(section: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_TASK_SECTION] = section
        }
    }
    
    suspend fun updateDefaultTaskColumn(column: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_TASK_COLUMN] = column
        }
    }
    
    suspend fun updateAutoArchive(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_ARCHIVE] = enabled
        }
    }
    
    suspend fun updateArchiveAfterDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ARCHIVE_AFTER_DAYS] = days
        }
    }
    
    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language
        }
    }
    
    suspend fun clearAllSettings() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    private fun mapPreferencesToSettings(preferences: Preferences): AppSettings {
        return AppSettings(
            theme = com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeSettings(
                themeMode = ThemeMode.fromString(
                    preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
                ),
                useDynamicColors = preferences[PreferencesKeys.USE_DYNAMIC_COLORS] ?: true,
                fontSize = FontScale.fromScale(
                    preferences[PreferencesKeys.FONT_SCALE] ?: 1.0f
                )
            ),
            notifications = com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings(
                enabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
                taskReminders = preferences[PreferencesKeys.TASK_REMINDERS] ?: true,
                smartSuggestions = preferences[PreferencesKeys.SMART_SUGGESTIONS] ?: true,
                achievements = preferences[PreferencesKeys.ACHIEVEMENTS] ?: true,
                noteReminders = preferences[PreferencesKeys.NOTE_REMINDERS] ?: false,
                quietHoursEnabled = preferences[PreferencesKeys.QUIET_HOURS_ENABLED] ?: false,
                quietHoursStart = preferences[PreferencesKeys.QUIET_HOURS_START]?.let {
                    LocalTime.parse(it)
                } ?: LocalTime.of(22, 0),
                quietHoursEnd = preferences[PreferencesKeys.QUIET_HOURS_END]?.let {
                    LocalTime.parse(it)
                } ?: LocalTime.of(7, 0),
                notificationFrequency = preferences[PreferencesKeys.NOTIFICATION_FREQUENCY]?.let {
                    NotificationFrequency.valueOf(it)
                } ?: NotificationFrequency.MEDIUM
            ),
            ai = com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings(
                enableAiAnalysis = preferences[PreferencesKeys.AI_ENABLED] ?: true,
                useCloudAi = preferences[PreferencesKeys.USE_CLOUD_AI] ?: false,
                autoAnalyzeTasks = preferences[PreferencesKeys.AUTO_ANALYZE_TASKS] ?: true,
                smartTaskBreakdown = preferences[PreferencesKeys.SMART_TASK_BREAKDOWN] ?: true
            ),
            gamification = com.letsgotoperfection.kino.feature.settings.internal.domain.model.GamificationSettings(
                enabled = preferences[PreferencesKeys.GAMIFICATION_ENABLED] ?: true,
                showStreaks = preferences[PreferencesKeys.SHOW_STREAKS] ?: true,
                showAchievements = preferences[PreferencesKeys.SHOW_ACHIEVEMENTS] ?: true,
                showProgressCelebrations = preferences[PreferencesKeys.SHOW_CELEBRATIONS] ?: true
            ),
            privacy = com.letsgotoperfection.kino.feature.settings.internal.domain.model.PrivacySettings(
                analyticsEnabled = preferences[PreferencesKeys.ANALYTICS_ENABLED] ?: true,
                crashReportingEnabled = preferences[PreferencesKeys.CRASH_REPORTING] ?: true,
                backupEnabled = preferences[PreferencesKeys.BACKUP_ENABLED] ?: false,
                backupFrequency = preferences[PreferencesKeys.BACKUP_FREQUENCY]?.let {
                    BackupFrequency.valueOf(it)
                } ?: BackupFrequency.WEEKLY
            ),
            general = com.letsgotoperfection.kino.feature.settings.internal.domain.model.GeneralSettings(
                defaultTaskSection = preferences[PreferencesKeys.DEFAULT_TASK_SECTION] ?: "personal",
                defaultTaskColumn = preferences[PreferencesKeys.DEFAULT_TASK_COLUMN] ?: "todo_this_week",
                autoArchiveCompleted = preferences[PreferencesKeys.AUTO_ARCHIVE] ?: false,
                archiveAfterDays = preferences[PreferencesKeys.ARCHIVE_AFTER_DAYS] ?: 30,
                language = preferences[PreferencesKeys.LANGUAGE] ?: "en"
            )
        )
    }
}
