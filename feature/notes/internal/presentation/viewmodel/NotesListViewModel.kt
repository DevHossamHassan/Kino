package com.letsgotoperfection.kino.feature.notes.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteSort
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.CreateNoteUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.DeleteNoteUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.GetAllNotesUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.SearchNotesUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.ToggleNotePinUseCase
import com.letsgotoperfection.kino.feature.notes.internal.presentation.state.NotesListAction
import com.letsgotoperfection.kino.feature.notes.internal.presentation.state.NotesListEvent
import com.letsgotoperfection.kino.feature.notes.internal.presentation.state.NotesListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * ViewModel for the notes list screen.
 * Manages notes list state and handles user actions.
 */
@HiltViewModel
internal class NotesListViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val toggleNotePinUseCase: ToggleNotePinUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<NotesListUiState>(NotesListUiState(isLoading = true))
    val uiState: StateFlow<NotesListUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = Channel<NotesListEvent>(Channel.BUFFERED)
    val uiEvent: SharedFlow<NotesListEvent> = _uiEvent.receiveAsFlow()

    private var notesJob: Job? = null
    private var searchJob: Job? = null
    
    init {
        loadNotes()
        setupSearchDebounce()
    }
    
    fun onAction(action: NotesListAction) {
        when (action) {
            is NotesListAction.LoadNotes -> loadNotes()
            is NotesListAction.FilterNotes -> filterNotes(action.filter)
            is NotesListAction.SortNotes -> sortNotes(action.sort)
            is NotesListAction.SearchNotes -> searchNotes(action.query)
            is NotesListAction.CreateNote -> createNote(action.title, action.content)
            is NotesListAction.DeleteNote -> deleteNote(action.noteId)
            is NotesListAction.TogglePin -> togglePin(action.noteId)
            is NotesListAction.NavigateToNoteDetail -> {
                // Navigation handled by UI callbacks
            }
            is NotesListAction.NavigateToNoteEditor -> {
                // Navigation handled by UI callbacks
            }
            is NotesListAction.DismissSnackbar -> dismissSnackbar()
        }
    }
    
    private fun loadNotes() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        observeNotes(
            flow = getAllNotesUseCase(
                filter = currentState.selectedFilter,
                sort = currentState.selectedSort
            )
        )
    }
    
    private fun filterNotes(filter: NoteFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadNotes()
    }
    
    private fun sortNotes(sort: NoteSort) {
        _uiState.value = _uiState.value.copy(selectedSort = sort)
        loadNotes()
    }
    
    private fun searchNotes(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
    
    private fun setupSearchDebounce() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(300) // 300ms debounce
                .collect { query ->
                    if (query.isBlank()) {
                        loadNotes()
                    } else {
                        performSearch(query)
                    }
                }
        }
    }
    
    private fun performSearch(query: String) {
        val currentState = _uiState.value
        val sort = currentState.selectedSort
        
        _uiState.update { it.copy(isSearching = true) }
        
        observeNotes(
            flow = searchNotesUseCase(query).map { notes ->
                sortNotes(notes, sort)
            }
        )
    }
    
    private fun createNote(title: String, content: androidx.compose.ui.text.AnnotatedString) {
        viewModelScope.launch {
            createNoteUseCase(title, content)
                .onSuccess {
                    _uiEvent.send(NotesListEvent.ShowSnackbar("Note created"))
                    loadNotes()
                }
                .onFailure { error ->
                    _uiEvent.send(NotesListEvent.ShowSnackbar(
                        error.message ?: "Failed to create note"
                    ))
                }
        }
    }
    
    private fun deleteNote(noteId: String) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
                .onSuccess {
                    _uiEvent.send(NotesListEvent.ShowSnackbar("Note deleted"))
                    loadNotes()
                }
                .onFailure { error ->
                    _uiEvent.send(NotesListEvent.ShowSnackbar(
                        error.message ?: "Failed to delete note"
                    ))
                }
        }
    }
    
    private fun togglePin(noteId: String) {
        viewModelScope.launch {
            toggleNotePinUseCase(noteId)
                .onSuccess { isPinned ->
                    val message = if (isPinned) "Note pinned" else "Note unpinned"
                    _uiEvent.send(NotesListEvent.ShowSnackbar(message))
                    loadNotes()
                }
                .onFailure { error ->
                    _uiEvent.send(
                        NotesListEvent.ShowSnackbar(
                            error.message ?: "Failed to update pin status"
                        )
                    )
                }
        }
    }
    
    // Navigation methods removed - handled by UI callbacks
    
    private fun dismissSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    private fun observeNotes(flow: Flow<List<Note>>) {
        notesJob?.cancel()
        notesJob = viewModelScope.launch {
            flow
                .onStart {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load notes"
                        )
                    }
                }
                .collect { notes ->
                    val pinnedNotes = notes.filter { it.isPinned }
                    val unpinnedNotes = notes.filter { !it.isPinned }
                    
                    _uiState.update {
                        it.copy(
                            notes = notes,
                            pinnedNotes = pinnedNotes,
                            unpinnedNotes = unpinnedNotes,
                            isLoading = false,
                            isSearching = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun sortNotes(notes: List<Note>, sort: NoteSort): List<Note> {
        return when (sort) {
            NoteSort.TITLE_ASC -> notes.sortedBy { it.title }
            NoteSort.TITLE_DESC -> notes.sortedByDescending { it.title }
            NoteSort.CREATED_ASC -> notes.sortedBy { it.createdAt }
            NoteSort.CREATED_DESC -> notes.sortedByDescending { it.createdAt }
            NoteSort.UPDATED_ASC -> notes.sortedBy { it.updatedAt }
            NoteSort.UPDATED_DESC -> notes.sortedByDescending { it.updatedAt }
        }
    }
}
