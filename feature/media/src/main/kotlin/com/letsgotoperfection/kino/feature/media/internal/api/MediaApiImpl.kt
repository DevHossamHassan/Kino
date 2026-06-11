package com.letsgotoperfection.kino.feature.media.internal.api

import android.content.Context
import android.net.Uri
import com.letsgotoperfection.kino.feature.media.api.Media
import com.letsgotoperfection.kino.feature.media.api.MediaApi
import com.letsgotoperfection.kino.feature.media.api.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media as DomainMedia
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType as DomainMediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import com.letsgotoperfection.kino.feature.media.internal.presentation.permission.MediaPermissionHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the public [MediaApi], adapting calls to the internal
 * [MediaRepository] and mapping between public and internal domain models.
 */
@Singleton
internal class MediaApiImpl @Inject constructor(
    private val mediaRepository: MediaRepository,
    @ApplicationContext private val context: Context
) : MediaApi {

    override suspend fun getMedia(mediaId: String): Result<Media> {
        return mediaRepository.getMediaById(mediaId).map { it.toPublic() }
    }

    override suspend fun attachMedia(
        uri: Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media> {
        return mediaRepository.attachMedia(uri, sourceType.toDomain(), sourceId)
            .map { it.toPublic() }
    }

    override suspend fun attachMedia(
        uris: List<Uri>,
        sourceType: MediaSourceType,
        sourceId: String
    ): List<Result<Media>> {
        return uris.map { uri ->
            mediaRepository.attachMedia(uri, sourceType.toDomain(), sourceId)
                .map { it.toPublic() }
        }
    }

    override suspend fun deleteMedia(mediaId: String): Result<Unit> {
        return mediaRepository.deleteMedia(mediaId)
    }

    override suspend fun getMediaCount(
        sourceType: MediaSourceType,
        sourceId: String
    ): Int {
        return mediaRepository.getMediaCount(sourceType.toDomain(), sourceId)
    }

    override fun hasMediaPermissions(): Boolean {
        return MediaPermissionHandler.hasPermissions(context)
    }
}

private fun MediaSourceType.toDomain(): DomainMediaSourceType = when (this) {
    MediaSourceType.TASK -> DomainMediaSourceType.TASK
    MediaSourceType.NOTE -> DomainMediaSourceType.NOTE
}

private fun DomainMediaSourceType.toPublic(): MediaSourceType = when (this) {
    DomainMediaSourceType.TASK -> MediaSourceType.TASK
    DomainMediaSourceType.NOTE -> MediaSourceType.NOTE
}

private fun DomainMedia.toPublic(): Media = Media(
    id = id,
    uri = uri,
    filename = filename,
    mimeType = mimeType,
    size = size,
    dateAdded = dateAdded,
    dateModified = dateModified,
    width = width,
    height = height,
    duration = duration,
    thumbnailUri = thumbnailUri,
    sourceType = sourceType.toPublic(),
    sourceId = sourceId
)
