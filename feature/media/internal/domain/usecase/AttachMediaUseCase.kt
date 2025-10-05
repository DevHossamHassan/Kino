package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import android.net.Uri
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import javax.inject.Inject

/**
 * Use case for attaching media to a source (task or note)
 * 
 * This use case handles the business logic for attaching media files
 * including validation and error handling
 */
internal class AttachMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    
    /**
     * Attach a single media file to a source
     * 
     * @param uri Content URI of the media file
     * @param sourceType Type of source (task, note)
     * @param sourceId ID of the source
     * @return Result containing the created Media or error
     */
    suspend operator fun invoke(
        uri: Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media> {
        return mediaRepository.attachMedia(uri, sourceType, sourceId)
    }
    
    /**
     * Attach multiple media files to a source
     * 
     * @param uris List of content URIs
     * @param sourceType Type of source (task, note)
     * @param sourceId ID of the source
     * @return List of Results for each media attachment
     */
    suspend operator fun invoke(
        uris: List<Uri>,
        sourceType: MediaSourceType,
        sourceId: String
    ): List<Result<Media>> {
        return uris.map { uri ->
            mediaRepository.attachMedia(uri, sourceType, sourceId)
        }
    }
}
