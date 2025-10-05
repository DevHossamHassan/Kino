package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaFilter
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all media items
 * 
 * This use case encapsulates the business logic for retrieving media
 * and can be extended with additional filtering or sorting logic
 */
internal class GetAllMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    
    /**
     * Get all media items
     * 
     * @return Flow of all media items
     */
    operator fun invoke(): Flow<List<Media>> {
        return mediaRepository.getAllMedia()
    }
    
    /**
     * Get media items with filter
     * 
     * @param filter Filter criteria
     * @return Flow of filtered media items
     */
    operator fun invoke(filter: MediaFilter): Flow<List<Media>> {
        return mediaRepository.getMediaByFilter(filter)
    }
}
