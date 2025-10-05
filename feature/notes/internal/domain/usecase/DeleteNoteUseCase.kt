package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.common.event.AppEvent
import com.letsgotoperfection.kino.core.common.event.EventBus
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for deleting notes.
 * This encapsulates the business logic for note deletion.
 */
@Singleton
internal class DeleteNoteUseCase @Inject constructor(
    private val repository: NotesRepository,
    private val eventBus: EventBus
) {
    /**
     * Delete a note by its unique identifier.
     * 
     * @param noteId The unique identifier of the note to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(noteId: String): Result<Unit> = runCatching {
        repository.deleteNote(noteId)
            .onSuccess {
                eventBus.emit(AppEvent.TaskDeleted(noteId))
            }
            .getOrThrow()
    }
}
