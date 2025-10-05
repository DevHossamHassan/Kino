package com.letsgotoperfection.kino.feature.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.kino.core.designsystem.component.MarkdownTextEditor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String?,
    onNavigateBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNullOrEmpty()) return@rememberLauncherForActivityResult
        uris.forEach { uri ->
            context.persistReadPermission(uri)
            val metadata = context.resolveAttachmentMetadata(uri)
            if (metadata != null) {
                viewModel.onAttachmentAdded(metadata)
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(context.getString(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_unable_to_read_file))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NoteEditorEvent.Saved -> {
                    snackbarHostState.showSnackbar(context.getString(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_saved))
                    onNavigateBack()
                }
                is NoteEditorEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is NoteEditorEvent.ShowMessageRes -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                }
                is NoteEditorEvent.OpenAttachment -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(event.uri, event.mimeType)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    val packageManager = context.packageManager
                    val hasHandler = intent.resolveActivity(packageManager) != null
                    if (hasHandler) {
                        kotlin.runCatching { context.startActivity(intent) }
                            .onFailure {
                                snackbarHostState.showSnackbar(context.getString(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_unable_to_open_attachment))
                            }
                    } else {
                        snackbarHostState.showSnackbar(context.getString(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_no_app_to_open))
                    }
                }
                NoteEditorEvent.Finish -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            NoteEditorTopBar(
                title = if (noteId == null) stringResource(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_new_note) else stringResource(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_edit_note),
                canSave = uiState.canSave && !uiState.isSaving,
                isSaving = uiState.isSaving,
                isPinned = uiState.isPinned,
                onNavigateBack = onNavigateBack,
                onSaveClick = viewModel::saveNote,
                onTogglePinned = viewModel::onTogglePinned
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = stringResource(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_loading))
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    MarkdownTextEditor(
                        value = uiState.content,
                        onValueChange = viewModel::onContentChange,
                        placeholder = stringResource(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_placeholder),
                        onAddAttachment = {
                            documentLauncher.launch(arrayOf("image/*", "audio/*", "video/*", "application/pdf", "text/plain", "*/*"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    )

                    AttachmentsTray(
                        attachments = uiState.attachments,
                        onRemoveAttachment = viewModel::onAttachmentRemoved,
                        onAttachmentClick = viewModel::onAttachmentClicked,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteEditorTopBar(
    title: String,
    canSave: Boolean,
    isSaving: Boolean,
    isPinned: Boolean,
    onNavigateBack: () -> Unit,
    onSaveClick: () -> Unit,
    onTogglePinned: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(com.letsgotoperfection.kino.core.resources.R.string.navigate_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onTogglePinned) {
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = if (isPinned) stringResource(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_unpin) else stringResource(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_pin),
                    tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
            TextButton(
                onClick = onSaveClick,
                enabled = canSave
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = stringResource(com.letsgotoperfection.kino.core.resources.R.string.save))
                }
            }
        }
    )
}

@Composable
private fun AttachmentsTray(
    attachments: List<EditorAttachmentUiModel>,
    onRemoveAttachment: (String) -> Unit,
    onAttachmentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (attachments.isEmpty()) return

    LazyRow(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(attachments, key = { it.id }) { attachment ->
            AttachmentCard(
                attachment = attachment,
                onRemove = { onRemoveAttachment(attachment.id) },
                onClick = { onAttachmentClick(attachment.id) }
            )
        }
    }
}

@Composable
private fun AttachmentCard(
    attachment: EditorAttachmentUiModel,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    androidx.compose.material3.Card(
        modifier = Modifier
            .width(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = iconForMime(attachment.mimeType),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = Formatter.formatShortFileSize(context, attachment.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(com.letsgotoperfection.kino.core.resources.R.string.notes_editor_remove_attachment)
                )
            }
        }
    }
}

private fun iconForMime(mimeType: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        mimeType.startsWith("image/") -> Icons.Default.Image
        mimeType.startsWith("audio/") -> Icons.Default.MusicNote
        mimeType.startsWith("video/") -> Icons.Default.MovieCreation
        else -> Icons.Default.InsertDriveFile
    }
}

private fun Context.resolveAttachmentMetadata(uri: Uri): AttachmentMetadata? {
    return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (!cursor.moveToFirst()) return@use null

        val displayName = if (nameIndex != -1) cursor.getString(nameIndex) else uri.lastPathSegment ?: getString(com.letsgotoperfection.kino.core.resources.R.string.attachment)
        val size = if (sizeIndex != -1) cursor.getLong(sizeIndex) else 0L
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

        AttachmentMetadata(
            uri = uri,
            displayName = displayName,
            mimeType = mimeType,
            size = size
        )
    }
}

private fun Context.persistReadPermission(uri: Uri) {
    runCatching {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}
