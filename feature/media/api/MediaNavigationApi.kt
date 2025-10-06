package com.letsgotoperfection.kino.feature.media.api

import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.Serializable

/**
 * Public API for Media Navigation
 * Exposes navigation routes and graph for external modules
 */

@Serializable
object MediaManagerRoute

@Serializable
data class MediaViewerRoute(
    val mediaId: String
)

/**
 * Public navigation graph for media feature
 */
fun NavGraphBuilder.mediaGraph(
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    onNavigateToViewer: (String) -> Unit,
    onBackClick: () -> Unit
) {
    // Delegate to internal navigation implementation
    com.letsgotoperfection.kino.feature.media.internal.navigation.mediaGraph(
        onNavigateToTask = onNavigateToTask,
        onNavigateToNote = onNavigateToNote,
        onNavigateToViewer = onNavigateToViewer,
        onBackClick = onBackClick
    )
}
