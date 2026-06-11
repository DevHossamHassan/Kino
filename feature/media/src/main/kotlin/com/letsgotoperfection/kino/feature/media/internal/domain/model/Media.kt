package com.letsgotoperfection.kino.feature.media.internal.domain.model

import android.net.Uri
import java.time.LocalDateTime

/**
 * Domain model representing a media item (image, video, document, etc.)
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
 * Type of a media file, derived from its MIME type.
 */
internal enum class MediaType {
    IMAGE,
    VIDEO,
    DOCUMENT,
    AUDIO,
    OTHER;

    companion object {
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
 * Type of the source that owns a media item.
 */
internal enum class MediaSourceType {
    TASK,
    NOTE
}

/**
 * Filter criteria for media queries.
 */
internal data class MediaFilter(
    val type: MediaType? = null,
    val sourceType: MediaSourceType? = null,
    val searchQuery: String? = null
)
