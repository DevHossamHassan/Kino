package com.letsgotoperfection.kino.feature.media.internal.domain.model

import android.net.Uri
import java.time.LocalDateTime

/**
 * Domain model representing a media item (image, video, document, etc.)
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
internal data class Media(
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

/**
 * Enum representing the type of media file
 */
internal enum class MediaType {
    IMAGE,
    VIDEO,
    DOCUMENT,
    AUDIO,
    OTHER;
    
    companion object {
        /**
         * Determine media type from MIME type
         */
        fun fromMimeType(mimeType: String): MediaType {
            return when {
                mimeType.startsWith("image/") -> IMAGE
                mimeType.startsWith("video/") -> VIDEO
                mimeType.startsWith("audio/") -> AUDIO
                mimeType == "application/pdf" ||
                mimeType.startsWith("application/vnd") ||
                mimeType.startsWith("text/") -> DOCUMENT
                else -> OTHER
            }
        }
    }
}

/**
 * Enum representing the source type that owns the media
 */
internal enum class MediaSourceType {
    TASK,
    NOTE
}

/**
 * Filter criteria for media queries
 */
internal data class MediaFilter(
    val type: MediaType? = null,
    val sourceType: MediaSourceType? = null,
    val searchQuery: String? = null
)
