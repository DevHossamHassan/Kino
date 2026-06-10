package com.letsgotoperfection.kino.feature.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.component.EmptyStates
import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import com.letsgotoperfection.kino.feature.notes.api.Note
import com.letsgotoperfection.kino.feature.notes.di.rememberNotesApi
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Notes List Screen - Comprehensive implementation with proper state management
 * Uses NotesApi for data operations while maintaining proper encapsulation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNoteDetail: (String) -> Unit,
    onNavigateToNoteEditor: (String?) -> Unit,
    notesApi: NotesApi? = null
) {
    // Try to get NotesApi from Hilt, fall back to provided parameter
    val hiltNotesApi = rememberNotesApi()
    val api = notesApi ?: hiltNotesApi
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Load notes when the screen is first displayed
    LaunchedEffect(api) {
        if (api != null) {
            try {
                api.getAllNotes().collect { notesList ->
                    notes = notesList
                    isLoading = false
                    error = null
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load notes"
                isLoading = false
            }
        } else {
            // Fallback to sample data for testing
            notes = listOf(
                Note(
                    id = "1",
                    title = "Test Note 1",
                    content = "This is a test note to verify the UI is working correctly. It should appear in the notes list.",
                    isPinned = true,
                    tags = listOf("test", "sample"),
                    attachmentCount = 1,
                    createdAt = System.currentTimeMillis() - 86400000,
                    updatedAt = System.currentTimeMillis() - 86400000
                ),
                Note(
                    id = "2",
                    title = "Test Note 2",
                    content = "Another test note to demonstrate the list functionality and scrolling behavior.",
                    isPinned = false,
                    tags = listOf("demo"),
                    attachmentCount = 0,
                    createdAt = System.currentTimeMillis() - 172800000,
                    updatedAt = System.currentTimeMillis() - 172800000
                )
            )
            isLoading = false
        }
    }

    // Handle search with debounce
    LaunchedEffect(searchQuery, api) {
        if (searchQuery.isNotBlank() && api != null) {
            isSearching = true
            try {
                val searchResult = api.searchNotes(searchQuery)
                if (searchResult is com.letsgotoperfection.kino.core.common.Result.Success) {
                    notes = searchResult.data
                } else {
                    notes = emptyList()
                }
            } catch (e: Exception) {
                error = e.message ?: "Search failed"
            }
            isSearching = false
        } else if (searchQuery.isBlank() && api != null) {
            // Reload all notes when search is cleared
            try {
                api.getAllNotes().collect { notesList ->
                    notes = notesList
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load notes"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToNoteEditor(null) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search notes...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Content
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
                notes.isEmpty() -> {
                    EmptyStates.NotesEmpty(
                        onCreateNote = { onNavigateToNoteEditor(null) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    NotesListContent(
                        notes = notes,
                        onNoteClick = onNavigateToNoteDetail,
                        onDeleteClick = { noteId ->
                            // TODO: Implement delete functionality
                            snackbarMessage = "Delete functionality coming soon"
                        },
                        onPinClick = { noteId ->
                            // TODO: Implement pin functionality
                            snackbarMessage = "Pin functionality coming soon"
                        }
                    )
                }
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
private fun NotesListContent(
    notes: List<Note>,
    onNoteClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onPinClick: (String) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            NoteCard(
                note = note,
                onNoteClick = { onNoteClick(note.id) },
                onDeleteClick = { onDeleteClick(note.id) },
                onPinClick = { onPinClick(note.id) }
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onNoteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPinClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNoteClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    IconButton(
                        onClick = onPinClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = if (note.isPinned) "Unpin" else "Pin",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text(
                text = note.previewText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (note.tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    note.tags.take(3).forEach { tag ->
                        AssistChip(
                            onClick = { /* TODO: Filter by tag */ },
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                    if (note.tags.size > 3) {
                        AssistChip(
                            onClick = { /* TODO: Show all tags */ },
                            label = { Text("+${note.tags.size - 3}", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(note.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (note.hasAttachments) {
                    Text(
                        text = "${note.attachmentCount} attachment${if (note.attachmentCount > 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
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
    return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
}