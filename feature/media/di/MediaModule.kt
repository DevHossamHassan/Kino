package com.letsgotoperfection.kino.feature.media.di

import android.content.Context
import androidx.room.Room
import com.letsgotoperfection.kino.feature.media.api.MediaApi
import com.letsgotoperfection.kino.feature.media.internal.data.local.MediaDao
import com.letsgotoperfection.kino.feature.media.internal.data.local.MediaEntity
import com.letsgotoperfection.kino.feature.media.internal.data.repository.MediaRepositoryImpl
import com.letsgotoperfection.kino.feature.media.internal.data.storage.MediaStoreManager
import com.letsgotoperfection.kino.feature.media.internal.data.storage.PhotoPickerManager
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.AttachMediaUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.DeleteMediaUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.GetAllMediaUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.GetMediaByIdUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.NavigateToSourceUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.letsgotoperfection.kino.core.common.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Hilt module for Media feature dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MediaModule {
    
    /**
     * Bind MediaRepository interface to implementation
     */
    @Binds
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaRepository
    
    /**
     * Bind MediaApi interface to implementation
     */
    @Binds
    abstract fun bindMediaApi(
        mediaApiImpl: com.letsgotoperfection.kino.feature.media.internal.api.MediaApiImpl
    ): MediaApi
    
    companion object {
        
        /**
         * Provide MediaDao from the main database
         */
        @Provides
        @Singleton
        fun provideMediaDao(
            database: com.letsgotoperfection.kino.core.database.KinoDatabase
        ): MediaDao = database.mediaDao()
        
        /**
         * Provide MediaStoreManager
         */
        @Provides
        @Singleton
        fun provideMediaStoreManager(
            @ApplicationContext context: Context
        ): MediaStoreManager = MediaStoreManager(context)
        
        /**
         * Provide PhotoPickerManager
         */
        @Provides
        @Singleton
        fun providePhotoPickerManager(
            @ApplicationContext context: Context
        ): PhotoPickerManager = PhotoPickerManager(context)
        
        /**
         * Provide I/O dispatcher
         */
        @Provides
        @IoDispatcher
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
        
        /**
         * Provide Use Cases
         */
        @Provides
        @Singleton
        fun provideGetAllMediaUseCase(
            mediaRepository: MediaRepository
        ): GetAllMediaUseCase = GetAllMediaUseCase(mediaRepository)
        
        @Provides
        @Singleton
        fun provideAttachMediaUseCase(
            mediaRepository: MediaRepository
        ): AttachMediaUseCase = AttachMediaUseCase(mediaRepository)
        
        @Provides
        @Singleton
        fun provideDeleteMediaUseCase(
            mediaRepository: MediaRepository
        ): DeleteMediaUseCase = DeleteMediaUseCase(mediaRepository)

        @Provides
        @Singleton
        fun provideNavigateToSourceUseCase(): NavigateToSourceUseCase = NavigateToSourceUseCase()

        @Provides
        @Singleton
        fun provideGetMediaByIdUseCase(
            mediaRepository: MediaRepository
        ): GetMediaByIdUseCase = GetMediaByIdUseCase(mediaRepository)
    }
}
