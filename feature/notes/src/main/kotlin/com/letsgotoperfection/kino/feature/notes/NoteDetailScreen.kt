package com.letsgotoperfection.kino.feature.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.feature.notes.api.Note
import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import com.letsgotoperfection.kino.feature.notes.di.rememberNotesApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Note Detail Screen - Comprehensive note view with all details and actions
 * Shows full note content, metadata, and provides edit/delete/pin actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (String) -> Unit,
    notesApi: NotesApi? = null
) {
    // Try to get NotesApi from Hilt, fall back to provided parameter
    val hiltNotesApi = rememberNotesApi()
    val api = notesApi ?: hiltNotesApi
    
    var note by remember { mutableStateOf<Note?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var shouldDelete by remember { mutableStateOf(false) }
    var shouldTogglePin by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Load note details
    LaunchedEffect(noteId, api) {
        if (api != null) {
            try {
                val result = api.getNote(noteId)
                if (result is com.letsgotoperfection.kino.core.common.Result.Success) {
                    note = result.data
                    isLoading = false
                    error = null
                } else {
                    error = "Note not found"
                    isLoading = false
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load note"
                isLoading = false
            }
        } else {
            // Fallback to sample data for testing
            note = Note(
                id = noteId,
                title = "Sample Note",
                content = "This is a sample note to demonstrate the note detail screen functionality. It shows how the note content, metadata, and actions are displayed to the user.",
                isPinned = true,
                tags = listOf("sample", "demo", "test"),
                attachmentCount = 2,
                createdAt = System.currentTimeMillis() - 86400000,
                updatedAt = System.currentTimeMillis() - 86400000
            )
            isLoading = false
        }
    }

    // Handle delete operation
    LaunchedEffect(shouldDelete) {
        if (shouldDelete && api != null) {
            try {
                val result = api.deleteNote(noteId)
                if (result is com.letsgotoperfection.kino.core.common.Result.Success) {
                    snackbarMessage = "Note deleted successfully"
                    onNavigateBack()
                } else {
                    snackbarMessage = "Failed to delete note"
                }
            } catch (e: Exception) {
                snackbarMessage = "Error deleting note: ${e.message}"
            }
            shouldDelete = false
        }
    }

    // Handle pin toggle operation
    LaunchedEffect(shouldTogglePin) {
        if (shouldTogglePin && api != null) {
            try {
                val result = api.togglePin(noteId)
                if (result is com.letsgotoperfection.kino.core.common.Result.Success) {
                    val newPinStatus = result.data
                    snackbarMessage = if (newPinStatus) "Note pinned" else "Note unpinned"
                    // Refresh the note to show updated pin status
                    val updatedResult = api.getNote(noteId)
                    if (updatedResult is com.letsgotoperfection.kino.core.common.Result.Success) {
                        note = updatedResult.data
                    }
                } else {
                    snackbarMessage = "Failed to toggle pin status"
                }
            } catch (e: Exception) {
                snackbarMessage = "Error toggling pin: ${e.message}"
            }
            shouldTogglePin = false
        }
    }

    // Handle note operations
    val handleDelete = {
        if (api != null) {
            shouldDelete = true
        } else {
            snackbarMessage = "Delete functionality not available"
        }
    }

    val handleTogglePin = {
        if (api != null) {
            shouldTogglePin = true
        } else {
            snackbarMessage = "Pin functionality not available"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEditor(noteId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Note")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToEditor(noteId) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Note")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
            when {
            isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                            text = "Error: $error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            note != null -> {
                NoteDetailContent(
                    note = note!!,
                    onDelete = handleDelete,
                    onTogglePin = handleTogglePin
                )
            }
        }
    }

    // Show snackbar messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            snackbarMessage = null
        }
    }
}

@Composable
private fun NoteDetailContent(
    note: Note,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit
) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Note title
                        Text(
            text = note.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Note metadata
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                        Text(
                text = "Created: ${formatDate(note.createdAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
            
            if (note.isPinned) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pinned",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Tags
        if (note.tags.isNotEmpty()) {
            Text(
                text = "Tags:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(note.tags) { tag ->
                    AssistChip(
                        onClick = { /* TODO: Filter by tag */ },
                        label = { Text(tag) }
                    )
                }
            }
        }

        // Attachments
        if (note.hasAttachments) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📎 ${note.attachmentCount} attachment${if (note.attachmentCount > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Note content
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Content:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pin/Unpin button
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTogglePin() },
                colors = CardDefaults.cardColors(
                    containerColor = if (note.isPinned) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = if (note.isPinned) "Unpin" else "Pin",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (note.isPinned) "Unpin" else "Pin",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Delete button
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDelete() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp),
        ZoneOffset.UTC
    )
    return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"))
}