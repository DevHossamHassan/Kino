package com.letsgotoperfection.kino.feature.notes.internal.presentation.viewmodel

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.common.di.IoDispatcher
import com.letsgotoperfection.kino.core.database.dao.AttachmentDao
import com.letsgotoperfection.kino.core.database.dao.NoteDao
import com.letsgotoperfection.kino.core.database.entity.AttachmentEntity
import com.letsgotoperfection.kino.core.database.entity.NoteEntity
import com.letsgotoperfection.kino.core.resources.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

private const val TARGET_NOTE_TYPE = "note"

/**
 * ViewModel for the note editor screen. Supports creating new notes and
 * editing existing ones, including file attachments.
 */
@HiltViewModel
internal class NoteEditorViewModel @Inject constructor(
    private val noteDao: NoteDao,
    private val attachmentDao: AttachmentDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var currentNoteId: String? =
        savedStateHandle.get<String>("noteId")?.takeIf { it.isNotBlank() }
    private val noteIdState = MutableStateFlow(currentNoteId)

    private val persistedAttachments = MutableStateFlow<List<EditorAttachmentUiModel>>(emptyList())
    private val pendingAttachments = MutableStateFlow<List<EditorAttachmentUiModel>>(emptyList())

    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    private val _events = Channel<NoteEditorEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var originalCreatedAt: Long? = null

    init {
        observeAttachments()
        observeAttachmentUiState()

        currentNoteId?.let { id ->
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch(ioDispatcher) {
                noteDao.observeNoteById(id).collectLatest { entity ->
                    if (entity == null) {
                        _events.send(NoteEditorEvent.ShowMessage(R.string.notes_editor_not_found))
                        _events.send(NoteEditorEvent.Finish)
                    } else {
                        originalCreatedAt = entity.createdAt
                        _uiState.update {
                            NoteEditorUiState(
                                noteId = entity.id,
                                title = entity.title,
                                content = TextFieldValue(entity.content),
                                isPinned = entity.isPinned,
                                createdAt = entity.createdAt.toLocalDateTime(),
                                updatedAt = entity.updatedAt.toLocalDateTime(),
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onContentChange(value: TextFieldValue) {
        _uiState.update { it.copy(content = value) }
    }

    fun onTogglePinned() {
        _uiState.update { it.copy(isPinned = !it.isPinned) }
    }

    fun onAttachmentAdded(metadata: AttachmentMetadata) {
        viewModelScope.launch(ioDispatcher) {
            val attachment = metadata.asUiModel(isPersisted = currentNoteId != null)
            val noteId = currentNoteId
            if (noteId == null) {
                pendingAttachments.value =
                    pendingAttachments.value + attachment.copy(isPersisted = false)
            } else {
                attachmentDao.upsertAttachment(attachment.toEntity(noteId))
                updateAttachmentCount(noteId)
            }
        }
    }

    fun onAttachmentRemoved(attachmentId: String) {
        viewModelScope.launch(ioDispatcher) {
            val pendingExisting = pendingAttachments.value
            if (pendingExisting.any { it.id == attachmentId && !it.isPersisted }) {
                pendingAttachments.value = pendingExisting.filterNot { it.id == attachmentId }
                return@launch
            }

            val noteId = currentNoteId ?: return@launch
            attachmentDao.getAttachmentById(attachmentId)?.let { entity ->
                attachmentDao.deleteAttachment(entity)
                updateAttachmentCount(noteId)
            }
        }
    }

    fun onAttachmentClicked(attachmentId: String) {
        val attachment = _uiState.value.attachments.firstOrNull { it.id == attachmentId } ?: return
        viewModelScope.launch {
            _events.send(NoteEditorEvent.OpenAttachment(attachment.uri, attachment.mimeType))
        }
    }

    fun onAttachmentReadFailed() {
        viewModelScope.launch {
            _events.send(NoteEditorEvent.ShowMessage(R.string.notes_editor_unable_to_read_file))
        }
    }

    fun saveNote() {
        val stateSnapshot = _uiState.value
        if (!stateSnapshot.canSave || stateSnapshot.isSaving) return

        viewModelScope.launch(ioDispatcher) {
            try {
                _uiState.update { it.copy(isSaving = true, errorRes = null) }

                val now = System.currentTimeMillis()
                val noteId = currentNoteId ?: UUID.randomUUID().toString()

                val entity = NoteEntity(
                    id = noteId,
                    title = stateSnapshot.title.trim(),
                    content = stateSnapshot.content.text,
                    isPinned = stateSnapshot.isPinned,
                    attachmentCount = stateSnapshot.attachments.count { it.isPersisted } +
                        pendingAttachments.value.size,
                    createdAt = originalCreatedAt ?: now,
                    updatedAt = now
                )

                noteDao.upsertNote(entity)

                if (currentNoteId == null) {
                    currentNoteId = noteId
                    noteIdState.value = noteId
                }

                persistPendingAttachments(noteId)
                updateAttachmentCount(noteId)

                _uiState.update { currentState ->
                    currentState.copy(
                        noteId = noteId,
                        createdAt = entity.createdAt.toLocalDateTime(),
                        updatedAt = entity.updatedAt.toLocalDateTime(),
                        isSaving = false
                    )
                }

                _events.send(NoteEditorEvent.Saved(noteId))
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(isSaving = false, errorRes = R.string.notes_note_update_failed)
                }
                _events.send(NoteEditorEvent.ShowMessage(R.string.notes_note_update_failed))
            }
        }
    }

    private fun observeAttachments() {
        viewModelScope.launch(ioDispatcher) {
            noteIdState
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest { noteId ->
                    attachmentDao.getAttachments(noteId, TARGET_NOTE_TYPE).collect { entities ->
                        persistedAttachments.value = entities.map { it.toUiModel() }
                    }
                }
        }
    }

    private fun observeAttachmentUiState() {
        viewModelScope.launch {
            combine(persistedAttachments, pendingAttachments) { persisted, pending ->
                (persisted + pending).sortedByDescending { it.addedAt }
            }.collect { attachments ->
                _uiState.update { it.copy(attachments = attachments) }
            }
        }
    }

    private suspend fun persistPendingAttachments(noteId: String) {
        val pending = pendingAttachments.value
        if (pending.isEmpty()) return

        pending.forEach { attachment ->
            attachmentDao.upsertAttachment(attachment.toEntity(noteId))
        }
        pendingAttachments.value = emptyList()
    }

    private suspend fun updateAttachmentCount(noteId: String) {
        val persistedCount = attachmentDao.getAttachments(noteId, TARGET_NOTE_TYPE).first().size
        noteDao.updateAttachmentCount(noteId, persistedCount)
    }

    private fun AttachmentMetadata.asUiModel(isPersisted: Boolean): EditorAttachmentUiModel {
        return EditorAttachmentUiModel(
            id = UUID.randomUUID().toString(),
            uri = uri,
            displayName = displayName,
            mimeType = mimeType,
            size = size,
            addedAt = System.currentTimeMillis(),
            isPersisted = isPersisted
        )
    }

    private fun EditorAttachmentUiModel.toEntity(noteId: String): AttachmentEntity {
        return AttachmentEntity(
            id = id,
            targetId = noteId,
            targetType = TARGET_NOTE_TYPE,
            uri = uri.toString(),
            filename = displayName,
            mimeType = mimeType,
            size = size,
            addedAt = addedAt
        )
    }

    private fun AttachmentEntity.toUiModel(): EditorAttachmentUiModel {
        return EditorAttachmentUiModel(
            id = id,
            uri = Uri.parse(uri),
            displayName = filename,
            mimeType = mimeType,
            size = size,
            addedAt = addedAt,
            isPersisted = true
        )
    }

    private fun Long.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}

/**
 * UI state for the note editor screen.
 */
@Immutable
internal data class NoteEditorUiState(
    val noteId: String? = null,
    val title: String = "",
    val content: TextFieldValue = TextFieldValue(),
    val attachments: List<EditorAttachmentUiModel> = emptyList(),
    val isPinned: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    @StringRes val errorRes: Int? = null
) {
    val canSave: Boolean get() =
        title.isNotBlank() || content.text.isNotBlank() || attachments.isNotEmpty()
}

/**
 * UI model for an attachment shown in the editor.
 */
@Immutable
internal data class EditorAttachmentUiModel(
    val id: String,
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val size: Long,
    val addedAt: Long,
    val isPersisted: Boolean
)

/**
 * Metadata describing a file picked by the user to attach to a note.
 */
internal data class AttachmentMetadata(
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val size: Long
)

/**
 * One-time events for the note editor screen.
 */
internal sealed interface NoteEditorEvent {
    data class Saved(val noteId: String) : NoteEditorEvent
    data class ShowMessage(@StringRes val messageRes: Int) : NoteEditorEvent
    data class OpenAttachment(val uri: Uri, val mimeType: String) : NoteEditorEvent
    data object Finish : NoteEditorEvent
}
