package com.letsgotoperfection.kino.feature.notes.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteSort
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.DeleteNoteUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.GetAllNotesUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.SearchNotesUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.ToggleNotePinUseCase
import com.letsgotoperfection.kino.feature.notes.internal.presentation.state.NotesListAction
import com.letsgotoperfection.kino.feature.notes.internal.presentation.state.NotesListEvent
import com.letsgotoperfection.kino.feature.notes.internal.presentation.state.NotesListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the notes list screen.
 */
@HiltViewModel
internal class NotesListViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val toggleNotePinUseCase: ToggleNotePinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesListUiState(isLoading = true))
    val uiState: StateFlow<NotesListUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<NotesListEvent>(Channel.BUFFERED)
    val uiEvent: Flow<NotesListEvent> = _uiEvent.receiveAsFlow()

    private var notesJob: Job? = null

    init {
        loadNotes()
        setupSearchDebounce()
    }

    fun onAction(action: NotesListAction) {
        when (action) {
            is NotesListAction.LoadNotes -> loadNotes()
            is NotesListAction.FilterNotes -> {
                _uiState.update { it.copy(selectedFilter = action.filter) }
                loadNotes()
            }
            is NotesListAction.SortNotes -> {
                _uiState.update { it.copy(selectedSort = action.sort) }
                loadNotes()
            }
            is NotesListAction.SearchNotes ->
                _uiState.update { it.copy(searchQuery = action.query) }
            is NotesListAction.DeleteNote -> deleteNote(action.noteId)
            is NotesListAction.TogglePin -> togglePin(action.noteId)
        }
    }

    private fun loadNotes() {
        val state = _uiState.value
        observeNotes(getAllNotesUseCase(filter = state.selectedFilter, sort = state.selectedSort))
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _uiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(SEARCH_DEBOUNCE_MS)
                .collect { query ->
                    if (query.isBlank()) {
                        loadNotes()
                    } else {
                        _uiState.update { it.copy(isSearching = true) }
                        observeNotes(searchNotesUseCase(query))
                    }
                }
        }
    }

    private fun deleteNote(noteId: String) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
                .onSuccess {
                    _uiEvent.send(NotesListEvent.ShowSnackbar(R.string.notes_note_deleted))
                }
                .onFailure {
                    _uiEvent.send(NotesListEvent.ShowSnackbar(R.string.notes_note_delete_failed))
                }
        }
    }

    private fun togglePin(noteId: String) {
        viewModelScope.launch {
            toggleNotePinUseCase(noteId)
                .onSuccess { isPinned ->
                    val messageRes =
                        if (isPinned) R.string.notes_note_pinned_message
                        else R.string.notes_note_unpinned_message
                    _uiEvent.send(NotesListEvent.ShowSnackbar(messageRes))
                }
                .onFailure {
                    _uiEvent.send(NotesListEvent.ShowSnackbar(R.string.notes_note_update_failed))
                }
        }
    }

    private fun observeNotes(flow: Flow<List<Note>>) {
        notesJob?.cancel()
        notesJob = viewModelScope.launch {
            flow
                .onStart {
                    _uiState.update { it.copy(isLoading = true, errorRes = null) }
                }
                .catch {
                    _uiState.update {
                        it.copy(isLoading = false, errorRes = R.string.notes_load_failed)
                    }
                }
                .collect { notes ->
                    _uiState.update {
                        it.copy(
                            notes = notes,
                            isLoading = false,
                            isSearching = false,
                            errorRes = null
                        )
                    }
                }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}
