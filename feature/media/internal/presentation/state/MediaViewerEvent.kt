package com.letsgotoperfection.kino.feature.media.internal.presentation.state

/**
 * Events for Media Viewer
 */
sealed interface MediaViewerEvent {
    data class ShowError(val message: String) : MediaViewerEvent
    data class NavigateToTask(val taskId: String) : MediaViewerEvent
    data class NavigateToNote(val noteId: String) : MediaViewerEvent
    data object MediaDeleted : MediaViewerEvent
}
