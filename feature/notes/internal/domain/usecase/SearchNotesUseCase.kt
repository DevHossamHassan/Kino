package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for searching notes by free text queries.
 */
@Singleton
internal class SearchNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    operator fun invoke(query: String): Flow<List<Note>> {
        return repository.searchNotes(query)
    }
}
