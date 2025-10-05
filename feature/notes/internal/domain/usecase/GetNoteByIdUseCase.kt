package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting a specific note by ID.
 * This encapsulates the business logic for retrieving a single note.
 */
@Singleton
internal class GetNoteByIdUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Get a specific note by its unique identifier.
     * 
     * @param noteId The unique identifier of the note
     * @return Flow of the note or null if not found
     */
    operator fun invoke(noteId: String): Flow<Note?> {
        return repository.getNoteById(noteId)
    }
}
