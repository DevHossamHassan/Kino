package com.letsgotoperfection.kino.feature.notes.internal.api

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.CreateNoteUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.GetNoteByIdUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.GetAllNotesUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.SearchNotesUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.GetNotesByTagUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.UpdateNoteUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.DeleteNoteUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.AttachNoteToTaskUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.DetachNoteFromTaskUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.GetAllTagsUseCase
import com.letsgotoperfection.kino.feature.notes.internal.domain.usecase.GetNotesForTaskUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotesApiImpl @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val getNotesByTagUseCase: GetNotesByTagUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val attachNoteToTaskUseCase: AttachNoteToTaskUseCase,
    private val detachNoteFromTaskUseCase: DetachNoteFromTaskUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val getNotesForTaskUseCase: GetNotesForTaskUseCase
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

    override fun getAllNotes() = getAllNotesUseCase()

    override suspend fun searchNotes(query: String): Result<List<Note>> {
        return runCatching { searchNotesUseCase(query).first() }
            .fold(
                onSuccess = { Result.success(it) },
                onFailure = { Result.failure(it) }
            )
    }

    override fun getNotesByTag(tag: String) = getNotesByTagUseCase(tag)

    override suspend fun createNote(title: String, content: String, tags: List<String>): Result<String> {
        return createNoteUseCase(title, AnnotatedString(content), tags)
            .map { note -> note.id }
    }

    override suspend fun updateNote(
        noteId: String, 
        title: String?, 
        content: String?, 
        tags: List<String>?
    ): Result<Unit> {
        return updateNoteUseCase(noteId, title, content?.let { AnnotatedString(it) }, tags)
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return deleteNoteUseCase(noteId)
    }

    override suspend fun attachNoteToTask(noteId: String, taskId: String): Result<Unit> {
        return attachNoteToTaskUseCase(noteId, taskId)
    }

    override suspend fun detachNoteFromTask(noteId: String, taskId: String): Result<Unit> {
        return detachNoteFromTaskUseCase(noteId, taskId)
    }

    override fun getAllTags() = getAllTagsUseCase()

    override fun getNotesForTask(taskId: String) = getNotesForTaskUseCase(taskId)
}
