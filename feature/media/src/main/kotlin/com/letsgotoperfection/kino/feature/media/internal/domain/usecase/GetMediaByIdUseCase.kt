package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import javax.inject.Inject

/**
 * Retrieves a media item by its identifier.
 */
internal class GetMediaByIdUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {

    suspend operator fun invoke(mediaId: String): Result<Media> =
        mediaRepository.getMediaById(mediaId)
}
