package com.letsgotoperfection.kino.feature.media.internal.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.letsgotoperfection.kino.core.common.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val MEDIA_DIR = "media"
private const val THUMBNAILS_DIR = "media_thumbnails"
private const val THUMBNAIL_QUALITY = 85

/**
 * Manages media file storage in app-specific storage.
 *
 * Handles:
 * - Copying picked media into app storage (no storage permissions required)
 * - Extracting file metadata (size, name, dimensions, duration, thumbnails)
 * - Deleting files previously copied into app storage
 */
@Singleton
internal class MediaStoreManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val contentResolver = context.contentResolver
    private val authority: String get() = "${context.packageName}.fileprovider"

    /**
     * Copy the file behind [sourceUri] into the app-specific media directory.
     *
     * @return a FileProvider content URI pointing at the copy
     */
    suspend fun copyToAppStorage(sourceUri: Uri, filename: String): Result<Uri> =
        withContext(ioDispatcher) {
            runCatching {
                val mediaDir = File(context.filesDir, MEDIA_DIR).apply { mkdirs() }
                val destFile = uniqueFile(mediaDir, filename)

                contentResolver.openInputStream(sourceUri)?.use { input ->
                    FileOutputStream(destFile).use { output -> input.copyTo(output) }
                } ?: throw MediaCopyException("Failed to open input stream for $sourceUri")

                FileProvider.getUriForFile(context, authority, destFile)
            }
        }

    /**
     * Delete a file previously copied into app storage (media or thumbnail),
     * identified by its FileProvider content URI.
     */
    suspend fun deleteFromAppStorage(uri: Uri): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val file = resolveAppStorageFile(uri)
                ?: throw MediaDeletionException("Unrecognized app storage URI: $uri")
            if (file.exists() && !file.delete()) {
                throw MediaDeletionException("Failed to delete file: ${file.name}")
            }
        }
    }

    /** Get file size in bytes for a content URI. */
    suspend fun getFileSize(uri: Uri): Result<Long> = withContext(ioDispatcher) {
        runCatching {
            contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
                    } else {
                        0L
                    }
                } ?: 0L
        }
    }

    /** Get display name for a content URI. */
    suspend fun getFileName(uri: Uri): Result<String> = withContext(ioDispatcher) {
        runCatching {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getString(
                            cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                        )
                    } else {
                        null
                    }
                } ?: uri.lastPathSegment ?: DEFAULT_FILENAME
        }
    }

    /** Decode image bounds without loading the full bitmap. */
    suspend fun getImageDimensions(uri: Uri): Result<Pair<Int, Int>> = withContext(ioDispatcher) {
        runCatching {
            contentResolver.openInputStream(uri)?.use { input ->
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(input, null, options)
                options.outWidth to options.outHeight
            } ?: throw MediaMetadataException("Failed to read image dimensions for $uri")
        }
    }

    /**
     * Extract video metadata (dimensions, duration) and generate a thumbnail
     * stored in app storage.
     */
    suspend fun getVideoMetadata(uri: Uri, thumbnailPrefix: String): Result<VideoMetadata> =
        withContext(ioDispatcher) {
            runCatching {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, uri)

                    val width = retriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                        ?.toIntOrNull()
                    val height = retriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                        ?.toIntOrNull()
                    val duration = retriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
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

    /** Extract duration in milliseconds for an audio file. */
    suspend fun getAudioDuration(uri: Uri): Result<Long?> = withContext(ioDispatcher) {
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
        val thumbnailsDir = File(context.filesDir, THUMBNAILS_DIR).apply { mkdirs() }
        val thumbnailFile = File(thumbnailsDir, filename)
        FileOutputStream(thumbnailFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, THUMBNAIL_QUALITY, output)
        }
        return FileProvider.getUriForFile(context, authority, thumbnailFile)
    }

    /**
     * Map a FileProvider content URI back to the file inside app storage.
     * URIs look like content://<authority>/<pathName>/<filename> where
     * pathName matches the names declared in file_paths.xml.
     */
    private fun resolveAppStorageFile(uri: Uri): File? {
        val segments = uri.pathSegments
        if (segments.size < 2) return null
        val dir = when (segments[segments.size - 2]) {
            MEDIA_DIR -> File(context.filesDir, MEDIA_DIR)
            THUMBNAILS_DIR -> File(context.filesDir, THUMBNAILS_DIR)
            else -> return null
        }
        return File(dir, segments.last())
    }

    /** Avoid overwriting existing files with the same display name. */
    private fun uniqueFile(dir: File, filename: String): File {
        var candidate = File(dir, filename)
        if (!candidate.exists()) return candidate
        val dotIndex = filename.lastIndexOf('.')
        val base = if (dotIndex > 0) filename.substring(0, dotIndex) else filename
        val extension = if (dotIndex > 0) filename.substring(dotIndex) else ""
        var counter = 1
        while (candidate.exists()) {
            candidate = File(dir, "${base}_$counter$extension")
            counter++
        }
        return candidate
    }

    private companion object {
        const val DEFAULT_FILENAME = "file"
    }
}

/** Metadata extracted from a video file. */
internal data class VideoMetadata(
    val width: Int?,
    val height: Int?,
    val duration: Long?,
    val thumbnailUri: Uri?
)

/** Thrown when copying a media file into app storage fails. */
internal class MediaCopyException(message: String) : Exception(message)

/** Thrown when deleting a media file fails. */
internal class MediaDeletionException(message: String) : Exception(message)

/** Thrown when metadata extraction fails. */
internal class MediaMetadataException(message: String) : Exception(message)
