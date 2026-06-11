package com.letsgotoperfection.kino.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.letsgotoperfection.kino.core.database.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for media metadata.
 */
@Dao
interface MediaDao {

    @Query("SELECT * FROM media ORDER BY dateAdded DESC")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media WHERE id = :mediaId")
    suspend fun getById(mediaId: String): MediaEntity?

    @Query("SELECT * FROM media WHERE sourceType = :sourceType AND sourceId = :sourceId ORDER BY dateAdded DESC")
    fun getMediaBySource(sourceType: String, sourceId: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media WHERE sourceType = :sourceType ORDER BY dateAdded DESC")
    fun getMediaBySourceType(sourceType: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media WHERE filename LIKE '%' || :query || '%' ORDER BY dateAdded DESC")
    fun searchMedia(query: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media WHERE mimeType LIKE :mimeTypePattern ORDER BY dateAdded DESC")
    fun getMediaByMimeType(mimeTypePattern: String): Flow<List<MediaEntity>>

    @Query("SELECT COUNT(*) FROM media")
    suspend fun getMediaCount(): Int

    @Query("SELECT COUNT(*) FROM media WHERE sourceType = :sourceType AND sourceId = :sourceId")
    suspend fun getMediaCountBySource(sourceType: String, sourceId: String): Int

    @Query("SELECT SUM(size) FROM media")
    suspend fun getTotalSize(): Long?

    @Upsert
    suspend fun upsert(media: MediaEntity)

    @Update
    suspend fun update(media: MediaEntity)

    @Delete
    suspend fun delete(media: MediaEntity)

    @Query("DELETE FROM media WHERE id = :mediaId")
    suspend fun deleteById(mediaId: String)

    @Query("DELETE FROM media WHERE sourceType = :sourceType AND sourceId = :sourceId")
    suspend fun deleteBySource(sourceType: String, sourceId: String)
}
