package com.letsgotoperfection.kino.di

import android.content.Context
import com.letsgotoperfection.kino.data.TaskRepository
import com.letsgotoperfection.kino.feature.media.api.MediaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideContext(@ApplicationContext context: Context): Context = context
    
    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: com.letsgotoperfection.kino.core.database.dao.TaskDao,
        labelDao: com.letsgotoperfection.kino.core.database.dao.LabelDao
    ): TaskRepository {
        return TaskRepository(taskDao, labelDao)
    }
    
    @Provides
    @Singleton
    fun provideMediaApi(): MediaApi {
        // Return a simple implementation for now
        return object : MediaApi {
            override suspend fun getMedia(mediaId: String): Result<com.letsgotoperfection.kino.feature.media.api.Media> {
                return Result.failure(Exception("MediaApi not implemented"))
            }
            
            override suspend fun attachMedia(
                uri: android.net.Uri,
                sourceType: com.letsgotoperfection.kino.feature.media.api.MediaSourceType,
                sourceId: String
            ): Result<com.letsgotoperfection.kino.feature.media.api.Media> {
                return Result.failure(Exception("MediaApi not implemented"))
            }
            
            override suspend fun attachMedia(
                uris: List<android.net.Uri>,
                sourceType: com.letsgotoperfection.kino.feature.media.api.MediaSourceType,
                sourceId: String
            ): List<Result<com.letsgotoperfection.kino.feature.media.api.Media>> {
                return uris.map { Result.failure(Exception("MediaApi not implemented")) }
            }
            
            override suspend fun deleteMedia(mediaId: String): Result<Unit> {
                return Result.failure(Exception("MediaApi not implemented"))
            }
            
            override suspend fun getMediaCount(
                sourceType: com.letsgotoperfection.kino.feature.media.api.MediaSourceType,
                sourceId: String
            ): Int {
                return 0
            }
            
            override fun hasMediaPermissions(): Boolean {
                return false
            }
        }
    }
}
