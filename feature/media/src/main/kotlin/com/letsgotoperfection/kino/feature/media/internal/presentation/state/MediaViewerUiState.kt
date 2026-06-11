package com.letsgotoperfection.kino.feature.media.internal.presentation.state

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType

/**
 * UI state for the media viewer screen.
 */
@Immutable
internal data class MediaViewerUiState(
    val isLoading: Boolean = true,
    val media: Media? = null,
    val mediaType: MediaType? = null,
    @StringRes val errorRes: Int? = null
)

/**
 * One-time UI events for the media viewer screen.
 */
internal sealed interface MediaViewerEvent {
    data class ShowMessage(@StringRes val messageRes: Int) : MediaViewerEvent
    data class NavigateToTask(val taskId: String) : MediaViewerEvent
    data class NavigateToNote(val noteId: String) : MediaViewerEvent
    data object MediaDeleted : MediaViewerEvent
}
