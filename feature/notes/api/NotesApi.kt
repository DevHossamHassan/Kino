package com.letsgotoperfection.kino.feature.notes.api

import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Public API for Notes feature.
 * This allows other feature modules to access notes data and operations.
 * 
 * This API provides:
 * - Data queries (get, search, observe)
 * - Data mutations (create, update, delete)
 * - Business operations (attach to tasks, tag management)
 * 
 * @since 1.0.0
 * @see com.letsgotoperfection.kino.feature.kanban.api.KanbanApi for task operations
 * @see com.letsgotoperfection.kino.feature.media.api.MediaApi for media operations
 */
interface NotesApi {
    
    /**
     * Get note by ID.
     * 
     * @param noteId The unique note identifier
     * @return Result containing the Note or an error
     */
    suspend fun getNote(noteId: String): Result<Note>
    
    /**
     * Get all notes for a user.
     * 
     * @return Flow of all notes
     */
    fun getAllNotes(): Flow<List<Note>>
    
    /**
     * Search notes by query.
     * 
     * @param query The search query
     * @return Result containing matching notes
     */
    suspend fun searchNotes(query: String): Result<List<Note>>
    
    /**
     * Get notes by tag.
     * 
     * @param tag The tag to filter by
     * @return Flow of notes with the specified tag
     */
    fun getNotesByTag(tag: String): Flow<List<Note>>
    
    /**
     * Create a new note.
     * 
     * @param title The note title
     * @param content The note content
     * @param tags Optional list of tags
     * @return Result containing the created Note ID or an error
     */
    suspend fun createNote(title: String, content: String, tags: List<String> = emptyList()): Result<String>
    
    /**
     * Update an existing note.
     * 
     * @param noteId The note ID to update
     * @param title Optional new title
     * @param content Optional new content
     * @param tags Optional new tags
     * @return Result indicating success or failure
     */
    suspend fun updateNote(
        noteId: String, 
        title: String? = null, 
        content: String? = null, 
        tags: List<String>? = null
    ): Result<Unit>
    
    /**
     * Delete a note.
     * 
     * @param noteId The note ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteNote(noteId: String): Result<Unit>
    
    /**
     * Attach note to a task.
     * 
     * @param noteId The note ID
     * @param taskId The task ID to attach to
     * @return Result indicating success or failure
     */
    suspend fun attachNoteToTask(noteId: String, taskId: String): Result<Unit>
    
    /**
     * Detach note from a task.
     * 
     * @param noteId The note ID
     * @param taskId The task ID to detach from
     * @return Result indicating success or failure
     */
    suspend fun detachNoteFromTask(noteId: String, taskId: String): Result<Unit>
    
    /**
     * Get all tags used in notes.
     * 
     * @return Flow of all unique tags
     */
    fun getAllTags(): Flow<List<String>>
    
    /**
     * Get notes attached to a specific task.
     * 
     * @param taskId The task ID
     * @return Flow of notes attached to the task
     */
    fun getNotesForTask(taskId: String): Flow<List<Note>>
}
