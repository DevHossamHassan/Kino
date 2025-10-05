package com.letsgotoperfection.kino.feature.media.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.letsgotoperfection.kino.feature.media.internal.presentation.ui.MediaManagerScreen
import com.letsgotoperfection.kino.feature.media.internal.presentation.ui.MediaViewerScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType

/**
 * Navigation destinations for Media feature
 */
object MediaDestinations {
    const val MEDIA_MANAGER = "media_manager"
    const val MEDIA_VIEWER = "media_viewer/{mediaId}"
    
    fun mediaViewerRoute(mediaId: String) = "media_viewer/$mediaId"
}

/**
 * Navigation graph for Media feature
 */
fun NavGraphBuilder.mediaNavGraph(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit
) {
    androidx.navigation.compose.composable(
        route = MediaDestinations.MEDIA_MANAGER
    ) {
        MediaManagerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToTask = onNavigateToTask,
            onNavigateToNote = onNavigateToNote,
            onNavigateToViewer = { mediaId ->
                navController.navigate(MediaDestinations.mediaViewerRoute(mediaId))
            }
        )
    }
    
    androidx.navigation.compose.composable(
        route = MediaDestinations.MEDIA_VIEWER,
        arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
    ) {
        MediaViewerScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToTask = onNavigateToTask,
            onNavigateToNote = onNavigateToNote
        )
    }
}
