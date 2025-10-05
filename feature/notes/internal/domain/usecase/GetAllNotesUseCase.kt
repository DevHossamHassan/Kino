package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteSort
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting all notes with filtering and sorting.
 * This encapsulates the business logic for retrieving notes.
 */
@Singleton
internal class GetAllNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    /**
     * Get all notes with optional filtering and sorting.
     * 
     * @param filter Filter to apply to notes (default: ALL)
     * @param sort Sort order for notes (default: UPDATED_DESC)
     * @return Flow of filtered and sorted notes
     */
    operator fun invoke(
        filter: NoteFilter = NoteFilter.ALL,
        sort: NoteSort = NoteSort.UPDATED_DESC
    ): Flow<List<Note>> {
        return repository.getAllNotes(filter, sort)
    }
}
