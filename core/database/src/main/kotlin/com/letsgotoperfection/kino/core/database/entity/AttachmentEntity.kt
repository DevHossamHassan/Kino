package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val targetId: String,  // taskId or noteId
    val targetType: String, // "task" or "note"
    val uri: String,
    val filename: String,
    val mimeType: String,
    val size: Long,
    val addedAt: Long
)





