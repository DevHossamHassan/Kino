package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaFilter
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Observes media items, optionally narrowed by a [MediaFilter].
 */
internal class GetAllMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {

    operator fun invoke(): Flow<List<Media>> = mediaRepository.getAllMedia()

    operator fun invoke(filter: MediaFilter): Flow<List<Media>> =
        mediaRepository.getMediaByFilter(filter)
}
