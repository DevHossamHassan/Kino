package com.letsgotoperfection.kino.feature.media.internal.domain.repository

import android.net.Uri
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaFilter
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for media data operations.
 */
internal interface MediaRepository {

    /** Observe all media items, newest first. */
    fun getAllMedia(): Flow<List<Media>>

    /** Observe media attached to a specific source (task or note). */
    fun getMediaBySource(sourceType: MediaSourceType, sourceId: String): Flow<List<Media>>

    /** Observe media matching the given [filter]. */
    fun getMediaByFilter(filter: MediaFilter): Flow<List<Media>>

    /** Observe media whose filename matches the free-text [query]. */
    fun searchMedia(query: String): Flow<List<Media>>

    /**
     * Copy the file behind [uri] into app storage, extract metadata and persist it.
     */
    suspend fun attachMedia(
        uri: Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media>

    /** Delete a media item and its backing file. */
    suspend fun deleteMedia(mediaId: String): Result<Unit>

    /** Get a media item by id. */
    suspend fun getMediaById(mediaId: String): Result<Media>

    /** Count of media items attached to a specific source. */
    suspend fun getMediaCount(sourceType: MediaSourceType, sourceId: String): Int

    /** Total size of all stored media in bytes. */
    suspend fun getTotalSize(): Long
}
