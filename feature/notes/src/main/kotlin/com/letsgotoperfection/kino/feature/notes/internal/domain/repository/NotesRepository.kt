package com.letsgotoperfection.kino.feature.notes.internal.domain.repository

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteSort
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for notes data operations.
 */
internal interface NotesRepository {

    /** Observe all notes with optional filtering and sorting. */
    fun getAllNotes(
        filter: NoteFilter = NoteFilter.ALL,
        sort: NoteSort = NoteSort.UPDATED_DESC
    ): Flow<List<Note>>

    /** Observe a single note by id; emits null when the note does not exist. */
    fun getNoteById(noteId: String): Flow<Note?>

    /** Observe notes matching the free-text [query] in title or content. */
    fun searchNotes(query: String): Flow<List<Note>>

    /** Observe notes that have at least one attachment. */
    fun getNotesWithAttachments(): Flow<List<Note>>

    /** Create a new note and return it. */
    suspend fun createNote(
        title: String,
        content: AnnotatedString,
        labels: List<Label> = emptyList()
    ): Result<Note>

    /** Update an existing note; null arguments leave the field unchanged. */
    suspend fun updateNote(
        noteId: String,
        title: String? = null,
        content: AnnotatedString? = null,
        labels: List<Label>? = null
    ): Result<Note>

    /** Delete a note by id. */
    suspend fun deleteNote(noteId: String): Result<Unit>

    /** Toggle the pinned flag and return the new value. */
    suspend fun togglePin(noteId: String): Result<Boolean>
}
