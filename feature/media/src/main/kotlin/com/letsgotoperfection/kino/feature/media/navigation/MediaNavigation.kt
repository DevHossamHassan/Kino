package com.letsgotoperfection.kino.feature.media.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.letsgotoperfection.kino.feature.media.internal.presentation.ui.MediaManagerScreen
import com.letsgotoperfection.kino.feature.media.internal.presentation.ui.MediaViewerScreen
import kotlinx.serialization.Serializable

/**
 * Media feature routes - Type-safe navigation
 */
@Serializable
object MediaManagerRoute

@Serializable
data class MediaViewerRoute(
    val mediaId: String
)

/**
 * Media navigation graph
 * Exposes composable screens for app module to wire
 */
fun NavGraphBuilder.mediaGraph(
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    onNavigateToViewer: (String) -> Unit,
    onBackClick: () -> Unit
) {
    composable<MediaManagerRoute> {
        MediaManagerScreen(
            onNavigateBack = onBackClick,
            onNavigateToTask = onNavigateToTask,
            onNavigateToNote = onNavigateToNote,
            onNavigateToViewer = onNavigateToViewer
        )
    }
    composable<MediaViewerRoute> {
        MediaViewerScreen(
            onNavigateBack = onBackClick,
            onNavigateToTask = onNavigateToTask,
            onNavigateToNote = onNavigateToNote
        )
    }
}

/**
 * Deep link patterns for media feature
 */
object MediaDeepLinks {
    const val MEDIA_MANAGER = "kino://app/media"
    const val MEDIA_VIEWER = "kino://app/media/viewer/{mediaId}"
    
    fun createMediaViewerDeepLink(mediaId: String) = "kino://app/media/viewer/$mediaId"
}
