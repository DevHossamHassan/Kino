package com.letsgotoperfection.kino.feature.notes.api

import androidx.navigation.NavController
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note

/**
 * Public API for Notes feature.
 * This allows other feature modules to interact with notes functionality.
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
     * Create a new note.
     * 
     * @param title The note title
     * @param content The note content
     * @return Result containing the created Note ID or an error
     */
    suspend fun createNote(title: String, content: String): Result<String>
    
    /**
     * Navigate to notes list screen.
     * 
     * @param navController Navigation controller
     */
    fun navigateToNotesList(navController: NavController)
    
    /**
     * Navigate to note detail screen.
     * 
     * @param navController Navigation controller
     * @param noteId The unique note identifier
     */
    fun navigateToNoteDetail(navController: NavController, noteId: String)
    
    /**
     * Navigate to note editor screen.
     * 
     * @param navController Navigation controller
     * @param noteId Optional note ID for editing existing note
     */
    fun navigateToNoteEditor(navController: NavController, noteId: String? = null)
}

/**
 * Navigation destinations for Notes feature.
 */
object NotesDestinations {
    const val NOTES_LIST = "notes_list"
    const val NOTE_DETAIL = "note_detail/{noteId}"
    const val NOTE_EDITOR = "note_editor/{noteId?}"
    
    fun noteDetailRoute(noteId: String) = "note_detail/$noteId"
    fun noteEditorRoute(noteId: String? = null) = "note_editor/$noteId"
}
