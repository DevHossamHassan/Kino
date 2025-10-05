package com.letsgotoperfection.kino.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

/**
 * Convenience accessors for common navigation targets.
 */
fun NavController.navigateToKanbanBoard(
    navOptions: NavOptions? = null
) {
    navigate(AppDestinations.KANBAN, navOptions)
}

fun NavController.navigateToTaskDetail(
    taskId: String,
    navOptions: NavOptions? = null
) {
    navigate(AppDestinations.taskDetailRoute(taskId), navOptions)
}

fun NavController.navigateToNotesList(
    navOptions: NavOptions? = null
) {
    navigate(AppDestinations.NOTES, navOptions)
}

fun NavController.navigateToNoteDetail(
    noteId: String,
    navOptions: NavOptions? = null
) {
    navigate(AppDestinations.noteDetailRoute(noteId), navOptions)
}

fun NavController.navigateToMediaManager(
    navOptions: NavOptions? = null
) {
    navigate(AppDestinations.MEDIA, navOptions)
}

fun NavController.navigateToMediaViewer(
    mediaId: String,
    navOptions: NavOptions? = null
) {
    navigate(AppDestinations.mediaViewerRoute(mediaId), navOptions)
}

fun NavController.navigateBack() {
    popBackStack()
}
