package com.letsgotoperfection.kino.feature.recurringtasks.internal.fake

import com.letsgotoperfection.kino.feature.settings.api.SettingsApi
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake of [SettingsApi] for unit tests; only the recurring task notification
 * toggle is configurable, everything else returns fixed defaults.
 */
internal class FakeSettingsApi : SettingsApi {

    val recurringTaskNotificationsEnabled = MutableStateFlow(true)

    override fun getSettings(): Flow<AppSettings> = emptyFlow()

    override fun getThemeSettings(): Flow<ThemeSettings> = emptyFlow()

    override fun getNotificationSettings(): Flow<NotificationSettings> = emptyFlow()

    override fun getAiSettings(): Flow<AiSettings> = emptyFlow()

    override fun isDarkThemeEnabled(): Flow<Boolean> = flowOf(false)

    override fun areNotificationsEnabled(): Flow<Boolean> = flowOf(true)

    override fun isAiEnabled(): Flow<Boolean> = flowOf(false)

    override fun isGamificationEnabled(): Flow<Boolean> = flowOf(false)

    override fun areRecurringTaskNotificationsEnabled(): Flow<Boolean> =
        recurringTaskNotificationsEnabled

    override suspend fun updateRecurringTaskNotifications(enabled: Boolean): Result<Unit> {
        recurringTaskNotificationsEnabled.value = enabled
        return Result.success(Unit)
    }
}
