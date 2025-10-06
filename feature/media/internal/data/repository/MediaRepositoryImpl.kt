package com.letsgotoperfection.kino.feature.media.internal.data.repository

import android.content.Context
import android.net.Uri
import com.letsgotoperfection.kino.feature.media.internal.data.local.MediaDao
import com.letsgotoperfection.kino.feature.media.internal.data.local.toDomain
import com.letsgotoperfection.kino.feature.media.internal.data.local.toEntity
import com.letsgotoperfection.kino.feature.media.internal.data.storage.MediaStoreManager
import com.letsgotoperfection.kino.feature.media.internal.data.storage.VideoMetadata
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaFilter
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Qualifier

/**
 * Implementation of MediaRepository
 * 
 * Handles media operations with proper error handling and coroutines
 * Uses Scoped Storage for file access and Room for metadata storage
 */
@Singleton
internal class MediaRepositoryImpl @Inject constructor(
    private val mediaDao: MediaDao,
    private val mediaStoreManager: MediaStoreManager,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MediaRepository {
    
    override fun getAllMedia(): Flow<List<Media>> {
        return mediaDao.getAllMedia()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }
    
    override fun getMediaBySource(
        sourceType: MediaSourceType,
        sourceId: String
    ): Flow<List<Media>> {
        return mediaDao.getMediaBySource(sourceType.name, sourceId)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }
    
    override fun getMediaByFilter(filter: MediaFilter): Flow<List<Media>> {
        return when {
            filter.type != null && filter.sourceType != null -> {
                // Filter by both type and source type
                mediaDao.getMediaBySourceType(filter.sourceType.name)
                    .map { entities ->
                        entities
                            .filter { entity ->
                                MediaType.fromMimeType(entity.mimeType) == filter.type
                            }
                            .map { it.toDomain() }
                    }
            }
            filter.type != null -> {
                // Filter by type only
                val mimeTypePattern = when (filter.type) {
                    MediaType.IMAGE -> "image/%"
                    MediaType.VIDEO -> "video/%"
                    MediaType.AUDIO -> "audio/%"
                    MediaType.DOCUMENT -> "application/%"
                    MediaType.OTHER -> "%"
                }
                mediaDao.getMediaByMimeType(mimeTypePattern)
                    .map { entities ->
                        entities.map { it.toDomain() }
                    }
            }
            filter.sourceType != null -> {
                // Filter by source type only
                mediaDao.getMediaBySourceType(filter.sourceType.name)
                    .map { entities ->
                        entities.map { it.toDomain() }
                    }
            }
            !filter.searchQuery.isNullOrBlank() -> {
                // Search by query
                mediaDao.searchMedia(filter.searchQuery)
                    .map { entities ->
                        entities.map { it.toDomain() }
                    }
            }
            else -> {
                // No filter, return all
                getAllMedia()
            }
        }
    }
    
    override suspend fun attachMedia(
        uri: Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media> = withContext(ioDispatcher) {
        runCatching {
            // Get file information
            val filename = mediaStoreManager.getFileName(uri).getOrThrow()
            val size = mediaStoreManager.getFileSize(uri).getOrThrow()
            val mimeType = context.contentResolver.getType(uri) 
                ?: "application/octet-stream"
            
            // Copy to app storage
            val appUri = mediaStoreManager.copyToAppStorage(uri, filename)
                .getOrThrow()
            
            val mediaId = UUID.randomUUID().toString()

            val mediaType = MediaType.fromMimeType(mimeType)

            var width: Int? = null
            var height: Int? = null
            var duration: Long? = null
            var thumbnailUri: Uri? = null

            when (mediaType) {
                MediaType.IMAGE -> {
                    val dimensions = mediaStoreManager.getImageDimensions(uri).getOrNull()
                    width = dimensions?.first
                    height = dimensions?.second
                }
                MediaType.VIDEO -> {
                    val metadata: VideoMetadata? = mediaStoreManager
                        .getVideoMetadata(appUri, "thumb_$mediaId")
                        .getOrNull()
                    width = metadata?.width
                    height = metadata?.height
                    duration = metadata?.duration
                    thumbnailUri = metadata?.thumbnailUri
                }
                MediaType.AUDIO -> {
                    duration = mediaStoreManager.getAudioDuration(appUri).getOrNull()
                }
                else -> Unit
            }

            // Create entity
            val entity = com.letsgotoperfection.kino.feature.media.internal.data.local.MediaEntity(
                id = mediaId,
                uri = appUri.toString(),
                filename = filename,
                mimeType = mimeType,
                size = size,
                dateAdded = System.currentTimeMillis(),
                dateModified = System.currentTimeMillis(),
                width = width,
                height = height,
                duration = duration,
                thumbnailUri = thumbnailUri?.toString(),
                sourceType = sourceType.name,
                sourceId = sourceId
            )
            
            // Save to database
            mediaDao.insert(entity)
            
            entity.toDomain()
        }
    }
    
    override suspend fun deleteMedia(mediaId: String): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                val entity = mediaDao.getById(mediaId)
                    ?: throw MediaNotFoundException("Media not found: $mediaId")
                
                // Delete file from app storage
                val uri = Uri.parse(entity.uri)
                val file = File(uri.path ?: throw IllegalStateException("Invalid URI"))
                
                if (file.exists()) {
                    val deleted = file.delete()
                    if (!deleted) {
                        throw MediaDeletionException("Failed to delete file from storage")
                    }
                }
                
                // Delete from database
                mediaDao.delete(entity)
            }
        }
    
    override suspend fun getMediaById(mediaId: String): Result<Media> = 
        withContext(ioDispatcher) {
            runCatching {
                mediaDao.getById(mediaId)?.toDomain()
                    ?: throw MediaNotFoundException("Media not found: $mediaId")
            }
        }
    
    override suspend fun updateMedia(media: Media): Result<Unit> = 
        withContext(ioDispatcher) {
            runCatching {
                mediaDao.update(media.toEntity())
            }
        }
    
    override suspend fun getMediaCount(): Int = withContext(ioDispatcher) {
        mediaDao.getMediaCount()
    }
    
    override suspend fun getMediaCount(
        sourceType: MediaSourceType,
        sourceId: String
    ): Int = withContext(ioDispatcher) {
        mediaDao.getMediaCountBySource(sourceType.name.lowercase(), sourceId)
    }
    
    override suspend fun getTotalSize(): Long = withContext(ioDispatcher) {
        mediaDao.getTotalSize() ?: 0L
    }
    
    override fun searchMedia(query: String): Flow<List<Media>> {
        return mediaDao.searchMedia(query)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }
}

/**
 * Exception thrown when media is not found
 */
internal class MediaNotFoundException(message: String) : Exception(message)

/**
 * Exception thrown when media deletion fails
 */
internal class MediaDeletionException(message: String) : Exception(message)

/**
 * Qualifier for I/O dispatcher
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher
