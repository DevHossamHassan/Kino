package com.letsgotoperfection.kino.navigation

import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.letsgotoperfection.kino.feature.kanban.navigation.KanbanBoardRoute
import com.letsgotoperfection.kino.feature.media.navigation.MediaManagerRoute
import com.letsgotoperfection.kino.feature.media.navigation.MediaViewerRoute
import com.letsgotoperfection.kino.feature.notes.navigation.NoteDetailRoute
import com.letsgotoperfection.kino.feature.notes.navigation.NotesListRoute
import com.letsgotoperfection.kino.feature.recurringtasks.navigation.RecurringTasksListRoute
import com.letsgotoperfection.kino.feature.settings.navigation.SettingsRoute
import com.letsgotoperfection.kino.feature.taskdetail.navigation.TaskDetailRoute

/**
 * Single source of truth for app deep links (launcher intents and notifications).
 *
 * Format: kino://app/<feature>/...
 *
 * Supported links:
 * - kino://app/kanban
 * - kino://app/task/{taskId}
 * - kino://app/notes
 * - kino://app/note/{noteId}
 * - kino://app/media
 * - kino://app/media/viewer/{mediaId}
 * - kino://app/settings
 * - kino://app/recurring_tasks
 */
object DeepLinkHandler {

    /**
     * Navigate to the destination encoded in [intent], if any.
     *
     * @return true when the deep link was recognized and handled.
     */
    fun handleDeepLink(intent: Intent, navController: NavController): Boolean {
        val uri = intent.data ?: return false
        val route = parseDeepLink(uri) ?: return false
        navController.navigate(route)
        return true
    }

    /**
     * Parse a deep link URI into its type-safe navigation route.
     *
     * @return the route object, or null when the URI is not a recognized deep link.
     */
    fun parseDeepLink(uri: Uri): Any? {
        if (uri.scheme != "kino" || uri.host != "app") return null

        val segments = uri.pathSegments
        return when (segments.firstOrNull()) {
            "kanban" -> KanbanBoardRoute
            "task" -> segments.getOrNull(1)?.takeIf { it.isNotEmpty() }?.let { TaskDetailRoute(it) }
            "notes" -> NotesListRoute
            "note" -> segments.getOrNull(1)?.takeIf { it.isNotEmpty() }?.let { NoteDetailRoute(it) }
            "media" -> when {
                segments.getOrNull(1) == "viewer" ->
                    segments.getOrNull(2)?.takeIf { it.isNotEmpty() }?.let { MediaViewerRoute(it) }
                segments.size == 1 -> MediaManagerRoute
                else -> null
            }
            "settings" -> SettingsRoute
            "recurring_tasks" -> RecurringTasksListRoute
            else -> null
        }
    }
}
