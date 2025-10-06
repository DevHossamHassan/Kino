package com.letsgotoperfection.kino.feature.media.internal.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Media operations
 * 
 * Provides methods for CRUD operations on media entities
 * with reactive Flow support for real-time updates
 */
@Dao
internal interface MediaDao {
    
    /**
     * Get all media items
     * Returns Flow for reactive updates
     */
    @Query("SELECT * FROM media ORDER BY dateAdded DESC")
    fun getAllMedia(): Flow<List<MediaEntity>>
    
    /**
     * Get media by ID
     */
    @Query("SELECT * FROM media WHERE id = :mediaId")
    suspend fun getById(mediaId: String): MediaEntity?
    
    /**
     * Get media by source type and source ID
     * Returns Flow for reactive updates
     */
    @Query("SELECT * FROM media WHERE sourceType = :sourceType AND sourceId = :sourceId ORDER BY dateAdded DESC")
    fun getMediaBySource(sourceType: String, sourceId: String): Flow<List<MediaEntity>>
    
    /**
     * Get media by source type only
     * Returns Flow for reactive updates
     */
    @Query("SELECT * FROM media WHERE sourceType = :sourceType ORDER BY dateAdded DESC")
    fun getMediaBySourceType(sourceType: String): Flow<List<MediaEntity>>
    
    /**
     * Search media by filename
     * Returns Flow for reactive updates
     */
    @Query("SELECT * FROM media WHERE filename LIKE '%' || :query || '%' ORDER BY dateAdded DESC")
    fun searchMedia(query: String): Flow<List<MediaEntity>>
    
    /**
     * Get media by MIME type
     * Returns Flow for reactive updates
     */
    @Query("SELECT * FROM media WHERE mimeType LIKE :mimeTypePattern ORDER BY dateAdded DESC")
    fun getMediaByMimeType(mimeTypePattern: String): Flow<List<MediaEntity>>
    
    /**
     * Get total count of media items
     */
    @Query("SELECT COUNT(*) FROM media")
    suspend fun getMediaCount(): Int
    
    /**
     * Get media count for a specific source
     */
    @Query("SELECT COUNT(*) FROM media WHERE sourceType = :sourceType AND sourceId = :sourceId")
    suspend fun getMediaCountBySource(sourceType: String, sourceId: String): Int
    
    /**
     * Get total size of all media in bytes
     */
    @Query("SELECT SUM(size) FROM media")
    suspend fun getTotalSize(): Long?
    
    /**
     * Insert a new media item
     * Uses REPLACE strategy to handle conflicts
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: MediaEntity)
    
    /**
     * Insert multiple media items
     * Uses REPLACE strategy to handle conflicts
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(media: List<MediaEntity>)
    
    /**
     * Update an existing media item
     */
    @Update
    suspend fun update(media: MediaEntity)
    
    /**
     * Delete a media item
     */
    @Delete
    suspend fun delete(media: MediaEntity)
    
    /**
     * Delete media by ID
     */
    @Query("DELETE FROM media WHERE id = :mediaId")
    suspend fun deleteById(mediaId: String)
    
    /**
     * Delete all media for a specific source
     */
    @Query("DELETE FROM media WHERE sourceType = :sourceType AND sourceId = :sourceId")
    suspend fun deleteBySource(sourceType: String, sourceId: String)
    
    /**
     * Delete all media items
     */
    @Query("DELETE FROM media")
    suspend fun deleteAll()
    
    /**
     * Get media items with pagination
     * Useful for large datasets
     */
    @Query("SELECT * FROM media ORDER BY dateAdded DESC LIMIT :limit OFFSET :offset")
    suspend fun getMediaPaged(limit: Int, offset: Int): List<MediaEntity>
    
    /**
     * Get media items created after a specific timestamp
     * Useful for incremental updates
     */
    @Query("SELECT * FROM media WHERE dateAdded > :timestamp ORDER BY dateAdded DESC")
    fun getMediaAfter(timestamp: Long): Flow<List<MediaEntity>>
}
