package com.letsgotoperfection.kino.notification

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.letsgotoperfection.kino.navigation.AppDestinations

/**
 * Handles deep links from notifications to navigate to specific screens
 * 
 * This class processes notification intents and navigates to the appropriate
 * screen based on the deep link URI.
 */
object NotificationDeepLinkHandler {
    
    /**
     * Process a deep link intent and navigate to the appropriate screen
     * 
     * @param intent The intent containing the deep link URI
     * @param navController Navigation controller to handle navigation
     * @return true if the deep link was handled, false otherwise
     */
    fun handleDeepLink(intent: Intent, navController: NavController): Boolean {
        val data: Uri? = intent.data
        if (data == null) return false
        
        return when {
            data.scheme == "kino" && data.host == "app" -> {
                handleKinoDeepLink(data, navController)
            }
            else -> false
        }
    }
    
    /**
     * Handle Kino app deep links
     */
    private fun handleKinoDeepLink(uri: Uri, navController: NavController): Boolean {
        val path = uri.path ?: return false
        
        return when {
            path.startsWith("/task/") -> {
                val taskId = path.removePrefix("/task/")
                if (taskId.isNotEmpty()) {
                    navController.navigate(AppDestinations.taskDetailRoute(taskId))
                    true
                } else {
                    false
                }
            }
            path.startsWith("/note/") -> {
                val noteId = path.removePrefix("/note/")
                if (noteId.isNotEmpty()) {
                    navController.navigate(AppDestinations.noteDetailRoute(noteId))
                    true
                } else {
                    false
                }
            }
            path.startsWith("/media/viewer/") -> {
                val mediaId = path.removePrefix("/media/viewer/")
                if (mediaId.isNotEmpty()) {
                    navController.navigate(AppDestinations.mediaViewerRoute(mediaId))
                    true
                } else {
                    false
                }
            }
            path == "/kanban" -> {
                navController.navigate(AppDestinations.KANBAN)
                true
            }
            path == "/notes" -> {
                navController.navigate(AppDestinations.NOTES)
                true
            }
            path == "/media" -> {
                navController.navigate(AppDestinations.MEDIA)
                true
            }
            path == "/settings" -> {
                navController.navigate(AppDestinations.SETTINGS)
                true
            }
            path == "/recurring_tasks" -> {
                navController.navigate(AppDestinations.RECURRING_TASKS)
                true
            }
            else -> false
        }
    }
    
    /**
     * Create a deep link URI for a specific screen
     */
    fun createDeepLinkUri(screen: DeepLinkScreen, id: String? = null): Uri {
        val baseUri = Uri.parse("kino://app")
        
        return when (screen) {
            DeepLinkScreen.KANBAN -> baseUri.buildUpon().appendPath("kanban").build()
            DeepLinkScreen.NOTES -> baseUri.buildUpon().appendPath("notes").build()
            DeepLinkScreen.MEDIA -> baseUri.buildUpon().appendPath("media").build()
            DeepLinkScreen.SETTINGS -> baseUri.buildUpon().appendPath("settings").build()
            DeepLinkScreen.RECURRING_TASKS -> baseUri.buildUpon().appendPath("recurring_tasks").build()
            DeepLinkScreen.TASK_DETAIL -> {
                requireNotNull(id) { "Task ID is required for task detail deep link" }
                baseUri.buildUpon().appendPath("task").appendPath(id).build()
            }
            DeepLinkScreen.NOTE_DETAIL -> {
                requireNotNull(id) { "Note ID is required for note detail deep link" }
                baseUri.buildUpon().appendPath("note").appendPath(id).build()
            }
            DeepLinkScreen.MEDIA_VIEWER -> {
                requireNotNull(id) { "Media ID is required for media viewer deep link" }
                baseUri.buildUpon().appendPath("media").appendPath("viewer").appendPath(id).build()
            }
        }
    }
}

/**
 * Supported deep link screens
 */
enum class DeepLinkScreen {
    KANBAN,
    NOTES,
    MEDIA,
    SETTINGS,
    RECURRING_TASKS,
    TASK_DETAIL,
    NOTE_DETAIL,
    MEDIA_VIEWER
}


