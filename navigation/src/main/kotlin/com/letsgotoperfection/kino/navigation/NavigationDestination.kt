package com.letsgotoperfection.kino.navigation

/**
 * Base interface for all navigation destinations
 */
sealed interface NavigationDestination {
    val route: String
}

/**
 * Kanban feature destinations
 */
sealed class KanbanDestination(override val route: String) : NavigationDestination {
    data object Board : KanbanDestination("kanban_board")
    data class TaskDetail(val taskId: String) : KanbanDestination("task/{taskId}") {
        companion object {
            const val ROUTE = "task/{taskId}"
            fun createRoute(taskId: String) = "task/$taskId"
        }
    }
}

/**
 * Notes feature destinations
 */
sealed class NotesDestination(override val route: String) : NavigationDestination {
    data object NotesList : NotesDestination("notes")
    data class NoteDetail(val noteId: String) : NotesDestination("note/{noteId}") {
        companion object {
            const val ROUTE = "note/{noteId}"
            fun createRoute(noteId: String) = "note/$noteId"
        }
    }
}

/**
 * Media feature destinations
 */
sealed class MediaDestination(override val route: String) : NavigationDestination {
    data object MediaManager : MediaDestination("media")
    data class MediaViewer(val mediaId: String) : MediaDestination("media/{mediaId}") {
        companion object {
            const val ROUTE = "media/{mediaId}"
            fun createRoute(mediaId: String) = "media/$mediaId"
        }
    }
}

/**
 * Task Detail feature destinations
 */
sealed class TaskDetailDestination(override val route: String) : NavigationDestination {
    data class TaskDetail(val taskId: String) : TaskDetailDestination("task_detail/{taskId}") {
        companion object {
            const val ROUTE = "task_detail/{taskId}"
            fun createRoute(taskId: String) = "task_detail/$taskId"
        }
    }
}

/**
 * Notifications feature destinations
 */
sealed class NotificationsDestination(override val route: String) : NavigationDestination {
    data object NotificationsList : NotificationsDestination("notifications")
    data class NotificationDetail(val notificationId: String) : NotificationsDestination("notification/{notificationId}") {
        companion object {
            const val ROUTE = "notification/{notificationId}"
            fun createRoute(notificationId: String) = "notification/$notificationId"
        }
    }
}





