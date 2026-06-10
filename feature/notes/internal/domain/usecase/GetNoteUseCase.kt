package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Use case for getting a single note by ID
 */
class GetNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    
    suspend operator fun invoke(noteId: String): Result<com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note> {
        return repository.getNoteById(noteId)
    }
}




