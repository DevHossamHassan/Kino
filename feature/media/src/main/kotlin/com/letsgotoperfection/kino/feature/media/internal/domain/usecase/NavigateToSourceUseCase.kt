package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import javax.inject.Inject

/**
 * Resolves which navigation target owns a media item.
 */
internal class NavigateToSourceUseCase @Inject constructor() {

    operator fun invoke(
        media: Media,
        onNavigateToTask: (String) -> Unit,
        onNavigateToNote: (String) -> Unit
    ) {
        when (media.sourceType) {
            MediaSourceType.TASK -> onNavigateToTask(media.sourceId)
            MediaSourceType.NOTE -> onNavigateToNote(media.sourceId)
        }
    }
}
