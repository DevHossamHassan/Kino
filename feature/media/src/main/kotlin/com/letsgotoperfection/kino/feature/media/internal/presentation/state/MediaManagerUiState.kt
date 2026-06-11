package com.letsgotoperfection.kino.feature.media.internal.presentation.state

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType

/**
 * UI state for the media manager screen.
 */
@Immutable
internal data class MediaManagerUiState(
    val isLoading: Boolean = true,
    val media: List<Media> = emptyList(),
    val totalSize: Long = 0L,
    val viewMode: ViewMode = ViewMode.GRID,
    val typeFilter: MediaType? = null,
    @StringRes val errorRes: Int? = null
)

/**
 * View mode for media display.
 */
internal enum class ViewMode {
    GRID,
    LIST
}

/**
 * One-time UI events for the media manager screen.
 */
internal sealed interface MediaManagerEvent {
    data class ShowMessage(@StringRes val messageRes: Int) : MediaManagerEvent
}
