package com.letsgotoperfection.kino.feature.notes.internal.presentation.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.common.UiState
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteSort

/**
 * UI state for the notes list screen.
 */
@Immutable
data class NotesListUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: NoteFilter = NoteFilter.ALL,
    val selectedSort: NoteSort = NoteSort.UPDATED_DESC,
    val searchQuery: String = "",
    val snackbarMessage: String? = null
)

/**
 * Actions that can be performed on the notes list screen.
 */
sealed interface NotesListAction {
    data object LoadNotes : NotesListAction
    data class FilterNotes(val filter: NoteFilter) : NotesListAction
    data class SortNotes(val sort: NoteSort) : NotesListAction
    data class SearchNotes(val query: String) : NotesListAction
    data class CreateNote(val title: String, val content: AnnotatedString) : NotesListAction
    data class DeleteNote(val noteId: String) : NotesListAction
    data class TogglePin(val noteId: String) : NotesListAction
    data class NavigateToNoteDetail(val noteId: String) : NotesListAction
    data class NavigateToNoteEditor(val noteId: String?) : NotesListAction
    data object DismissSnackbar : NotesListAction
}

/**
 * One-time events for the notes list screen.
 */
sealed interface NotesListEvent {
    data class ShowSnackbar(val message: String) : NotesListEvent
    data class NavigateToNoteDetail(val noteId: String) : NotesListEvent
    data class NavigateToNoteEditor(val noteId: String?) : NotesListEvent
}
