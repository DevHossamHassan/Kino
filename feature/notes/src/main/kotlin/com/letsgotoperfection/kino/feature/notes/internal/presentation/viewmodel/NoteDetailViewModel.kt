package com.letsgotoperfection.kino.feature.notes.internal.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.DeleteNoteUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.GetNoteByIdUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.ToggleNotePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the note detail screen. Observes a note reactively so the
 * screen updates automatically after edits.
 */
@HiltViewModel
internal class NoteDetailViewModel @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val toggleNotePinUseCase: ToggleNotePinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<NoteDetailEvent>(Channel.BUFFERED)
    val uiEvent: Flow<NoteDetailEvent> = _uiEvent.receiveAsFlow()

    private var noteJob: Job? = null
    private var isDeleting = false

    fun loadNote(noteId: String) {
        noteJob?.cancel()
        _uiState.update { it.copy(isLoading = true, errorRes = null) }
        noteJob = viewModelScope.launch {
            getNoteByIdUseCase(noteId)
                .catch {
                    _uiState.update {
                        it.copy(isLoading = false, errorRes = R.string.notes_load_failed)
                    }
                }
                .collect { note ->
                    if (note == null && isDeleting) return@collect
                    _uiState.update {
                        it.copy(
                            note = note,
                            isLoading = false,
                            errorRes = if (note == null) R.string.notes_editor_not_found else null
                        )
                    }
                }
        }
    }

    fun togglePin() {
        val noteId = _uiState.value.note?.id ?: return
        viewModelScope.launch {
            toggleNotePinUseCase(noteId).onFailure {
                _uiEvent.send(NoteDetailEvent.ShowSnackbar(R.string.notes_note_update_failed))
            }
        }
    }

    fun deleteNote() {
        val noteId = _uiState.value.note?.id ?: return
        viewModelScope.launch {
            isDeleting = true
            deleteNoteUseCase(noteId)
                .onSuccess {
                    _uiEvent.send(NoteDetailEvent.NoteDeleted)
                }
                .onFailure {
                    isDeleting = false
                    _uiEvent.send(NoteDetailEvent.ShowSnackbar(R.string.notes_note_delete_failed))
                }
        }
    }
}

/**
 * UI state for the note detail screen.
 */
@Immutable
internal data class NoteDetailUiState(
    val note: Note? = null,
    val isLoading: Boolean = false,
    @StringRes val errorRes: Int? = null
)

/**
 * One-time events for the note detail screen.
 */
internal sealed interface NoteDetailEvent {
    data class ShowSnackbar(@StringRes val messageRes: Int) : NoteDetailEvent
    data object NoteDeleted : NoteDetailEvent
}
