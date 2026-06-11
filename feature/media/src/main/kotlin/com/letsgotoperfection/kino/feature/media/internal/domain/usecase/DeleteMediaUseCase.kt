package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import javax.inject.Inject

/**
 * Deletes media items including their backing files in app storage.
 */
internal class DeleteMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {

    suspend operator fun invoke(mediaId: String): Result<Unit> =
        mediaRepository.deleteMedia(mediaId)

    suspend operator fun invoke(mediaIds: List<String>): List<Result<Unit>> =
        mediaIds.map { mediaRepository.deleteMedia(it) }
}
