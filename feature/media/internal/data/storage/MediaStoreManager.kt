package com.letsgotoperfection.kino.feature.media.internal.data.storage

import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages media access using Scoped Storage (Android 10+) and MediaStore API
 * 
 * This class handles:
 * - Querying media from MediaStore using ContentResolver
 * - Copying media to app-specific storage
 * - Deleting media with proper permission handling
 * - Getting file metadata (size, name, dimensions)
 */
@Singleton
internal class MediaStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val contentResolver = context.contentResolver
    
    /**
     * Query all media from MediaStore
     * Uses coroutines for I/O operations
     */
    suspend fun queryAllMedia(): Result<List<MediaStoreItem>> = 
        withContext(Dispatchers.IO) {
            runCatching {
                val mediaItems = mutableListOf<MediaStoreItem>()
                
                // Query images
                mediaItems.addAll(queryImages())
                
                // Query videos
                mediaItems.addAll(queryVideos())
                
                mediaItems.sortedByDescending { it.dateAdded }
            }
        }
    
    /**
     * Query images from MediaStore
     */
    private suspend fun queryImages(): List<MediaStoreItem> = 
        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
            )
            
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            
            val items = mutableListOf<MediaStoreItem>()
            
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    
                    items.add(
                        MediaStoreItem(
                            id = id,
                            uri = contentUri,
                            filename = cursor.getString(nameColumn),
                            size = cursor.getLong(sizeColumn),
                            mimeType = cursor.getString(mimeTypeColumn),
                            dateAdded = cursor.getLong(dateAddedColumn),
                            dateModified = cursor.getLong(dateModifiedColumn),
                            width = cursor.getInt(widthColumn),
                            height = cursor.getInt(heightColumn),
                            duration = null,
                            type = MediaType.IMAGE
                        )
                    )
                }
            }
            
            items
        }
    
    /**
     * Query videos from MediaStore
     */
    private suspend fun queryVideos(): List<MediaStoreItem> = 
        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION
            )
            
            val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
            
            val items = mutableListOf<MediaStoreItem>()
            
            contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    
                    items.add(
                        MediaStoreItem(
                            id = id,
                            uri = contentUri,
                            filename = cursor.getString(nameColumn),
                            size = cursor.getLong(sizeColumn),
                            mimeType = cursor.getString(mimeTypeColumn),
                            dateAdded = cursor.getLong(dateAddedColumn),
                            dateModified = cursor.getLong(dateModifiedColumn),
                            width = cursor.getInt(widthColumn),
                            height = cursor.getInt(heightColumn),
                            duration = cursor.getLong(durationColumn),
                            type = MediaType.VIDEO
                        )
                    )
                }
            }
            
            items
        }
    
    /**
     * Copy media to app-specific directory
     * Returns URI of copied file
     */
    suspend fun copyToAppStorage(sourceUri: Uri, filename: String): Result<Uri> =
        withContext(Dispatchers.IO) {
            runCatching {
                // Get app-specific directory (doesn't require permissions)
                val mediaDir = File(context.filesDir, "media").apply {
                    if (!exists()) mkdirs()
                }
                
                val destFile = File(mediaDir, filename)
                
                // Copy file using streams
                contentResolver.openInputStream(sourceUri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                } ?: throw MediaCopyException("Failed to open input stream")
                
                // Return content URI using FileProvider
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    destFile
                )
            }
        }
    
    /**
     * Delete media from MediaStore
     * Requires user permission on Android 10+
     */
    suspend fun deleteMedia(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+: Request user permission to delete
                val pendingIntent = MediaStore.createDeleteRequest(
                    contentResolver,
                    listOf(uri)
                )
                
                throw DeleteRequiresUserPermissionException(pendingIntent)
            } else {
                // Below Android 11: Direct deletion (if we have permission)
                val deleted = contentResolver.delete(uri, null, null)
                if (deleted == 0) {
                    throw MediaDeletionException("Failed to delete media")
                }
            }
        }
    }
    
    /**
     * Get file size from URI
     */
    suspend fun getFileSize(uri: Uri): Result<Long> = withContext(Dispatchers.IO) {
        runCatching {
            contentResolver.query(
                uri,
                arrayOf(OpenableColumns.SIZE),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.getLong(sizeIndex)
                } else {
                    0L
                }
            } ?: 0L
        }
    }
    
    /**
     * Get filename from URI
     */
    suspend fun getFileName(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.getString(nameIndex)
                } else {
                    "unknown"
                }
            } ?: "unknown"
        }
    }
    
    /**
     * Get image dimensions from URI
     */
    suspend fun getImageDimensions(uri: Uri): Result<Pair<Int, Int>> = withContext(Dispatchers.IO) {
        runCatching {
            contentResolver.openInputStream(uri)?.use { input ->
                val options = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                android.graphics.BitmapFactory.decodeStream(input, null, options)
                options.outWidth to options.outHeight
            } ?: throw MediaDimensionException("Failed to get image dimensions")
        }
    }

    /**
     * Extract metadata for a video file including dimensions, duration and a generated thumbnail.
     */
    suspend fun getVideoMetadata(uri: Uri, thumbnailPrefix: String): Result<VideoMetadata> =
        withContext(Dispatchers.IO) {
            runCatching {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, uri)

                    val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                        ?.toIntOrNull()
                    val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                        ?.toIntOrNull()
                    val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull()

                    val frame = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST)
                    val thumbnailUri = frame?.let {
                        saveBitmapAsThumbnail(it, "$thumbnailPrefix.jpg")
                    }

                    VideoMetadata(
                        width = width,
                        height = height,
                        duration = duration,
                        thumbnailUri = thumbnailUri
                    )
                } finally {
                    retriever.release()
                }
            }
        }

    /**
     * Extract duration for an audio file.
     */
    suspend fun getAudioDuration(uri: Uri): Result<Long?> = withContext(Dispatchers.IO) {
        runCatching {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()
            } finally {
                retriever.release()
            }
        }
    }

    private fun saveBitmapAsThumbnail(bitmap: Bitmap, filename: String): Uri {
        val thumbnailsDir = File(context.filesDir, "media_thumbnails").apply {
            if (!exists()) mkdirs()
        }

        val thumbnailFile = File(thumbnailsDir, filename)
        FileOutputStream(thumbnailFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            thumbnailFile
        )
    }
}

internal data class VideoMetadata(
    val width: Int?,
    val height: Int?,
    val duration: Long?,
    val thumbnailUri: Uri?
)

/**
 * Data class representing a media item from MediaStore
 */
internal data class MediaStoreItem(
    val id: Long,
    val uri: Uri,
    val filename: String,
    val size: Long,
    val mimeType: String,
    val dateAdded: Long,
    val dateModified: Long,
    val width: Int?,
    val height: Int?,
    val duration: Long?,
    val type: MediaType
)

/**
 * Exception thrown when user permission is required to delete media
 */
internal class DeleteRequiresUserPermissionException(
    val pendingIntent: PendingIntent
) : Exception("User permission required to delete media")

/**
 * Exception thrown when media deletion fails
 */
internal class MediaDeletionException(message: String) : Exception(message)

/**
 * Exception thrown when media copy fails
 */
internal class MediaCopyException(message: String) : Exception(message)

/**
 * Exception thrown when getting image dimensions fails
 */
internal class MediaDimensionException(message: String) : Exception(message)
