package com.letsgotoperfection.kino.feature.settings.internal.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object PreferencesKeys {
    // Theme
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val USE_DYNAMIC_COLORS = booleanPreferencesKey("use_dynamic_colors")
    val FONT_SCALE = floatPreferencesKey("font_scale")
    
    // Notifications
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val TASK_REMINDERS = booleanPreferencesKey("task_reminders")
    val SMART_SUGGESTIONS = booleanPreferencesKey("smart_suggestions")
    val ACHIEVEMENTS = booleanPreferencesKey("achievements")
    val NOTE_REMINDERS = booleanPreferencesKey("note_reminders")
    val RECURRING_TASKS = booleanPreferencesKey("recurring_tasks")
    val QUIET_HOURS_ENABLED = booleanPreferencesKey("quiet_hours_enabled")
    val QUIET_HOURS_START = stringPreferencesKey("quiet_hours_start")
    val QUIET_HOURS_END = stringPreferencesKey("quiet_hours_end")
    val NOTIFICATION_FREQUENCY = stringPreferencesKey("notification_frequency")
    
    // AI
    val AI_ENABLED = booleanPreferencesKey("ai_enabled")
    val USE_CLOUD_AI = booleanPreferencesKey("use_cloud_ai")
    val AUTO_ANALYZE_TASKS = booleanPreferencesKey("auto_analyze_tasks")
    val SMART_TASK_BREAKDOWN = booleanPreferencesKey("smart_task_breakdown")
    
    // Gamification
    val GAMIFICATION_ENABLED = booleanPreferencesKey("gamification_enabled")
    val SHOW_STREAKS = booleanPreferencesKey("show_streaks")
    val SHOW_ACHIEVEMENTS = booleanPreferencesKey("show_achievements")
    val SHOW_CELEBRATIONS = booleanPreferencesKey("show_celebrations")
    
    // Privacy
    val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
    val CRASH_REPORTING = booleanPreferencesKey("crash_reporting")
    val BACKUP_ENABLED = booleanPreferencesKey("backup_enabled")
    val BACKUP_FREQUENCY = stringPreferencesKey("backup_frequency")
    
    // General
    val DEFAULT_TASK_SECTION = stringPreferencesKey("default_task_section")
    val DEFAULT_TASK_COLUMN = stringPreferencesKey("default_task_column")
    val AUTO_ARCHIVE = booleanPreferencesKey("auto_archive")
    val ARCHIVE_AFTER_DAYS = intPreferencesKey("archive_after_days")
    val LANGUAGE = stringPreferencesKey("language")
}
