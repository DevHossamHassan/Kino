package com.letsgotoperfection.kino.feature.notes.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.letsgotoperfection.kino.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Public API for Notes feature.
 * This allows other feature modules to interact with notes in a limited way.
 * 
 * This API provides only what other modules actually need:
 * - Media module: Get notes with media, navigate to specific notes
 * - Task modules: Get notes attached to tasks
 * 
 * @since 1.0.0
 * @see com.letsgotoperfection.kino.feature.media.api.MediaApi for media operations
 */
interface NotesApi {
    
    /**
     * Get all notes.
     * Used by the notes list screen to display all notes.
     * 
     * @return Flow of all notes
     */
    fun getAllNotes(): Flow<List<Note>>
    
    /**
     * Search notes by query.
     * Used by the notes list screen for search functionality.
     * 
     * @param query The search query
     * @return Result containing matching notes
     */
    suspend fun searchNotes(query: String): Result<List<Note>>
    
    /**
     * Get note by ID for navigation purposes.
     * Used by media module to navigate to specific notes.
     * 
     * @param noteId The unique note identifier
     * @return Result containing the Note or an error
     */
    suspend fun getNote(noteId: String): Result<Note>
    
    /**
     * Get all notes that have media attachments.
     * Used by media module to show notes with media.
     * 
     * @return Flow of notes with media attachments
     */
    fun getNotesWithMedia(): Flow<List<Note>>
    
    /**
     * Get notes attached to a specific task.
     * Used by task modules to show related notes.
     * 
     * @param taskId The task ID
     * @return Flow of notes attached to the task
     */
    fun getNotesForTask(taskId: String): Flow<List<Note>>
    
    /**
     * Navigate to a specific note.
     * Used by other modules to navigate to note detail screen.
     * 
     * @param noteId The note ID to navigate to
     * @return Navigation route for the note
     */
    fun getNoteDetailRoute(noteId: String): String
    
    /**
     * Create a new note.
     * Used by the note editor for creating new notes.
     * 
     * @param title The note title
     * @param content The note content
     * @param tags List of tags for the note
     * @return Result containing the created note ID or an error
     */
    suspend fun createNote(title: String, content: String, tags: List<String> = emptyList()): Result<String>
    
    /**
     * Update an existing note.
     * Used by the note editor for updating notes.
     * 
     * @param noteId The note ID to update
     * @param title The new title (optional)
     * @param content The new content (optional)
     * @param tags The new tags (optional)
     * @return Result indicating success or failure
     */
    suspend fun updateNote(noteId: String, title: String? = null, content: String? = null, tags: List<String>? = null): Result<Unit>
    
    /**
     * Delete a note.
     * Used by the note detail screen for deleting notes.
     * 
     * @param noteId The note ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteNote(noteId: String): Result<Unit>
    
    /**
     * Toggle pin status of a note.
     * Used by the note detail screen for pinning/unpinning notes.
     * 
     * @param noteId The note ID to toggle
     * @return Result containing the new pin status or an error
     */
    suspend fun togglePin(noteId: String): Result<Boolean>
}

/**
 * Public data model for notes that can be used by other modules.
 * This is a simplified version of the internal Note model.
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
    val isRecentlyUpdated: Boolean get() = 
        updatedAt > (System.currentTimeMillis() - 24 * 60 * 60 * 1000) // 24 hours
    val previewText: String get() = content.take(100) + if (content.length > 100) "..." else ""
}
