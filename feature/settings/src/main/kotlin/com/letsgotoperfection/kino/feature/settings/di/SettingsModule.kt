package com.letsgotoperfection.kino.feature.settings.di

import android.content.Context
import com.letsgotoperfection.kino.feature.settings.api.SettingsApi
import com.letsgotoperfection.kino.feature.settings.internal.api.SettingsApiImpl
import com.letsgotoperfection.kino.feature.settings.internal.data.datastore.SettingsDataStore
import com.letsgotoperfection.kino.feature.settings.internal.data.repository.SettingsRepositoryImpl
import com.letsgotoperfection.kino.feature.settings.internal.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Settings feature dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindSettingsApi(
        settingsApiImpl: SettingsApiImpl
    ): SettingsApi

    companion object {
        @Provides
        @Singleton
        fun provideSettingsDataStore(
            @ApplicationContext context: Context
        ): SettingsDataStore {
            return SettingsDataStore(context)
        }
    }
}