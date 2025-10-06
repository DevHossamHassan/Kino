package com.letsgotoperfection.kino.navigation

import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.letsgotoperfection.kino.feature.kanban.navigation.KanbanBoardRoute
import com.letsgotoperfection.kino.feature.media.navigation.MediaViewerRoute
import com.letsgotoperfection.kino.feature.notes.navigation.NoteDetailRoute
import com.letsgotoperfection.kino.feature.settings.navigation.SettingsRoute
import com.letsgotoperfection.kino.feature.taskdetail.navigation.TaskDetailRoute

/**
 * Handles deep links for the Kino app
 * Format: kino://app/feature/...
 */
object DeepLinkHandler {
    
    fun handleDeepLink(intent: Intent, navController: NavController): Boolean {
        val uri = intent.data ?: return false
        
        return when {
            uri.scheme != "kino" || uri.host != "app" -> false
            
            // kino://app/kanban
            uri.pathSegments.firstOrNull() == "kanban" -> {
                navController.navigate(KanbanBoardRoute)
                true
            }
            
            // kino://app/task/{taskId}
            uri.pathSegments.firstOrNull() == "task" -> {
                val taskId = uri.pathSegments.getOrNull(1)
                taskId?.let {
                    navController.navigate(TaskDetailRoute(it))
                    true
                } ?: false
            }
            
            // kino://app/note/{noteId}
            uri.pathSegments.firstOrNull() == "note" -> {
                val noteId = uri.pathSegments.getOrNull(1)
                noteId?.let {
                    navController.navigate(NoteDetailRoute(it))
                    true
                } ?: false
            }
            
            // kino://app/media/viewer/{mediaId}
            uri.pathSegments.firstOrNull() == "media" &&
            uri.pathSegments.getOrNull(1) == "viewer" -> {
                val mediaId = uri.pathSegments.getOrNull(2)
                mediaId?.let {
                    navController.navigate(MediaViewerRoute(it))
                    true
                } ?: false
            }
            
            // kino://app/settings
            uri.pathSegments.firstOrNull() == "settings" -> {
                navController.navigate(SettingsRoute)
                true
            }
            
            else -> false
        }
    }
    
    /**
     * Parse deep link URI to route object
     */
    fun parseDeepLink(uri: Uri): Any? {
        return when {
            uri.scheme != "kino" || uri.host != "app" -> null
            
            uri.pathSegments.firstOrNull() == "kanban" -> KanbanBoardRoute
            
            uri.pathSegments.firstOrNull() == "task" -> {
                val taskId = uri.pathSegments.getOrNull(1)
                taskId?.let { TaskDetailRoute(it) }
            }
            
            uri.pathSegments.firstOrNull() == "note" -> {
                val noteId = uri.pathSegments.getOrNull(1)
                noteId?.let { NoteDetailRoute(it) }
            }
            
            uri.pathSegments.firstOrNull() == "media" &&
            uri.pathSegments.getOrNull(1) == "viewer" -> {
                val mediaId = uri.pathSegments.getOrNull(2)
                mediaId?.let { MediaViewerRoute(it) }
            }
            
            uri.pathSegments.firstOrNull() == "settings" -> SettingsRoute
            
            else -> null
        }
    }
}
