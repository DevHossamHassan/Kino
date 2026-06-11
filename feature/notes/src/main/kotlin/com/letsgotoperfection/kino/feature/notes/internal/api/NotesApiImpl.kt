package com.letsgotoperfection.kino.feature.notes.internal.api

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.feature.notes.api.Note
import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note as DomainNote

/**
 * Real implementation of the public [NotesApi], backed by [NotesRepository].
 */
@Singleton
internal class NotesApiImpl @Inject constructor(
    private val repository: NotesRepository
) : NotesApi {

    override fun getAllNotes(): Flow<List<Note>> =
        repository.getAllNotes().map { notes -> notes.map { it.toPublic() } }

    override suspend fun searchNotes(query: String): Result<List<Note>> =
        try {
            Result.Success(repository.searchNotes(query).first().map { it.toPublic() })
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun getNote(noteId: String): Result<Note> =
        try {
            val note = repository.getNoteById(noteId).first()
            if (note != null) {
                Result.Success(note.toPublic())
            } else {
                Result.Error(NoSuchElementException("Note not found: $noteId"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }

    override fun getNotesWithMedia(): Flow<List<Note>> =
        repository.getNotesWithAttachments().map { notes -> notes.map { it.toPublic() } }

    override suspend fun createNote(title: String, content: String): Result<String> =
        repository.createNote(title, AnnotatedString(content)).fold(
            onSuccess = { Result.Success(it.id) },
            onFailure = { Result.Error(it) }
        )

    override suspend fun updateNote(
        noteId: String,
        title: String?,
        content: String?
    ): Result<Unit> =
        repository.updateNote(
            noteId = noteId,
            title = title,
            content = content?.let { AnnotatedString(it) }
        ).fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it) }
        )

    override suspend fun deleteNote(noteId: String): Result<Unit> =
        repository.deleteNote(noteId).fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it) }
        )

    override suspend fun togglePin(noteId: String): Result<Boolean> =
        repository.togglePin(noteId).fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it) }
        )

    private fun DomainNote.toPublic(): Note = Note(
        id = id,
        title = title,
        content = content.text,
        isPinned = isPinned,
        tags = labels.map { it.name },
        attachmentCount = attachmentCount,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
