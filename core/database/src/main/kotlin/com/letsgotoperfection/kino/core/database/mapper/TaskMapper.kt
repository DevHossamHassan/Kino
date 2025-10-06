package com.letsgotoperfection.kino.core.database.mapper

import com.letsgotoperfection.kino.core.database.entity.TaskEntity
import com.letsgotoperfection.kino.core.database.entity.ChecklistItemEntity
import com.letsgotoperfection.kino.core.database.entity.LabelEntity
import com.letsgotoperfection.kino.core.database.entity.AttachmentEntity
import com.letsgotoperfection.kino.core.database.entity.NoteEntity
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Attachment
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.Priority
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun TaskEntity.toDomain(
    labels: List<Label> = emptyList(),
    checklist: List<ChecklistItem> = emptyList(),
    attachments: List<Attachment> = emptyList()
): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        section = TaskSection.values().find { 
            it.name.lowercase() == section.lowercase() 
        } ?: TaskSection.PERSONAL,
        column = TaskColumn.values().find { 
            it.name.lowercase() == column.lowercase() 
        } ?: TaskColumn.TODO_THIS_WEEK,
        priority = Priority.valueOf(priority.uppercase()),
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()),
        updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault()),
        dueDate = dueDate?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) },
        progress = progress,
        labels = labels,
        checklist = checklist,
        attachments = attachments
    )
}

// Note mappers will be added when notes feature is implemented

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        section = section.name.lowercase(),
        column = column.name.lowercase(),
        priority = priority.name.lowercase(),
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        dueDate = dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        progress = progress
    )
}

fun ChecklistItemEntity.toDomain(): ChecklistItem {
    return ChecklistItem(
        id = id,
        taskId = taskId,
        text = text,
        isCompleted = isCompleted,
        order = order,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault())
    )
}

fun ChecklistItem.toEntity(): ChecklistItemEntity {
    return ChecklistItemEntity(
        id = id,
        taskId = taskId,
        text = text,
        isCompleted = isCompleted,
        order = order,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

fun LabelEntity.toDomain(): Label {
    return Label(
        id = id,
        name = name,
        color = color
    )
}

fun Label.toEntity(): LabelEntity {
    return LabelEntity(
        id = id,
        name = name,
        color = color
    )
}

fun AttachmentEntity.toDomain(): Attachment {
    return Attachment(
        id = id,
        targetId = targetId,
        targetType = targetType,
        uri = uri,
        filename = filename,
        mimeType = mimeType,
        size = size,
        addedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(addedAt), ZoneId.systemDefault())
    )
}

fun Attachment.toEntity(): AttachmentEntity {
    return AttachmentEntity(
        id = id,
        targetId = targetId,
        targetType = targetType,
        uri = uri,
        filename = filename,
        mimeType = mimeType,
        size = size,
        addedAt = addedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

// Task detail specific mapping lives in the feature module to avoid a reverse dependency.
