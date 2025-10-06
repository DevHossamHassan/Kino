package com.letsgotoperfection.kino.feature.notes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letsgotoperfection.kino.feature.notes.NotesListScreen
import com.letsgotoperfection.kino.feature.notes.NoteEditorScreen
import kotlinx.serialization.Serializable

/**
 * Notes feature routes - Type-safe navigation
 */
@Serializable
object NotesListRoute

@Serializable
data class NoteDetailRoute(
    val noteId: String
)

@Serializable
data class NoteEditorRoute(
    val noteId: String? = null
)

/**
 * Notes navigation graph
 * Exposes composable screens for app module to wire
 */
fun NavGraphBuilder.notesGraph(
    onNoteClick: (String) -> Unit,
    onNavigateToEditor: (String?) -> Unit,
    onBackClick: () -> Unit
) {
    composable<NotesListRoute> {
        NotesListScreen(
            onNavigateToNoteDetail = onNoteClick,
            onNavigateToNoteEditor = onNavigateToEditor,
            onNavigateBack = onBackClick
        )
    }
    
    composable<NoteDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<NoteDetailRoute>()
        // NoteDetailScreen will be implemented later
        // For now, navigate to editor
        onNavigateToEditor(route.noteId)
    }
    
    composable<NoteEditorRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<NoteEditorRoute>()
        NoteEditorScreen(
            noteId = route.noteId,
            onNavigateBack = onBackClick
        )
    }
}

/**
 * Deep link patterns for notes feature
 */
object NotesDeepLinks {
    const val NOTES_LIST = "kino://app/notes"
    const val NOTE_DETAIL = "kino://app/note/{noteId}"
    const val NOTE_EDITOR = "kino://app/note/editor"
    
    fun createNoteDetailDeepLink(noteId: String) = "kino://app/note/$noteId"
    fun createNoteEditorDeepLink(noteId: String? = null) = 
        if (noteId != null) "kino://app/note/editor?noteId=$noteId" else "kino://app/note/editor"
}
