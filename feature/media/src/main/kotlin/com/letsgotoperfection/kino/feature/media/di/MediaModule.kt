package com.letsgotoperfection.kino.feature.media.di

import com.letsgotoperfection.kino.feature.media.api.MediaApi
import com.letsgotoperfection.kino.feature.media.internal.api.MediaApiImpl
import com.letsgotoperfection.kino.feature.media.internal.data.repository.MediaRepositoryImpl
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for the media feature.
 *
 * MediaDao is provided by the core database module; use cases use
 * constructor injection, so only the repository and public API need bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class MediaModule {

    @Binds
    @Singleton
    abstract fun bindMediaRepository(impl: MediaRepositoryImpl): MediaRepository

    @Binds
    @Singleton
    abstract fun bindMediaApi(impl: MediaApiImpl): MediaApi
}
