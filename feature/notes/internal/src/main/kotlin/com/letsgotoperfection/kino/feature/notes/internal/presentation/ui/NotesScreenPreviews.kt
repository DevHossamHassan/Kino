package com.letsgotoperfection.kino.feature.notes.internal.presentation.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.component.*
import com.letsgotoperfection.kino.core.designsystem.preview.*
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.model.*

/**
 * Preview with full data
 */
@Preview(
    name = "Notes Screen - Light",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Preview(
    name = "Notes Screen - Dark",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NotesScreenPreview() {
    KinoTheme {
        Surface {
            NotesScreen(
                notes = listOf(
                    PreviewData.sampleNotePinned,
                    PreviewData.sampleNoteRegular
                ),
                onNoteClick = {},
                onNavigateBack = {}
            )
        }
    }
}

/**
 * Preview with empty state
 */
@Preview(
    name = "Notes Screen - Empty",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun NotesScreenEmptyPreview() {
    KinoTheme {
        Surface {
            EmptyState(
                icon = Icons.Default.Note,
                title = "No notes yet",
                message = "Create your first note to start organizing your thoughts",
                actionLabel = "Create Note",
                onActionClick = {}
            )
        }
    }
}

/**
 * Preview with different note states
 */
@Preview(
    name = "Notes Screen - Different States",
    showBackground = true
)
@Composable
private fun NotesScreenStatesPreview(
    @PreviewParameter(NotePreviewProvider::class) note: Note
) {
    KinoTheme {
        Surface {
            NotesScreen(
                notes = listOf(note),
                onNoteClick = {},
                onNavigateBack = {}
            )
        }
    }
}

/**
 * Preview on tablet
 */
@Preview(
    name = "Notes Screen - Tablet",
    device = "spec:width=600dp,height=1024dp,dpi=240",
    showSystemUi = true
)
@Composable
private fun NotesScreenTabletPreview() {
    KinoTheme {
        Surface {
            NotesScreen(
                notes = listOf(
                    PreviewData.sampleNotePinned,
                    PreviewData.sampleNoteRegular
                ),
                onNoteClick = {},
                onNavigateBack = {}
            )
        }
    }
}

/**
 * Font scale accessibility preview
 */
@Preview(
    name = "Notes Screen - Font Scale Large",
    fontScale = 1.5f,
    showBackground = true
)
@Composable
private fun NotesScreenFontScalePreview() {
    KinoTheme {
        Surface {
            NotesScreen(
                notes = listOf(PreviewData.sampleNotePinned),
                onNoteClick = {},
                onNavigateBack = {}
            )
        }
    }
}

/**
 * RTL preview for Arabic support
 */
@Preview(
    name = "Notes Screen - RTL",
    locale = "ar",
    showBackground = true
)
@Composable
private fun NotesScreenRTLPreview() {
    KinoTheme {
        Surface {
            NotesScreen(
                notes = listOf(PreviewData.sampleNotePinned),
                onNoteClick = {},
                onNavigateBack = {}
            )
        }
    }
}

/**
 * Mock Notes Screen for previews
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesScreen(
    notes: List<Note>,
    onNoteClick: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Add, contentDescription = "Add Note")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        if (notes.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Note,
                title = "No notes yet",
                message = "Create your first note to start organizing your thoughts",
                actionLabel = "Create Note",
                onActionClick = {}
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note.id) }
                    )
                }
            }
        }
    }
}

/**
 * Mock Note Card for previews
 */
@Composable
private fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (note.isPinned) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (note.isPinned) {
                    Icon(
                        Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            if (note.labels.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(note.labels.take(3)) { label ->
                        LabelChip(label = label)
                    }
                    if (note.labels.size > 3) {
                        item {
                            Text(
                                text = "+${note.labels.size - 3}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (note.attachments.isNotEmpty()) {
                    MetaInfo(
                        icon = Icons.Default.AttachFile,
                        text = note.attachments.size.toString()
                    )
                }
                Text(
                    text = note.createdAt.toString().substring(0, 10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
