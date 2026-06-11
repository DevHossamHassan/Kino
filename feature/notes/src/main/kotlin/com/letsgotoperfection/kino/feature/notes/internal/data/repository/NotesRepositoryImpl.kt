package com.letsgotoperfection.kino.feature.notes.internal.data.repository

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.common.di.IoDispatcher
import com.letsgotoperfection.kino.core.database.dao.LabelDao
import com.letsgotoperfection.kino.core.database.dao.NoteDao
import com.letsgotoperfection.kino.core.database.entity.NoteLabelCrossRef
import com.letsgotoperfection.kino.core.database.mapper.toDomain
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.notes.internal.data.mapper.toDomain
import com.letsgotoperfection.kino.feature.notes.internal.data.mapper.toEntity
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteFilter
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.NoteSort
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed implementation of [NotesRepository].
 */
@Singleton
internal class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val labelDao: LabelDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NotesRepository {

    override fun getAllNotes(filter: NoteFilter, sort: NoteSort): Flow<List<Note>> {
        val notesFlow = when (filter) {
            NoteFilter.ALL -> noteDao.getAllNotes()
            NoteFilter.PINNED -> noteDao.getPinnedNotes()
            NoteFilter.RECENT -> noteDao.getRecentNotes(since = recentThresholdMillis())
            NoteFilter.WITH_LABELS -> noteDao.getAllNotes()
        }

        return notesFlow
            .map { entities ->
                val notes = entities.map { entity ->
                    entity.toDomain(labels = noteLabels(entity.id))
                }
                val filtered = if (filter == NoteFilter.WITH_LABELS) {
                    notes.filter { it.labels.isNotEmpty() }
                } else {
                    notes
                }
                filtered.sortedWith(sort)
            }
            .flowOn(ioDispatcher)
    }

    override fun getNoteById(noteId: String): Flow<Note?> {
        return combine(
            noteDao.observeNoteById(noteId),
            labelDao.getNoteLabels(noteId)
        ) { entity, labels ->
            entity?.toDomain(labels = labels.map { it.toDomain() })
        }.flowOn(ioDispatcher)
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query)
            .map { entities -> entities.map { it.toDomain(labels = noteLabels(it.id)) } }
            .flowOn(ioDispatcher)
    }

    override fun getNotesWithAttachments(): Flow<List<Note>> {
        return noteDao.getNotesWithAttachments()
            .map { entities -> entities.map { it.toDomain(labels = noteLabels(it.id)) } }
            .flowOn(ioDispatcher)
    }

    override suspend fun createNote(
        title: String,
        content: AnnotatedString,
        labels: List<Label>
    ): Result<Note> = withContext(ioDispatcher) {
        runCatching {
            val now = LocalDateTime.now()
            val note = Note(
                id = UUID.randomUUID().toString(),
                title = title,
                content = content,
                isPinned = false,
                labels = labels,
                attachmentCount = 0,
                createdAt = now,
                updatedAt = now
            )

            noteDao.upsertNote(note.toEntity())
            labels.forEach { label ->
                labelDao.addNoteLabel(NoteLabelCrossRef(note.id, label.id))
            }

            note
        }
    }

    override suspend fun updateNote(
        noteId: String,
        title: String?,
        content: AnnotatedString?,
        labels: List<Label>?
    ): Result<Note> = withContext(ioDispatcher) {
        runCatching {
            val existing = noteDao.getNoteById(noteId)
                ?: throw NoteNotFoundException(noteId)

            val updated = existing.copy(
                title = title ?: existing.title,
                content = content?.text ?: existing.content,
                updatedAt = System.currentTimeMillis()
            )
            noteDao.upsertNote(updated)

            labels?.let { newLabels ->
                labelDao.removeAllNoteLabels(noteId)
                newLabels.forEach { label ->
                    labelDao.addNoteLabel(NoteLabelCrossRef(noteId, label.id))
                }
            }

            updated.toDomain(labels = noteLabels(noteId))
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val note = noteDao.getNoteById(noteId)
                ?: throw NoteNotFoundException(noteId)
            noteDao.deleteNote(note)
        }
    }

    override suspend fun togglePin(noteId: String): Result<Boolean> = withContext(ioDispatcher) {
        runCatching {
            val note = noteDao.getNoteById(noteId)
                ?: throw NoteNotFoundException(noteId)
            val newValue = !note.isPinned
            noteDao.updatePinned(noteId, newValue)
            newValue
        }
    }

    private suspend fun noteLabels(noteId: String): List<Label> =
        labelDao.getNoteLabels(noteId).first().map { it.toDomain() }

    private fun recentThresholdMillis(): Long =
        LocalDateTime.now()
            .minusDays(1)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    private fun List<Note>.sortedWith(sort: NoteSort): List<Note> = when (sort) {
        NoteSort.TITLE_ASC -> sortedBy { it.title }
        NoteSort.TITLE_DESC -> sortedByDescending { it.title }
        NoteSort.CREATED_ASC -> sortedBy { it.createdAt }
        NoteSort.CREATED_DESC -> sortedByDescending { it.createdAt }
        NoteSort.UPDATED_ASC -> sortedBy { it.updatedAt }
        NoteSort.UPDATED_DESC -> sortedByDescending { it.updatedAt }
    }
}

/**
 * Thrown when an operation targets a note id that does not exist.
 */
internal class NoteNotFoundException(noteId: String) : Exception("Note not found: $noteId")
