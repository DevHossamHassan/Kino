package com.letsgotoperfection.kino.feature.media.internal.api

import android.content.Context
import android.net.Uri
import com.letsgotoperfection.kino.feature.media.api.MediaApi
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import com.letsgotoperfection.kino.feature.media.internal.presentation.permission.MediaPermissionHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MediaApi
 * 
 * Provides public API for other feature modules to interact with media
 */
@Singleton
internal class MediaApiImpl @Inject constructor(
    private val mediaRepository: MediaRepository,
    @ApplicationContext private val context: Context
) : MediaApi {
    
    override suspend fun getMedia(mediaId: String): Result<Media> {
        return mediaRepository.getMediaById(mediaId)
    }
    
    override suspend fun attachMedia(
        uri: Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media> {
        return mediaRepository.attachMedia(uri, sourceType, sourceId)
    }
    
    override suspend fun attachMedia(
        uris: List<Uri>,
        sourceType: MediaSourceType,
        sourceId: String
    ): List<Result<Media>> {
        return uris.map { uri ->
            mediaRepository.attachMedia(uri, sourceType, sourceId)
        }
    }
    
    override suspend fun deleteMedia(mediaId: String): Result<Unit> {
        return mediaRepository.deleteMedia(mediaId)
    }
    
    override suspend fun getMediaCount(
        sourceType: MediaSourceType,
        sourceId: String
    ): Int {
        return mediaRepository.getMediaBySource(sourceType, sourceId)
            .first()
            .size
    }
    
    override fun hasMediaPermissions(): Boolean {
        return MediaPermissionHandler.hasPermissions(context)
    }
}
