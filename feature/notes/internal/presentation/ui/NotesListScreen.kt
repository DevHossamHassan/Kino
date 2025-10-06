package com.letsgotoperfection.kino.feature.notes.internal.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.kino.core.designsystem.component.LoadingScreen
import com.letsgotoperfection.kino.core.designsystem.component.ErrorScreen
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.presentation.state.NotesListAction
import com.letsgotoperfection.kino.feature.notes.internal.presentation.state.NotesListEvent
import com.letsgotoperfection.kino.feature.notes.internal.presentation.viewmodel.NotesListViewModel
import java.time.format.DateTimeFormatter

/**
 * Notes List Screen - Shows all notes with filtering and search capabilities.
 */
@Composable
fun NotesListScreen(
    viewModel: NotesListViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToNoteDetail: (String) -> Unit,
    onNavigateToNoteEditor: (String?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    
    // Handle UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is NotesListEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is NotesListEvent.NavigateToNoteDetail -> {
                    onNavigateToNoteDetail(event.noteId)
                }
                is NotesListEvent.NavigateToNoteEditor -> {
                    onNavigateToNoteEditor(event.noteId)
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            NotesTopBar(
                onNavigateBack = onNavigateBack,
                onSearchClick = {
                    isSearchVisible = !isSearchVisible
                    if (!isSearchVisible && uiState.searchQuery.isNotBlank()) {
                        viewModel.onAction(NotesListAction.SearchNotes(""))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAction(NotesListAction.NavigateToNoteEditor(null)) },
                modifier = Modifier.padding(bottom = 80.dp) // Account for bottom navigation bar
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Create new note"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorScreen(
                    message = uiState.error,
                    onRetry = { viewModel.onAction(NotesListAction.LoadNotes) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                NotesListContent(
                    notes = uiState.notes,
                    selectedFilter = uiState.selectedFilter,
                    searchQuery = uiState.searchQuery,
                    isSearchVisible = isSearchVisible,
                    onSearchQueryChange = { query ->
                        viewModel.onAction(NotesListAction.SearchNotes(query))
                    },
                    onFilterChange = { filter ->
                        viewModel.onAction(NotesListAction.FilterNotes(filter))
                    },
                    onNoteClick = { noteId ->
                        viewModel.onAction(NotesListAction.NavigateToNoteDetail(noteId))
                    },
                    onNoteDelete = { noteId ->
                        viewModel.onAction(NotesListAction.DeleteNote(noteId))
                    },
                    onNotePin = { noteId ->
                        viewModel.onAction(NotesListAction.TogglePin(noteId))
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesTopBar(
    onNavigateBack: () -> Unit,
    onSearchClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Notes") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search notes"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun NotesListContent(
    notes: List<Note>,
    selectedFilter: NoteFilter,
    searchQuery: String,
    isSearchVisible: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (NoteFilter) -> Unit,
    onNoteClick: (String) -> Unit,
    onNoteDelete: (String) -> Unit,
    onNotePin: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (isSearchVisible) {
            SearchField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Filter chips
        FilterChipsRow(
            selectedFilter = selectedFilter,
            onFilterChange = onFilterChange
        )
        
        // Notes list
        if (notes.isEmpty()) {
            EmptyNotesState()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        onDelete = { onNoteDelete(note.id) },
                        onPin = { onNotePin(note.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search notes") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        },
        modifier = modifier,
        singleLine = true
    )
}

@Composable
private fun FilterChipsRow(
    selectedFilter: NoteFilter,
    onFilterChange: (NoteFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NoteFilter.values().forEach { filter ->
            AssistChip(
                onClick = { onFilterChange(filter) },
                label = { Text(filter.name) },
                selected = selectedFilter == filter
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Row {
                    if (note.isPinned) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    
                    IconButton(
                        onClick = onPin,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = if (note.isPinned) "Unpin" else "Pin",
                            modifier = androidx.compose.ui.Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            if (note.previewText.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = note.previewText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (note.labels.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    note.labels.forEach { label ->
                        AssistChip(
                            onClick = { },
                            label = { Text(label.name) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            Text(
                text = note.updatedAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyNotesState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No notes yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Create your first note to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
