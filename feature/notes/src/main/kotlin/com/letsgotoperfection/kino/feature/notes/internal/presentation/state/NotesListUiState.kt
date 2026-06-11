package com.letsgotoperfection.kino.feature.notes.internal.presentation.state

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteSort

/**
 * UI state for the notes list screen.
 */
@Immutable
internal data class NotesListUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    @StringRes val errorRes: Int? = null,
    val selectedFilter: NoteFilter = NoteFilter.ALL,
    val selectedSort: NoteSort = NoteSort.UPDATED_DESC,
    val searchQuery: String = ""
) {
    val isEmpty: Boolean get() = notes.isEmpty() && !isLoading
    val isSearchActive: Boolean get() = searchQuery.isNotBlank()
}

/**
 * Actions the notes list screen can dispatch to its ViewModel.
 */
internal sealed interface NotesListAction {
    data object LoadNotes : NotesListAction
    data class FilterNotes(val filter: NoteFilter) : NotesListAction
    data class SortNotes(val sort: NoteSort) : NotesListAction
    data class SearchNotes(val query: String) : NotesListAction
    data class DeleteNote(val noteId: String) : NotesListAction
    data class TogglePin(val noteId: String) : NotesListAction
}

/**
 * One-time events for the notes list screen.
 */
internal sealed interface NotesListEvent {
    data class ShowSnackbar(@StringRes val messageRes: Int) : NotesListEvent
}
