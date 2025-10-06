package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Note entity with performance-optimized indices.
 * 
 * PERFORMANCE IMPROVEMENTS:
 * - Index on isPinned for quickly filtering pinned notes
 * - Index on updatedAt for sorting by recent modifications
 * - Composite index on (isPinned, updatedAt) for common query pattern
 */
@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["isPinned"], name = "idx_note_is_pinned"),
        Index(value = ["updatedAt"], name = "idx_note_updated_at"),
        Index(value = ["isPinned", "updatedAt"], name = "idx_note_pinned_updated")
    ]
)
data class NoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,  // JSON formatted text with styles
    val isPinned: Boolean = false,
    val attachmentCount: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
