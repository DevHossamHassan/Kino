package com.letsgotoperfection.kino.feature.taskdetail.internal.data.mapper

import com.letsgotoperfection.kino.core.database.entity.TaskEntity
import com.letsgotoperfection.kino.core.model.Attachment
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model.TaskDetail
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Convert a [TaskEntity] into the richer [TaskDetail] model used by the
 * feature. This lives in the feature module to avoid introducing a dependency
 * from the shared database layer back to feature-specific domain classes.
 */
internal fun TaskEntity.toTaskDetail(
    checklist: List<ChecklistItem>,
    attachments: List<Attachment>,
    labels: List<Label>
): TaskDetail {
    return TaskDetail(
        id = id,
        title = title,
        description = description,
        section = TaskSection.valueOf(section.uppercase()),
        column = TaskColumn.valueOf(column.uppercase()),
        priority = Priority.valueOf(priority.uppercase()),
        progress = progress,
        labels = labels,
        dueDate = dueDate?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) },
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()),
        updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault()),
        checklist = checklist,
        attachments = attachments
    )
}
