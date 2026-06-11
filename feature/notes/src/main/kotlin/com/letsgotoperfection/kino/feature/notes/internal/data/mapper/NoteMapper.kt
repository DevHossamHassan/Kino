package com.letsgotoperfection.kino.feature.notes.internal.data.mapper

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.database.entity.NoteEntity
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal fun NoteEntity.toDomain(labels: List<Label> = emptyList()): Note {
    return Note(
        id = id,
        title = title,
        content = AnnotatedString(text = content),
        isPinned = isPinned,
        labels = labels,
        attachmentCount = attachmentCount,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()),
        updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault())
    )
}

internal fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content.text,
        isPinned = isPinned,
        attachmentCount = attachmentCount,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
