package com.letsgotoperfection.kino.feature.media.internal.presentation.state

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
    val error: String? = null,
    val mediaType: MediaType? = null
)
