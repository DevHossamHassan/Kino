package com.letsgotoperfection.kino.di

import com.letsgotoperfection.kino.data.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * App-level dependency injection module
 *
 * This module provides app-wide dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: com.letsgotoperfection.kino.core.database.dao.TaskDao
    ): TaskRepository {
        return TaskRepository(taskDao)
    }
}
