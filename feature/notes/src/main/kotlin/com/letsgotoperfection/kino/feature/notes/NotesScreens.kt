package com.letsgotoperfection.kino.feature.notes

import androidx.compose.runtime.Composable
import com.letsgotoperfection.kino.feature.notes.NotesListScreen
import com.letsgotoperfection.kino.feature.notes.NoteDetailScreen
import com.letsgotoperfection.kino.feature.notes.NoteEditorScreen
import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Public API composables for Notes feature.
 * 
 * These composables expose the internal UI screens to the navigation module
 * while maintaining proper encapsulation.
 */

/**
 * Notes List Screen - Public API
 */
@Composable
fun NotesListScreenApi(
    onNavigateBack: () -> Unit,
    onNavigateToNoteDetail: (String) -> Unit,
    onNavigateToNoteEditor: (String?) -> Unit,
    notesApi: NotesApi? = null
) {
    NotesListScreen(
        onNavigateBack = onNavigateBack,
        onNavigateToNoteDetail = onNavigateToNoteDetail,
        onNavigateToNoteEditor = onNavigateToNoteEditor,
        notesApi = notesApi
    )
}

/**
 * Note Detail Screen - Public API
 */
@Composable
fun NoteDetailScreenApi(
    noteId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (String) -> Unit,
    notesApi: NotesApi? = null
) {
    NoteDetailScreen(
        noteId = noteId,
        onNavigateBack = onNavigateBack,
        onNavigateToEditor = onNavigateToEditor,
        notesApi = notesApi
    )
}

/**
 * Note Editor Screen - Public API
 */
@Composable
fun NoteEditorScreenApi(
    noteId: String?,
    onNavigateBack: () -> Unit,
    notesApi: NotesApi? = null
) {
    NoteEditorScreen(
        noteId = noteId,
        onNavigateBack = onNavigateBack,
        notesApi = notesApi
    )
}
