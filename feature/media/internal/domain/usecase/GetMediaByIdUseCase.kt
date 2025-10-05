package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for retrieving a media item by its identifier.
 */
@Singleton
internal class GetMediaByIdUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(mediaId: String): Result<Media> = repository.getMediaById(mediaId)
}
