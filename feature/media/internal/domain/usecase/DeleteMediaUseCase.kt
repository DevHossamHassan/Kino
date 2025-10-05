package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import javax.inject.Inject

/**
 * Use case for deleting media
 * 
 * This use case handles the business logic for deleting media files
 * including cleanup of associated files and database records
 */
internal class DeleteMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    
    /**
     * Delete a single media item
     * 
     * @param mediaId ID of the media to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(mediaId: String): Result<Unit> {
        return mediaRepository.deleteMedia(mediaId)
    }
    
    /**
     * Delete multiple media items
     * 
     * @param mediaIds List of media IDs to delete
     * @return List of Results for each deletion
     */
    suspend operator fun invoke(mediaIds: List<String>): List<Result<Unit>> {
        return mediaIds.map { mediaId ->
            mediaRepository.deleteMedia(mediaId)
        }
    }
}
