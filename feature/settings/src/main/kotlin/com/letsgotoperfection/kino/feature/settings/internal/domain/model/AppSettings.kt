package com.letsgotoperfection.kino.feature.settings.internal.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalTime

/**
 * Complete app settings model
 */
@Immutable
data class AppSettings(
    val theme: ThemeSettings,
    val notifications: NotificationSettings,
    val ai: AiSettings,
    val gamification: GamificationSettings,
    val privacy: PrivacySettings,
    val general: GeneralSettings
)

/**
 * Theme and appearance settings
 */
@Immutable
data class ThemeSettings(
    val themeMode: ThemeMode,
    val useDynamicColors: Boolean,  // Material You (Android 12+)
    val fontSize: FontScale
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;  // Follow system setting
    
    companion object {
        fun fromString(value: String): ThemeMode {
            return values().find { it.name == value } ?: SYSTEM
        }
    }
}

enum class FontScale(val scale: Float) {
    SMALL(0.85f),
    NORMAL(1.0f),
    LARGE(1.15f),
    EXTRA_LARGE(1.3f);
    
    companion object {
        fun fromScale(scale: Float): FontScale {
            return values().minByOrNull { kotlin.math.abs(it.scale - scale) } ?: NORMAL
        }
    }
}

/**
 * Notification preferences
 */
@Immutable
data class NotificationSettings(
    val enabled: Boolean,
    val taskReminders: Boolean,
    val smartSuggestions: Boolean,
    val achievements: Boolean,
    val noteReminders: Boolean,
    val quietHoursEnabled: Boolean,
    val quietHoursStart: LocalTime,
    val quietHoursEnd: LocalTime,
    val notificationFrequency: NotificationFrequency
)

enum class NotificationFrequency {
    LOW,      // Max 2 per day
    MEDIUM,   // Max 5 per day
    HIGH;     // Max 10 per day
    
    fun maxNotificationsPerDay(): Int = when (this) {
        LOW -> 2
        MEDIUM -> 5
        HIGH -> 10
    }
}

/**
 * AI and smart features settings
 */
@Immutable
data class AiSettings(
    val enableAiAnalysis: Boolean,
    val useCloudAi: Boolean,  // vs on-device
    val autoAnalyzeTasks: Boolean,
    val smartTaskBreakdown: Boolean
)

/**
 * Gamification settings
 */
@Immutable
data class GamificationSettings(
    val enabled: Boolean,
    val showStreaks: Boolean,
    val showAchievements: Boolean,
    val showProgressCelebrations: Boolean
)

/**
 * Privacy settings
 */
@Immutable
data class PrivacySettings(
    val analyticsEnabled: Boolean,
    val crashReportingEnabled: Boolean,
    val backupEnabled: Boolean,
    val backupFrequency: BackupFrequency
)

enum class BackupFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    MANUAL
}

/**
 * General settings
 */
@Immutable
data class GeneralSettings(
    val defaultTaskSection: String,  // personal, work, family
    val defaultTaskColumn: String,   // backlog, todo, etc.
    val autoArchiveCompleted: Boolean,
    val archiveAfterDays: Int,
    val language: String
)
