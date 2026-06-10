package com.letsgotoperfection.kino.core.database.dao

import androidx.room.*
import com.letsgotoperfection.kino.core.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?
    
    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun observeNoteById(noteId: String): Flow<NoteEntity?>
    
    @Query("SELECT * FROM notes WHERE isPinned = 1 ORDER BY updatedAt DESC")
    fun getPinnedNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchNotes(query: String): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE isPinned = 0 ORDER BY updatedAt DESC")
    fun getUnpinnedNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE attachmentCount > 0 ORDER BY updatedAt DESC")
    fun getNotesWithAttachments(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE updatedAt >= :since ORDER BY updatedAt DESC")
    fun getRecentNotes(since: Long): Flow<List<NoteEntity>>
    
    @Upsert
    suspend fun upsertNote(note: NoteEntity)
    
    @Delete
    suspend fun deleteNote(note: NoteEntity)
    
    @Query("UPDATE notes SET isPinned = :pinned WHERE id = :noteId")
    suspend fun updatePinned(noteId: String, pinned: Boolean)
    
    @Query("UPDATE notes SET updatedAt = :timestamp WHERE id = :noteId")
    suspend fun updateTimestamp(noteId: String, timestamp: Long)

    @Query("UPDATE notes SET attachmentCount = :count WHERE id = :noteId")
    suspend fun updateAttachmentCount(noteId: String, count: Int)
    
    @Query("SELECT * FROM notes WHERE id IN (SELECT noteId FROM note_labels WHERE labelId = :labelId) ORDER BY updatedAt DESC")
    fun getNotesByLabel(labelId: String): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE updatedAt >= :since ORDER BY updatedAt DESC")
    fun getNotesSince(since: Long): Flow<List<NoteEntity>>
}
