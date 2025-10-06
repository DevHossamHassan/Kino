package com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model

import com.letsgotoperfection.kino.core.model.Attachment
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import java.time.LocalDateTime

/**
 * Domain model representing detailed task information with all related data.
 * This is used specifically for the task detail screen.
 */
data class TaskDetail(
    val id: String,
    val title: String,
    val description: String,
    val section: TaskSection,
    val column: TaskColumn,
    val priority: Priority,
    val progress: Int,
    val labels: List<Label>,
    val dueDate: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val checklist: List<ChecklistItem>,
    val attachments: List<Attachment>
) {
    val attachmentCount: Int get() = attachments.size
    val checklistTotal: Int get() = checklist.size
    val checklistCompleted: Int get() = checklist.count { it.isCompleted }
    val isOverdue: Boolean get() = dueDate?.isBefore(LocalDateTime.now()) == true
    val dueDateFormatted: String? get() = dueDate?.let { 
        it.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    }
    val priorityColor: String get() = priority.colorHex
}
