package com.letsgotoperfection.kino.feature.media.internal.data.repository

import android.content.Context
import android.net.Uri
import com.letsgotoperfection.kino.core.common.di.IoDispatcher
import com.letsgotoperfection.kino.core.database.dao.MediaDao
import com.letsgotoperfection.kino.core.database.entity.MediaEntity
import com.letsgotoperfection.kino.feature.media.internal.data.mapper.toDomain
import com.letsgotoperfection.kino.feature.media.internal.data.storage.MediaStoreManager
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaFilter
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed implementation of [MediaRepository].
 *
 * Media files are copied into app-specific storage on attach so no
 * runtime storage permissions are needed to read them back later.
 */
@Singleton
internal class MediaRepositoryImpl @Inject constructor(
    private val mediaDao: MediaDao,
    private val mediaStoreManager: MediaStoreManager,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MediaRepository {

    override fun getAllMedia(): Flow<List<Media>> {
        return mediaDao.getAllMedia().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getMediaBySource(
        sourceType: MediaSourceType,
        sourceId: String
    ): Flow<List<Media>> {
        return mediaDao.getMediaBySource(sourceType.name, sourceId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getMediaByFilter(filter: MediaFilter): Flow<List<Media>> {
        return when {
            filter.type != null && filter.sourceType != null -> {
                mediaDao.getMediaBySourceType(filter.sourceType.name)
                    .map { entities ->
                        entities
                            .filter { MediaType.fromMimeType(it.mimeType) == filter.type }
                            .map { it.toDomain() }
                    }
            }

            filter.type != null -> {
                mediaDao.getAllMedia()
                    .map { entities ->
                        entities
                            .filter { MediaType.fromMimeType(it.mimeType) == filter.type }
                            .map { it.toDomain() }
                    }
            }

            filter.sourceType != null -> {
                mediaDao.getMediaBySourceType(filter.sourceType.name)
                    .map { entities -> entities.map { it.toDomain() } }
            }

            !filter.searchQuery.isNullOrBlank() -> searchMedia(filter.searchQuery)

            else -> getAllMedia()
        }
    }

    override fun searchMedia(query: String): Flow<List<Media>> {
        return mediaDao.searchMedia(query).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun attachMedia(
        uri: Uri,
        sourceType: MediaSourceType,
        sourceId: String
    ): Result<Media> = withContext(ioDispatcher) {
        runCatching {
            val filename = mediaStoreManager.getFileName(uri).getOrThrow()
            val size = mediaStoreManager.getFileSize(uri).getOrThrow()
            val mimeType = context.contentResolver.getType(uri) ?: DEFAULT_MIME_TYPE

            val appUri = mediaStoreManager.copyToAppStorage(uri, filename).getOrThrow()

            val mediaId = UUID.randomUUID().toString()
            var width: Int? = null
            var height: Int? = null
            var duration: Long? = null
            var thumbnailUri: Uri? = null

            when (MediaType.fromMimeType(mimeType)) {
                MediaType.IMAGE -> {
                    val dimensions = mediaStoreManager.getImageDimensions(appUri).getOrNull()
                    width = dimensions?.first
                    height = dimensions?.second
                }

                MediaType.VIDEO -> {
                    val metadata = mediaStoreManager
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

            val now = System.currentTimeMillis()
            val entity = MediaEntity(
                id = mediaId,
                uri = appUri.toString(),
                filename = filename,
                mimeType = mimeType,
                size = size,
                dateAdded = now,
                dateModified = now,
                width = width,
                height = height,
                duration = duration,
                thumbnailUri = thumbnailUri?.toString(),
                sourceType = sourceType.name,
                sourceId = sourceId
            )

            mediaDao.upsert(entity)
            entity.toDomain()
        }
    }

    override suspend fun deleteMedia(mediaId: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val entity = mediaDao.getById(mediaId)
                ?: throw MediaNotFoundException("Media not found: $mediaId")

            mediaStoreManager.deleteFromAppStorage(Uri.parse(entity.uri)).getOrThrow()
            entity.thumbnailUri?.let { thumbnail ->
                // Best effort: a stale thumbnail must not block the deletion.
                mediaStoreManager.deleteFromAppStorage(Uri.parse(thumbnail))
            }

            mediaDao.delete(entity)
        }
    }

    override suspend fun getMediaById(mediaId: String): Result<Media> = withContext(ioDispatcher) {
        runCatching {
            mediaDao.getById(mediaId)?.toDomain()
                ?: throw MediaNotFoundException("Media not found: $mediaId")
        }
    }

    override suspend fun getMediaCount(
        sourceType: MediaSourceType,
        sourceId: String
    ): Int = withContext(ioDispatcher) {
        mediaDao.getMediaCountBySource(sourceType.name, sourceId)
    }

    override suspend fun getTotalSize(): Long = withContext(ioDispatcher) {
        mediaDao.getTotalSize() ?: 0L
    }

    private companion object {
        const val DEFAULT_MIME_TYPE = "application/octet-stream"
    }
}

/** Thrown when a media item cannot be found by id. */
internal class MediaNotFoundException(message: String) : Exception(message)
