package com.letsgotoperfection.kino.feature.notes.api

import androidx.navigation.NavController
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Public API for Notes feature.
 * 
 * This API allows other feature modules to:
 * - Query notes
 * - Create/update notes
 * - Link notes to tasks
 * 
 * @since 1.0.0
 * @see com.letsgotoperfection.kino.feature.kanban.api.KanbanApi for task operations
 */
interface NotesApi {
    
    /**
     * Retrieves a note by its unique identifier.
     * 
     * @param noteId The unique note identifier
     * @return Result containing the Note or an error
     */
    suspend fun getNote(noteId: String): Result<Note>
    
    /**
     * Creates a new note
     * 
     * @param note The note to create
     * @return Result containing the created note ID or an error
     */
    suspend fun createNote(note: Note): Result<String>
    
    /**
     * Updates an existing note
     * 
     * @param note The note to update
     * @return Result indicating success or failure
     */
    suspend fun updateNote(note: Note): Result<Unit>
    
    /**
     * Deletes a note by ID
     * 
     * @param noteId The note ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteNote(noteId: String): Result<Unit>
    
    /**
     * Links a note to a task
     * 
     * @param noteId The note ID to link
     * @param taskId The task ID to link to
     * @return Result indicating success or failure
     */
    suspend fun linkNoteToTask(noteId: String, taskId: String): Result<Unit>
    
    /**
     * Navigate to note editor screen
     * 
     * @param navController The navigation controller
     * @param noteId The note ID to edit (null for new note)
     */
    fun navigateToNoteEditor(navController: NavController, noteId: String? = null)
    
    /**
     * Get observable note updates
     * 
     * @return Flow of note update events
     */
    fun observeNoteUpdates(): Flow<NoteUpdate>
}

/**
 * Note update event for cross-feature communication
 */
data class NoteUpdate(
    val noteId: String,
    val type: UpdateType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class UpdateType {
    CREATED, UPDATED, DELETED, LINKED_TO_TASK, UNLINKED_FROM_TASK
}

