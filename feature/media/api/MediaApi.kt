package com.letsgotoperfection.kino.feature.media.api

import android.net.Uri
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType

/**
 * Public API for Media feature
 * 
 * This interface allows other feature modules to interact with media
 * without depending on the internal implementation
 */
interface MediaApi {
    
    /**
     * Get media by ID
     * 
     * @param mediaId The unique media identifier
     * @return Result containing the Media or an error
     */
    suspend fun getMedia(mediaId: String): Result<Media>
    
    /**
     * Attach media to a source
     * 
     * @param uri Content URI of the media file
     * @param sourceType Type of source (task, note)
     * @param sourceId ID of the source
     * @return Result containing the created Media or an error
     */
    suspend fun attachMedia(
        uri: Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media>
    
    /**
     * Attach multiple media files to a source
     * 
     * @param uris List of content URIs
     * @param sourceType Type of source (task, note)
     * @param sourceId ID of the source
     * @return List of Results for each media attachment
     */
    suspend fun attachMedia(
        uris: List<Uri>,
        sourceType: MediaSourceType,
        sourceId: String
    ): List<Result<Media>>
    
    /**
     * Delete media by ID
     * 
     * @param mediaId ID of the media to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteMedia(mediaId: String): Result<Unit>
    
    /**
     * Get media count for a source
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
     * Check if media permissions are granted
     * 
     * @return True if all required permissions are granted
     */
    fun hasMediaPermissions(): Boolean
}
