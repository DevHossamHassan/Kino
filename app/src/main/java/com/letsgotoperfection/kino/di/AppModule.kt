package com.letsgotoperfection.kino.di

import android.content.Context
import com.letsgotoperfection.kino.core.database.dao.LabelDao
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.data.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * App-level dependency injection module
 *
 * This module provides app-wide dependencies. Feature APIs (NotesApi,
 * MediaApi, KanbanApi, ...) are bound inside their own feature modules.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao,
        labelDao: LabelDao
    ): TaskRepository {
        return TaskRepository(taskDao, labelDao)
    }
}
