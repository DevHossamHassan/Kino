package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Observes a single note by id; emits null when the note does not exist.
 */
internal class GetNoteByIdUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    operator fun invoke(noteId: String): Flow<Note?> = repository.getNoteById(noteId)
}
