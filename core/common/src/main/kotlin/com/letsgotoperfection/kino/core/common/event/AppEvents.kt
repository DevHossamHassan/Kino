package com.letsgotoperfection.kino.core.common.event

/**
 * Application-wide events for cross-feature communication
 */
sealed interface AppEvent {
    data class TaskCreated(val taskId: String) : AppEvent
    data class TaskUpdated(val taskId: String) : AppEvent
    data class TaskDeleted(val taskId: String) : AppEvent
    data class MediaAttached(val mediaId: String, val targetId: String) : AppEvent
    data class NoteLinked(val noteId: String, val taskId: String) : AppEvent
    data class NotificationTriggered(val type: String, val message: String) : AppEvent
}

