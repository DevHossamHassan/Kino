package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Media entity storing metadata about media files attached to tasks or notes.
 *
 * The actual file content lives in app-specific storage and is accessed via [uri].
 */
@Entity(
    tableName = "media",
    indices = [
        Index(value = ["sourceType", "sourceId"], name = "idx_media_source"),
        Index(value = ["dateAdded"], name = "idx_media_date_added"),
        Index(value = ["mimeType"], name = "idx_media_mime_type")
    ]
)
data class MediaEntity(
    @PrimaryKey val id: String,
    val uri: String,
    val filename: String,
    val mimeType: String,
    val size: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val width: Int?,
    val height: Int?,
    val duration: Long?,
    val thumbnailUri: String?,
    val sourceType: String,
    val sourceId: String
)
