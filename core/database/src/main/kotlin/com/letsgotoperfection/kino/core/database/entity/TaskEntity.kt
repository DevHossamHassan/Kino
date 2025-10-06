package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Task entity with performance-optimized indices.
 * 
 * PERFORMANCE IMPROVEMENTS:
 * - Index on section for fast filtering by Personal/Work/Family
 * - Index on column for Kanban board queries
 * - Index on updatedAt for sorting recent tasks
 * - Index on dueDate for filtering upcoming/overdue tasks
 * - Composite index on (section, column) for combined filters
 * - Index on recurringTaskId for finding task instances
 */
@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["section"], name = "idx_task_section"),
        Index(value = ["column"], name = "idx_task_column"),
        Index(value = ["updatedAt"], name = "idx_task_updated_at"),
        Index(value = ["dueDate"], name = "idx_task_due_date"),
        Index(value = ["section", "column"], name = "idx_task_section_column"),
        Index(value = ["recurringTaskId"], name = "idx_task_recurring_task_id"),
        Index(value = ["scheduledDate"], name = "idx_task_scheduled_date"),
        Index(value = ["column", "orderPosition"], name = "idx_task_column_order")
    ]
)
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val section: String,  // personal, work, family
    val column: String,   // backlog, todo_this_week, in_progress, pending, done
    val priority: String, // high, medium, low
    val createdAt: Long,
    val updatedAt: Long,
    val dueDate: Long?,
    val progress: Int = 0,
    val orderPosition: Int = 0,  // Position within column for drag-to-reorder
    val recurringTaskId: String? = null,  // Link to parent recurring task
    val scheduledDate: Long? = null  // When this instance was scheduled (epoch day)
)
