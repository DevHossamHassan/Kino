package com.letsgotoperfection.kino.feature.notes.api

import com.letsgotoperfection.kino.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Public API for the Notes feature.
 *
 * Allows other feature modules to read and manipulate notes without depending
 * on the internal implementation.
 *
 * @since 1.0.0
 */
interface NotesApi {

    /**
     * Observe all notes, pinned first, most recently updated next.
     */
    fun getAllNotes(): Flow<List<Note>>

    /**
     * Search notes by free-text query against title and content.
     *
     * @param query The search query
     * @return Result containing matching notes
     */
    suspend fun searchNotes(query: String): Result<List<Note>>

    /**
     * Get a note by id.
     *
     * @param noteId The unique note identifier
     * @return Result containing the Note or an error if not found
     */
    suspend fun getNote(noteId: String): Result<Note>

    /**
     * Observe all notes that have at least one attachment.
     * Used by the media feature to surface notes with media.
     */
    fun getNotesWithMedia(): Flow<List<Note>>

    /**
     * Create a new note.
     *
     * @return Result containing the created note id
     */
    suspend fun createNote(title: String, content: String): Result<String>

    /**
     * Update an existing note; null arguments leave the field unchanged.
     */
    suspend fun updateNote(
        noteId: String,
        title: String? = null,
        content: String? = null
    ): Result<Unit>

    /**
     * Delete a note by id.
     */
    suspend fun deleteNote(noteId: String): Result<Unit>

    /**
     * Toggle the pinned status of a note.
     *
     * @return Result containing the new pin state
     */
    suspend fun togglePin(noteId: String): Result<Boolean>
}

/**
 * Public data model for notes exposed to other modules.
 */
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val tags: List<String>,
    val attachmentCount: Int,
    val createdAt: Long,
    val updatedAt: Long
) {
    val hasAttachments: Boolean get() = attachmentCount > 0
}
