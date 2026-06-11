package com.letsgotoperfection.kino.core.model

import java.time.LocalDateTime
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val section: TaskSection,
    val column: TaskColumn,
    val priority: Priority,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val dueDate: LocalDateTime?,
    val progress: Int = 0,
    val orderPosition: Int = 0,
    val labels: List<Label> = emptyList(),
    val checklist: List<ChecklistItem> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    /** ID of the recurring task template this task was generated from, if any. */
    val recurringTaskId: String? = null,
    /** The occurrence date this instance was generated for, if recurring. */
    val scheduledDate: java.time.LocalDate? = null
) {
    val attachmentCount: Int get() = attachments.size
    val checklistTotal: Int get() = checklist.size
    val checklistCompleted: Int get() = checklist.count { it.isCompleted }
    val dueDateFormatted: String? get() = dueDate?.let { 
        it.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    }
    val priorityColor: String get() = priority.colorHex
}

enum class TaskSection(val displayName: String) {
    PERSONAL("Personal"),
    WORK("Work"),
    FAMILY("Family")
}

enum class TaskColumn(val displayName: String) {
    BACKLOG("Backlog"),
    TODO_THIS_WEEK("To Do This Week"),
    IN_PROGRESS("In Progress"),
    PENDING("Pending"),
    DONE("Done")
}

enum class Priority(val displayName: String, val colorHex: String) {
    HIGH("High", "#E53E3E"),
    MEDIUM("Medium", "#D69E2E"),
    LOW("Low", "#38A169")
}

data class ChecklistItem(
    val id: String,
    val taskId: String,
    val text: String,
    val isCompleted: Boolean,
    val order: Int,
    val createdAt: LocalDateTime
)

data class Attachment(
    val id: String,
    val targetId: String,
    val targetType: String,
    val uri: String,
    val filename: String,
    val mimeType: String,
    val size: Long,
    val addedAt: LocalDateTime
)

data class Label(
    val id: String,
    val name: String,
    val color: String
)