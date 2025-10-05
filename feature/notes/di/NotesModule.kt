package com.letsgotoperfection.kino.feature.notes.di

import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import com.letsgotoperfection.kino.feature.notes.internal.api.NotesApiImpl
import com.letsgotoperfection.kino.feature.notes.internal.data.repository.NotesRepositoryImpl
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Notes feature dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NotesModule {
    
    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        impl: NotesRepositoryImpl
    ): NotesRepository
    
    @Binds
    @Singleton
    abstract fun bindNotesApi(
        impl: NotesApiImpl
    ): NotesApi
}
