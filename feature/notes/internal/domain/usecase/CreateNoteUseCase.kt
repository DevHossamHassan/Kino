package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.common.event.AppEvent
import com.letsgotoperfection.kino.core.common.event.EventBus
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for creating new notes.
 * This encapsulates the business logic for note creation.
 */
@Singleton
internal class CreateNoteUseCase @Inject constructor(
    private val repository: NotesRepository,
    private val eventBus: EventBus
) {
    /**
     * Create a new note with the provided content.
     * 
     * @param title The title of the note
     * @param content The rich text content of the note
     * @param labels Optional labels to attach to the note
     * @return Result containing the created Note or error
     */
    suspend operator fun invoke(
        title: String,
        content: AnnotatedString,
        labels: List<Label> = emptyList()
    ): Result<Note> = runCatching {
        require(title.isNotBlank()) { "Note title cannot be blank" }
        
        val note = Note(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            content = content,
            isPinned = false,
            labels = labels,
            attachmentCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        repository.createNote(note.title, note.content, note.labels)
            .onSuccess { createdNote ->
                eventBus.emit(AppEvent.NoteLinked(createdNote.id, ""))
            }
            .getOrThrow()
    }
}
