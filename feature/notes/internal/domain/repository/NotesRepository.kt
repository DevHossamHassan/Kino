package com.letsgotoperfection.kino.feature.notes.internal.domain.repository

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteSort
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for notes operations.
 * Provides access to note data and related functionality.
 */
internal interface NotesRepository {
    
    /**
     * Get all notes with optional filtering and sorting.
     */
    fun getAllNotes(
        filter: NoteFilter = NoteFilter.ALL,
        sort: NoteSort = NoteSort.UPDATED_DESC
    ): Flow<List<Note>>
    
    /**
     * Get a specific note by ID.
     */
    fun getNoteById(noteId: String): Flow<Note?>
    
    /**
     * Search notes by query string.
     */
    fun searchNotes(query: String): Flow<List<Note>>
    
    /**
     * Create a new note.
     */
    suspend fun createNote(
        title: String,
        content: AnnotatedString,
        labels: List<Label> = emptyList()
    ): Result<Note>
    
    /**
     * Update an existing note.
     */
    suspend fun updateNote(
        noteId: String,
        title: String? = null,
        content: AnnotatedString? = null,
        labels: List<Label>? = null
    ): Result<Note>
    
    /**
     * Delete a note.
     */
    suspend fun deleteNote(noteId: String): Result<Unit>
    
    /**
     * Toggle pin status of a note.
     */
    suspend fun togglePin(noteId: String): Result<Boolean>
    
    /**
     * Get notes by label.
     */
    fun getNotesByLabel(labelId: String): Flow<List<Note>>
    
    /**
     * Get pinned notes.
     */
    fun getPinnedNotes(): Flow<List<Note>>
    
    /**
     * Get recent notes (updated in last 24 hours).
     */
    fun getRecentNotes(): Flow<List<Note>>
}
