package com.letsgotoperfection.kino.feature.media.internal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Room entity for storing media information
 * 
 * This entity stores metadata about media files that have been attached
 * to tasks or notes. The actual file content is stored in app-specific
 * storage and accessed via the URI.
 */
@Entity(tableName = "media")
internal data class MediaEntity(
    @PrimaryKey
    val id: String,
    val uri: String,                    // Content URI as string
    val filename: String,
    val mimeType: String,
    val size: Long,                     // Size in bytes
    val dateAdded: Long,                // Timestamp when added
    val dateModified: Long,             // Timestamp when last modified
    val width: Int?,                    // Width in pixels (for images/videos)
    val height: Int?,                   // Height in pixels (for images/videos)
    val duration: Long?,                // Duration in milliseconds (for videos/audio)
    val thumbnailUri: String?,          // Thumbnail URI as string
    val sourceType: String,             // Source type (TASK, NOTE)
    val sourceId: String                // ID of the source that owns this media
)

/**
 * Convert MediaEntity to domain model
 */
internal fun MediaEntity.toDomain(): Media {
    return Media(
        id = id,
        uri = android.net.Uri.parse(uri),
        filename = filename,
        mimeType = mimeType,
        size = size,
        dateAdded = LocalDateTime.ofEpochSecond(dateAdded / 1000, 0, ZoneOffset.UTC),
        dateModified = LocalDateTime.ofEpochSecond(dateModified / 1000, 0, ZoneOffset.UTC),
        width = width,
        height = height,
        duration = duration,
        thumbnailUri = thumbnailUri?.let { android.net.Uri.parse(it) },
        sourceType = MediaSourceType.valueOf(sourceType),
        sourceId = sourceId
    )
}

/**
 * Convert domain model to MediaEntity
 */
internal fun Media.toEntity(): MediaEntity {
    return MediaEntity(
        id = id,
        uri = uri.toString(),
        filename = filename,
        mimeType = mimeType,
        size = size,
        dateAdded = dateAdded.toEpochSecond(ZoneOffset.UTC) * 1000,
        dateModified = dateModified.toEpochSecond(ZoneOffset.UTC) * 1000,
        width = width,
        height = height,
        duration = duration,
        thumbnailUri = thumbnailUri?.toString(),
        sourceType = sourceType.name,
        sourceId = sourceId
    )
}
