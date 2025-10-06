package com.letsgotoperfection.kino.feature.media.internal.api

import android.content.Context
import android.net.Uri
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.feature.media.api.MediaApi
import com.letsgotoperfection.kino.feature.media.api.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType as DomainMediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MediaApi
 * 
 * Provides public API for other feature modules to interact with media
 * without depending on the internal implementation
 */
@Singleton
class MediaApiImpl @Inject constructor(
    private val mediaRepository: MediaRepository,
    @ApplicationContext private val context: Context
) : MediaApi {
    
    override suspend fun getMedia(mediaId: String): Result<Media> {
        return try {
            val media = mediaRepository.getMediaById(mediaId).getOrThrow()
            Result.Success(media)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun attachMedia(
        uri: Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media> {
        return try {
            val media = mediaRepository.attachMedia(uri, sourceType.toDomain(), sourceId).getOrThrow()
            Result.Success(media)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun attachMedia(
        uris: List<Uri>,
        sourceType: MediaSourceType,
        sourceId: String
    ): List<Result<Media>> {
        return uris.map { uri ->
            try {
                val media = mediaRepository.attachMedia(uri, sourceType.toDomain(), sourceId).getOrThrow()
                Result.Success(media)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
    
    override suspend fun deleteMedia(mediaId: String): Result<Unit> {
        return try {
            mediaRepository.deleteMedia(mediaId).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getMediaCount(
        sourceType: MediaSourceType,
        sourceId: String
    ): Int {
        return try {
            mediaRepository.getMediaCount(sourceType.toDomain(), sourceId)
        } catch (e: Exception) {
            0
        }
    }
    
    override fun hasMediaPermissions(): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == 
               android.content.pm.PackageManager.PERMISSION_GRANTED &&
               context.checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == 
               android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}

private fun MediaSourceType.toDomain(): DomainMediaSourceType = when (this) {
    MediaSourceType.TASK -> DomainMediaSourceType.TASK
    MediaSourceType.NOTE -> DomainMediaSourceType.NOTE
}
