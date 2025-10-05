package com.letsgotoperfection.kino.feature.notes.internal.data.repository

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.database.dao.AttachmentDao
import com.letsgotoperfection.kino.core.database.dao.LabelDao
import com.letsgotoperfection.kino.core.database.dao.NoteDao
import com.letsgotoperfection.kino.feature.notes.internal.data.mapper.toDomain
import com.letsgotoperfection.kino.feature.notes.internal.data.mapper.toEntity
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteSort
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotesRepository.
 * Handles data operations for notes functionality.
 */
@Singleton
internal class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val labelDao: LabelDao,
    private val attachmentDao: AttachmentDao
) : NotesRepository {
    
    override fun getAllNotes(
        filter: NoteFilter,
        sort: NoteSort
    ): Flow<List<Note>> {
        val notesFlow = when (filter) {
            NoteFilter.ALL -> noteDao.getAllNotes()
            NoteFilter.PINNED -> noteDao.getPinnedNotes()
            NoteFilter.RECENT -> getRecentNotesFlow()
            NoteFilter.WITH_LABELS -> getNotesWithLabelsFlow()
        }
        
        return combine(
            notesFlow,
            labelDao.getAllLabels()
        ) { notes, allLabels ->
            notes.map { noteEntity ->
                val noteLabels = labelDao.getNoteLabels(noteEntity.id).first()
                noteEntity.toDomain(labels = noteLabels.map { it.toDomain() })
            }.let { notesWithLabels ->
                when (sort) {
                    NoteSort.TITLE_ASC -> notesWithLabels.sortedBy { it.title }
                    NoteSort.TITLE_DESC -> notesWithLabels.sortedByDescending { it.title }
                    NoteSort.CREATED_ASC -> notesWithLabels.sortedBy { it.createdAt }
                    NoteSort.CREATED_DESC -> notesWithLabels.sortedByDescending { it.createdAt }
                    NoteSort.UPDATED_ASC -> notesWithLabels.sortedBy { it.updatedAt }
                    NoteSort.UPDATED_DESC -> notesWithLabels.sortedByDescending { it.updatedAt }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
    
    override fun getNoteById(noteId: String): Flow<Note?> {
        return combine(
            noteDao.observeNoteById(noteId),
            labelDao.getNoteLabels(noteId)
        ) { noteEntity, labels ->
            noteEntity?.toDomain(labels = labels.map { it.toDomain() })
        }.flowOn(Dispatchers.IO)
    }
    
    override fun searchNotes(query: String): Flow<List<Note>> {
        return combine(
            noteDao.searchNotes(query),
            labelDao.getAllLabels()
        ) { notes, allLabels ->
            notes.map { noteEntity ->
                val noteLabels = labelDao.getNoteLabels(noteEntity.id).first()
                noteEntity.toDomain(labels = noteLabels.map { it.toDomain() })
            }
        }.flowOn(Dispatchers.IO)
    }
    
    override suspend fun createNote(
        title: String,
        content: AnnotatedString,
        labels: List<Label>
    ): Result<Note> = runCatching {
        withContext(Dispatchers.IO) {
            val note = Note(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                content = content,
                isPinned = false,
                labels = labels,
                attachmentCount = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            noteDao.upsertNote(note.toEntity())
            
            // Add labels
            labels.forEach { label ->
                labelDao.addNoteLabel(
                    com.letsgotoperfection.kino.core.database.entity.NoteLabelCrossRef(note.id, label.id)
                )
            }
            
            note
        }
    }
    
    override suspend fun updateNote(
        noteId: String,
        title: String?,
        content: AnnotatedString?,
        labels: List<Label>?
    ): Result<Note> = runCatching {
        withContext(Dispatchers.IO) {
            val existingNote = noteDao.getNoteById(noteId)
                ?: throw NoteNotFoundException("Note not found: $noteId")
            
            val updatedNote = existingNote.copy(
                title = title ?: existingNote.title,
                content = content?.text ?: existingNote.content,
                updatedAt = System.currentTimeMillis()
            )
            
            noteDao.upsertNote(updatedNote)
            
            // Update labels if provided
            labels?.let { newLabels ->
                // Remove existing labels
                val existingLabels = labelDao.getNoteLabels(noteId).first()
                existingLabels.forEach { label ->
                    labelDao.removeNoteLabel(
                        com.letsgotoperfection.kino.core.database.entity.NoteLabelCrossRef(noteId, label.id)
                    )
                }
                
                // Add new labels
                newLabels.forEach { label ->
                    labelDao.addNoteLabel(
                        com.letsgotoperfection.kino.core.database.entity.NoteLabelCrossRef(noteId, label.id)
                    )
                }
            }
            
            val noteLabels = labelDao.getNoteLabels(noteId).first()
            updatedNote.toDomain(labels = noteLabels.map { it.toDomain() })
        }
    }
    
    override suspend fun deleteNote(noteId: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val note = noteDao.getNoteById(noteId)
                ?: throw NoteNotFoundException("Note not found: $noteId")
            
            noteDao.deleteNote(note)
        }
    }
    
    override suspend fun togglePin(noteId: String): Result<Boolean> = runCatching {
        withContext(Dispatchers.IO) {
            val note = noteDao.getNoteById(noteId)
                ?: throw NoteNotFoundException("Note not found: $noteId")
            
            val newPinnedValue = !note.isPinned
            noteDao.updatePinned(noteId, newPinnedValue)
            newPinnedValue
        }
    }
    
    override fun getNotesByLabel(labelId: String): Flow<List<Note>> {
        return combine(
            noteDao.getNotesByLabel(labelId),
            labelDao.getAllLabels()
        ) { notes, allLabels ->
            notes.map { noteEntity ->
                val noteLabels = labelDao.getNoteLabels(noteEntity.id).first()
                noteEntity.toDomain(labels = noteLabels.map { it.toDomain() })
            }
        }.flowOn(Dispatchers.IO)
    }
    
    override fun getPinnedNotes(): Flow<List<Note>> {
        return combine(
            noteDao.getPinnedNotes(),
            labelDao.getAllLabels()
        ) { notes, allLabels ->
            notes.map { noteEntity ->
                val noteLabels = labelDao.getNoteLabels(noteEntity.id).first()
                noteEntity.toDomain(labels = noteLabels.map { it.toDomain() })
            }
        }.flowOn(Dispatchers.IO)
    }
    
    override fun getRecentNotes(): Flow<List<Note>> {
        return getRecentNotesFlow()
    }
    
    private fun getRecentNotesFlow(): Flow<List<Note>> {
        val yesterday = LocalDateTime.now().minusDays(1)
        val yesterdayMillis = yesterday.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return combine(
            noteDao.getAllNotes(),
            labelDao.getAllLabels()
        ) { notes, allLabels ->
            notes
                .filter { it.updatedAt >= yesterdayMillis }
                .map { noteEntity ->
                    val noteLabels = labelDao.getNoteLabels(noteEntity.id).first()
                    noteEntity.toDomain(labels = noteLabels.map { it.toDomain() })
                }
        }.flowOn(Dispatchers.IO)
    }
    
    private fun getNotesWithLabelsFlow(): Flow<List<Note>> {
        return combine(
            noteDao.getAllNotes(),
            labelDao.getAllLabels()
        ) { notes, allLabels ->
            notes
                .filter { noteEntity ->
                    labelDao.getNoteLabels(noteEntity.id).first().isNotEmpty()
                }
                .map { noteEntity ->
                    val noteLabels = labelDao.getNoteLabels(noteEntity.id).first()
                    noteEntity.toDomain(labels = noteLabels.map { it.toDomain() })
                }
        }.flowOn(Dispatchers.IO)
    }
}

/**
 * Exception thrown when a note is not found.
 */
internal class NoteNotFoundException(message: String) : Exception(message)
