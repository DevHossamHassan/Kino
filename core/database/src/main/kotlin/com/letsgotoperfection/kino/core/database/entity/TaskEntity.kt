package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
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
    val recurringTaskId: String? = null,  // Link to parent recurring task
    val scheduledDate: Long? = null  // When this instance was scheduled (epoch day)
)
