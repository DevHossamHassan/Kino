package com.letsgotoperfection.kino.feature.notes.internal.api

import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavController
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import com.letsgotoperfection.kino.feature.notes.api.NotesDestinations
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.CreateNoteUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.GetNoteByIdUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotesApiImpl @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val createNoteUseCase: CreateNoteUseCase
) : NotesApi {

    override suspend fun getNote(noteId: String): Result<Note> {
        return runCatching { getNoteByIdUseCase(noteId).first() }
            .fold(
                onSuccess = { note ->
                    if (note != null) Result.success(note) else Result.failure(NoSuchElementException("Note not found"))
                },
                onFailure = { Result.failure(it) }
            )
    }

    override suspend fun createNote(title: String, content: String): Result<String> {
        return createNoteUseCase(title, AnnotatedString(content))
            .map { note -> note.id }
    }

    override fun navigateToNotesList(navController: NavController) {
        navController.navigate(NotesDestinations.NOTES_LIST)
    }

    override fun navigateToNoteDetail(navController: NavController, noteId: String) {
        navController.navigate(NotesDestinations.noteDetailRoute(noteId))
    }

    override fun navigateToNoteEditor(navController: NavController, noteId: String?) {
        navController.navigate(NotesDestinations.noteEditorRoute(noteId))
    }
}
