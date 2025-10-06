package com.letsgotoperfection.kino.feature.media.internal.domain.repository

import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaFilter
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for media operations
 * 
 * Provides a clean abstraction for media data access
 * following the Repository pattern
 */
internal interface MediaRepository {
    
    /**
     * Get all media items
     * Returns Flow for reactive updates
     */
    fun getAllMedia(): Flow<List<Media>>
    
    /**
     * Get media by source (task or note)
     * Returns Flow for reactive updates
     */
    fun getMediaBySource(
        sourceType: MediaSourceType,
        sourceId: String
    ): Flow<List<Media>>
    
    /**
     * Get media by filter criteria
     * Returns Flow for reactive updates
     */
    fun getMediaByFilter(filter: MediaFilter): Flow<List<Media>>
    
    /**
     * Attach media to a source
     * 
     * @param uri Content URI of the media file
     * @param sourceType Type of source (task, note)
     * @param sourceId ID of the source
     * @return Result containing the created Media or error
     */
    suspend fun attachMedia(
        uri: android.net.Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media>
    
    /**
     * Delete media by ID
     * 
     * @param mediaId ID of the media to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteMedia(mediaId: String): Result<Unit>
    
    /**
     * Get media by ID
     * 
     * @param mediaId ID of the media
     * @return Result containing the Media or error
     */
    suspend fun getMediaById(mediaId: String): Result<Media>
    
    /**
     * Update media metadata
     * 
     * @param media Updated media object
     * @return Result indicating success or failure
     */
    suspend fun updateMedia(media: Media): Result<Unit>
    
    /**
     * Get total count of media items
     * 
     * @return Total count
     */
    suspend fun getMediaCount(): Int
    
    /**
     * Get media count for a specific source
     * 
     * @param sourceType Type of source
     * @param sourceId ID of the source
     * @return Number of media items attached to the source
     */
    suspend fun getMediaCount(
        sourceType: MediaSourceType,
        sourceId: String
    ): Int
    
    /**
     * Get total size of all media in bytes
     * 
     * @return Total size in bytes
     */
    suspend fun getTotalSize(): Long
    
    /**
     * Search media by filename
     * 
     * @param query Search query
     * @return Flow of matching media items
     */
    fun searchMedia(query: String): Flow<List<Media>>
}
