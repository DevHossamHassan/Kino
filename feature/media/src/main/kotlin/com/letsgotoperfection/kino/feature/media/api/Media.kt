package com.letsgotoperfection.kino.feature.media.api

import android.net.Uri
import java.time.LocalDateTime

/**
 * Public domain model representing a media item (image, video, document, etc.)
 * 
 * @param id Unique identifier for the media item
 * @param uri Content URI from MediaStore for accessing the file
 * @param filename Original filename of the media
 * @param mimeType MIME type of the media file
 * @param size Size of the file in bytes
 * @param dateAdded When the media was added to the system
 * @param dateModified When the media was last modified
 * @param width Width in pixels (for images/videos)
 * @param height Height in pixels (for images/videos)
 * @param duration Duration in milliseconds (for videos/audio)
 * @param thumbnailUri URI for thumbnail (for videos/documents)
 * @param sourceType Type of source that owns this media (task, note)
 * @param sourceId ID of the source that owns this media
 */
data class Media(
    val id: String,
    val uri: Uri,
    val filename: String,
    val mimeType: String,
    val size: Long,
    val dateAdded: LocalDateTime,
    val dateModified: LocalDateTime,
    val width: Int?,
    val height: Int?,
    val duration: Long?,
    val thumbnailUri: Uri?,
    val sourceType: MediaSourceType,
    val sourceId: String
)
