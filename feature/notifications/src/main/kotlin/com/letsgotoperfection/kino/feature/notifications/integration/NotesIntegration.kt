package com.letsgotoperfection.kino.feature.notifications.integration

import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Integration example for Notes feature
 */
class NotesIntegration @Inject constructor(
    private val notificationApi: NotificationApi
) {
    
    /**
     * Send note reminder notification
     */
    suspend fun sendNoteReminder(
        noteId: String,
        noteTitle: String,
        reminderTime: LocalDateTime? = null
    ) {
        notificationApi.sendNoteReminder(
            noteId = noteId,
            noteTitle = noteTitle,
            reminderText = "Reminder to review: $noteTitle"
        )
    }
    
    /**
     * Send note sharing notification
     */
    suspend fun sendNoteSharedNotification(
        noteId: String,
        noteTitle: String,
        sharedWith: String
    ) {
        notificationApi.sendNotification(
            title = "Note Shared",
            message = "\"$noteTitle\" has been shared with $sharedWith",
            category = com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory.NOTE_REMINDER,
            deepLink = "kino://note/$noteId"
        )
    }
}
