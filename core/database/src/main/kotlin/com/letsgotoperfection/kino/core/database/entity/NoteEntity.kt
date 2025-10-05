package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,  // JSON formatted text with styles
    val isPinned: Boolean = false,
    val attachmentCount: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
