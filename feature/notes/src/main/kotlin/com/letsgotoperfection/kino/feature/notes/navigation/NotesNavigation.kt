package com.letsgotoperfection.kino.feature.notes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letsgotoperfection.kino.feature.notes.NotesListScreenApi
import com.letsgotoperfection.kino.feature.notes.NoteDetailScreenApi
import com.letsgotoperfection.kino.feature.notes.NoteEditorScreenApi
import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import com.letsgotoperfection.kino.feature.notes.di.rememberNotesApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
        NotesListScreenApi(
            onNavigateBack = onBackClick,
            onNavigateToNoteDetail = onNoteClick,
            onNavigateToNoteEditor = onNavigateToEditor
        )
    }
    
    composable<NoteDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<NoteDetailRoute>()
        val notesApi: NotesApi? = rememberNotesApi()
        NoteDetailScreenApi(
            noteId = route.noteId,
            onNavigateBack = onBackClick,
            onNavigateToEditor = onNavigateToEditor,
            notesApi = notesApi
        )
    }
    
    composable<NoteEditorRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<NoteEditorRoute>()
        val notesApi: NotesApi? = rememberNotesApi()
        NoteEditorScreenApi(
            noteId = route.noteId,
            onNavigateBack = onBackClick,
            notesApi = notesApi
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
