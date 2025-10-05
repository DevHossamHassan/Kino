package com.letsgotoperfection.kino.feature.media.internal.domain.usecase

import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import javax.inject.Inject

/**
 * Use case for navigating to the source of media
 * 
 * This use case handles navigation logic when user wants to go
 * to the task or note that owns a specific media item
 */
internal class NavigateToSourceUseCase @Inject constructor() {
    
    /**
     * Navigate to the source of a media item
     * 
     * @param media The media item to navigate from
     * @param onNavigateToTask Callback for task navigation
     * @param onNavigateToNote Callback for note navigation
     */
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
    
    /**
     * Navigate to source by source type and ID
     * 
     * @param sourceType Type of source
     * @param sourceId ID of the source
     * @param onNavigateToTask Callback for task navigation
     * @param onNavigateToNote Callback for note navigation
     */
    operator fun invoke(
        sourceType: MediaSourceType,
        sourceId: String,
        onNavigateToTask: (String) -> Unit,
        onNavigateToNote: (String) -> Unit
    ) {
        when (sourceType) {
            MediaSourceType.TASK -> onNavigateToTask(sourceId)
            MediaSourceType.NOTE -> onNavigateToNote(sourceId)
        }
    }
}
