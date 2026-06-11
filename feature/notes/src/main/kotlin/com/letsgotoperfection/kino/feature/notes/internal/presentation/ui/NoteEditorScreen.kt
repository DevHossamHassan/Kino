package com.letsgotoperfection.kino.feature.notes.internal.presentation.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.letsgotoperfection.kino.core.designsystem.component.MarkdownTextEditor
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.notes.internal.presentation.viewmodel.AttachmentMetadata
import com.letsgotoperfection.kino.feature.notes.internal.presentation.viewmodel.EditorAttachmentUiModel
import com.letsgotoperfection.kino.feature.notes.internal.presentation.viewmodel.NoteEditorEvent
import com.letsgotoperfection.kino.feature.notes.internal.presentation.viewmodel.NoteEditorViewModel

/**
 * Note editor screen — create or edit a note with markdown content,
 * pinning and file attachments.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteEditorScreen(
    noteId: String?,
    onNavigateBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val attachmentPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val mimeType = context.contentResolver.getType(uri) ?: DEFAULT_MIME_TYPE
            var displayName = uri.lastPathSegment.orEmpty()
            var size = 0L
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) displayName = cursor.getString(nameIndex) ?: displayName
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex >= 0) size = cursor.getLong(sizeIndex)
                }
            }
            viewModel.onAttachmentAdded(
                AttachmentMetadata(
                    uri = uri,
                    displayName = displayName,
                    mimeType = mimeType,
                    size = size
                )
            )
        }.onFailure {
            viewModel.onAttachmentReadFailed()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NoteEditorEvent.Saved -> {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.notes_editor_saved)
                    )
                    onNavigateBack()
                }
                is NoteEditorEvent.ShowMessage ->
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                is NoteEditorEvent.OpenAttachment -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(event.uri, event.mimeType)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.notes_editor_no_app_to_open)
                        )
                    }
                }
                is NoteEditorEvent.Finish -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (noteId != null) R.string.notes_editor_edit_note
                            else R.string.notes_editor_new_note
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.notes_editor_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onTogglePinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = stringResource(
                                if (uiState.isPinned) R.string.notes_editor_unpin
                                else R.string.notes_editor_pin
                            ),
                            tint = if (uiState.isPinned) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    } else {
                        IconButton(
                            onClick = viewModel::saveNote,
                            enabled = uiState.canSave
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text(stringResource(R.string.notes_editor_new_note)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    singleLine = true
                )

                MarkdownTextEditor(
                    value = uiState.content,
                    onValueChange = viewModel::onContentChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    placeholder = stringResource(R.string.notes_editor_placeholder),
                    onAddAttachment = {
                        attachmentPicker.launch(arrayOf(ANY_MIME_TYPE))
                    }
                )

                if (uiState.attachments.isNotEmpty()) {
                    AttachmentsRow(
                        attachments = uiState.attachments,
                        onAttachmentClick = viewModel::onAttachmentClicked,
                        onAttachmentRemove = viewModel::onAttachmentRemoved
                    )
                }

                uiState.errorRes?.let { errorRes ->
                    Text(
                        text = stringResource(errorRes),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun AttachmentsRow(
    attachments: List<EditorAttachmentUiModel>,
    onAttachmentClick: (String) -> Unit,
    onAttachmentRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(attachments, key = { it.id }) { attachment ->
            InputChip(
                selected = false,
                onClick = { onAttachmentClick(attachment.id) },
                label = { Text(attachment.displayName, maxLines = 1) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Attachment,
                        contentDescription = null,
                        modifier = Modifier.padding(2.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onAttachmentRemove(attachment.id) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(
                                R.string.notes_editor_remove_attachment
                            )
                        )
                    }
                }
            )
        }
    }
}

private const val DEFAULT_MIME_TYPE = "application/octet-stream"
private const val ANY_MIME_TYPE = "*/*"
