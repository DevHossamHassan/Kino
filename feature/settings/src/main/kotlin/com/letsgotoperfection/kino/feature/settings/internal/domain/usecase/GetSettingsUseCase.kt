package com.letsgotoperfection.kino.feature.settings.internal.domain.usecase

import com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings
import com.letsgotoperfection.kino.feature.settings.internal.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        return settingsRepository.getSettings()
    }
}
