package com.letsgotoperfection.kino.feature.notes.di

import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import com.letsgotoperfection.kino.feature.notes.api.NotesApiBridge
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Notes feature dependencies.
 * Provides the public API implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NotesModule {

    @Binds
    @Singleton
    abstract fun bindNotesApi(impl: NotesApiBridge): NotesApi
}
