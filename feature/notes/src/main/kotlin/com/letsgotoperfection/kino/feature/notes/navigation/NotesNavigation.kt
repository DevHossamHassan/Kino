package com.letsgotoperfection.kino.feature.notes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letsgotoperfection.kino.feature.notes.internal.presentation.ui.NoteDetailScreen
import com.letsgotoperfection.kino.feature.notes.internal.presentation.ui.NoteEditorScreen
import com.letsgotoperfection.kino.feature.notes.internal.presentation.ui.NotesListScreen
import kotlinx.serialization.Serializable

/**
 * Notes feature routes — type-safe navigation.
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
 * Notes navigation graph wired by the app module.
 */
fun NavGraphBuilder.notesGraph(
    onNoteClick: (String) -> Unit,
    onNavigateToEditor: (String?) -> Unit,
    onBackClick: () -> Unit
) {
    composable<NotesListRoute> {
        NotesListScreen(
            onNavigateBack = onBackClick,
            onNavigateToNoteDetail = onNoteClick,
            onNavigateToNoteEditor = onNavigateToEditor
        )
    }

    composable<NoteDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<NoteDetailRoute>()
        NoteDetailScreen(
            noteId = route.noteId,
            onNavigateBack = onBackClick,
            onNavigateToEditor = onNavigateToEditor
        )
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
 * Deep link patterns for the notes feature.
 */
object NotesDeepLinks {
    const val NOTES_LIST = "kino://app/notes"
    const val NOTE_DETAIL = "kino://app/note/{noteId}"
    const val NOTE_EDITOR = "kino://app/note/editor"

    fun createNoteDetailDeepLink(noteId: String) = "kino://app/note/$noteId"
    fun createNoteEditorDeepLink(noteId: String? = null) =
        if (noteId != null) "kino://app/note/editor?noteId=$noteId" else "kino://app/note/editor"
}
