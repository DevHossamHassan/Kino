package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for toggling the pinned status of a note.
 */
@Singleton
internal class ToggleNotePinUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(noteId: String) = repository.togglePin(noteId)
}
