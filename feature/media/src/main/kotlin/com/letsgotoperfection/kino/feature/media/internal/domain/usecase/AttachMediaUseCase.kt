package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import android.net.Uri
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import javax.inject.Inject

/**
 * Attaches media files to a source (task or note) by copying them
 * into app storage and persisting their metadata.
 */
internal class AttachMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {

    suspend operator fun invoke(
        uri: Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media> = mediaRepository.attachMedia(uri, sourceType, sourceId)

    suspend operator fun invoke(
        uris: List<Uri>,
        sourceType: MediaSourceType,
        sourceId: String
    ): List<Result<Media>> = uris.map { uri ->
        mediaRepository.attachMedia(uri, sourceType, sourceId)
    }
}
