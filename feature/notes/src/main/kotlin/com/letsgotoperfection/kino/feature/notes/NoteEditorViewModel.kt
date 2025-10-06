package com.letsgotoperfection.kino.feature.notes

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.common.di.IoDispatcher
import com.letsgotoperfection.kino.core.database.dao.AttachmentDao
import com.letsgotoperfection.kino.core.database.dao.NoteDao
import com.letsgotoperfection.kino.core.database.entity.AttachmentEntity
import com.letsgotoperfection.kino.core.database.entity.NoteEntity
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

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteDao: NoteDao,
    private val attachmentDao: AttachmentDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var currentNoteId: String? = savedStateHandle.get<String>("noteId")?.takeIf { it.isNotBlank() }
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
            // Observe note reactively to auto-update UI on changes
            // Fixed: Thread-safe state update
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch(ioDispatcher) {
                noteDao.observeNoteById(id).collectLatest { entity ->
                    if (entity == null) {
                        _events.send(NoteEditorEvent.ShowMessageRes(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_not_found))
                        _events.send(NoteEditorEvent.Finish)
                    } else {
                        originalCreatedAt = entity.createdAt
                        // Fixed: Thread-safe state update
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

    /**
     * Fixed: Thread-safe state updates for all UI callbacks
     */
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
                pendingAttachments.value = pendingAttachments.value + attachment.copy(isPersisted = false)
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

    /**
     * Saves the current note to the database
     * Fixed: Used .update{} instead of direct .value assignment to prevent race conditions
     */
    fun saveNote() {
        val stateSnapshot = _uiState.value
        if (!stateSnapshot.canSave || stateSnapshot.isSaving) return

        viewModelScope.launch(ioDispatcher) {
            try {
                // Fixed: Use .update{} for thread-safe state mutations
                _uiState.update { it.copy(isSaving = true, errorMessage = null) }

                val now = System.currentTimeMillis()
                val noteId = currentNoteId ?: UUID.randomUUID().toString()

                val entity = NoteEntity(
                    id = noteId,
                    title = stateSnapshot.title.trim(),
                    content = stateSnapshot.content.text,
                    isPinned = stateSnapshot.isPinned,
                    attachmentCount = stateSnapshot.attachments.count { it.isPersisted } + pendingAttachments.value.size,
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

                // Fixed: Use .update{} instead of .value for thread safety
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
                // Fixed: Thread-safe error state update
                _uiState.update { it.copy(isSaving = false, errorMessage = throwable.message) }
                _events.send(NoteEditorEvent.ShowMessage(throwable.message ?: "Failed to save note"))
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

    /**
     * Observes attachment state changes and updates UI
     * Fixed: Used .update{} instead of direct .value assignment to prevent race conditions
     */
    private fun observeAttachmentUiState() {
        viewModelScope.launch {
            combine(persistedAttachments, pendingAttachments) { persisted, pending ->
                (persisted + pending).sortedByDescending { it.addedAt }
            }.collect { attachments ->
                // Fixed: Thread-safe state update
                _uiState.update { it.copy(attachments = attachments) }
            }
        }
    }

    /**
     * Loads an existing note (legacy method, now replaced by reactive observation)
     * Fixed: Thread-safe state updates
     */
    private suspend fun loadExistingNote(noteId: String) {
        // Deprecated by reactive observation; kept for compatibility if needed elsewhere
        val entity = noteDao.getNoteById(noteId)
        if (entity == null) {
            _events.send(NoteEditorEvent.ShowMessageRes(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_not_found))
            _events.send(NoteEditorEvent.Finish)
            return
        }

        originalCreatedAt = entity.createdAt
        // Fixed: Thread-safe state update
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
        val identifier = UUID.randomUUID().toString()
        return EditorAttachmentUiModel(
            id = identifier,
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

data class NoteEditorUiState(
    val noteId: String? = null,
    val title: String = "",
    val content: TextFieldValue = TextFieldValue(),
    val attachments: List<EditorAttachmentUiModel> = emptyList(),
    val isPinned: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val canSave: Boolean get() =
        title.isNotBlank() || content.text.isNotBlank() || attachments.isNotEmpty()
}

data class EditorAttachmentUiModel(
    val id: String,
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val size: Long,
    val addedAt: Long,
    val isPersisted: Boolean
)

data class AttachmentMetadata(
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val size: Long
)

sealed interface NoteEditorEvent {
    data class Saved(val noteId: String) : NoteEditorEvent
    data class ShowMessage(val message: String) : NoteEditorEvent
    data class ShowMessageRes(val messageRes: Int) : NoteEditorEvent
    data class OpenAttachment(val uri: Uri, val mimeType: String) : NoteEditorEvent
    data object Finish : NoteEditorEvent
}
