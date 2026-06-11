package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Toggles the pinned status of a note and returns the new value.
 */
internal class ToggleNotePinUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(noteId: String): Result<Boolean> = repository.togglePin(noteId)
}
