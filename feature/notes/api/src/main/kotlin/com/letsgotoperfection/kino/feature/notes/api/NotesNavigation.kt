package com.letsgotoperfection.kino.feature.notes.api

/**
 * Navigation destinations for Notes feature
 */
object NotesDestinations {
    const val NOTES_LIST = "notes_list"
    const val NOTE_EDITOR = "note_editor/{noteId}"
    
    fun noteEditorRoute(noteId: String? = null) = "note_editor/${noteId ?: "new"}"
}

