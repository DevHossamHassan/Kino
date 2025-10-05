package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.common.event.AppEvent
import com.letsgotoperfection.kino.core.common.event.EventBus
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for updating existing notes.
 * This encapsulates the business logic for note updates.
 */
@Singleton
internal class UpdateNoteUseCase @Inject constructor(
    private val repository: NotesRepository,
    private val eventBus: EventBus
) {
    /**
     * Update an existing note with new content.
     * 
     * @param noteId The unique identifier of the note to update
     * @param title New title for the note (optional)
     * @param content New content for the note (optional)
     * @param labels New labels for the note (optional)
     * @return Result containing the updated Note or error
     */
    suspend operator fun invoke(
        noteId: String,
        title: String? = null,
        content: AnnotatedString? = null,
        labels: List<Label>? = null
    ): Result<com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note> = runCatching {
        repository.updateNote(
            noteId = noteId,
            title = title,
            content = content,
            labels = labels
        ).onSuccess { updatedNote ->
            eventBus.emit(AppEvent.NoteLinked(updatedNote.id, ""))
        }.getOrThrow()
    }
}
