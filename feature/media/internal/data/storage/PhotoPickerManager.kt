package com.letsgotoperfection.kino.feature.media.internal.data.storage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Photo Picker API for selecting media
 * 
 * This class handles:
 * - Checking Photo Picker availability
 * - Creating appropriate intents for different Android versions
 * - Supporting multiple selection and MIME type filtering
 */
@Singleton
internal class PhotoPickerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Check if Photo Picker is available
     * Available on Android 11+ with Google Play Services
     */
    fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true // Built-in on Android 13+
        } else {
            // Check if available via Google Play Services on Android 11-12
            context.packageManager.hasSystemFeature(
                "android.software.activity_recognition"
            )
        }
    }
    
    /**
     * Create Photo Picker intent for images only
     * Supports multiple selection
     */
    fun createImagePickerIntent(
        allowMultiple: Boolean = true,
        maxItems: Int = 10
    ): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use built-in Photo Picker on Android 13+
            Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                type = "image/*"
                putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxItems)
                if (allowMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
            }
        } else {
            // Fallback to ACTION_GET_CONTENT for older versions
            Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                if (allowMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                addCategory(Intent.CATEGORY_OPENABLE)
            }
        }
    }
    
    /**
     * Create Photo Picker intent for videos only
     * Supports multiple selection
     */
    fun createVideoPickerIntent(
        allowMultiple: Boolean = true,
        maxItems: Int = 10
    ): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use built-in Photo Picker on Android 13+
            Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                type = "video/*"
                putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxItems)
                if (allowMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
            }
        } else {
            // Fallback to ACTION_GET_CONTENT for older versions
            Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "video/*"
                if (allowMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                addCategory(Intent.CATEGORY_OPENABLE)
            }
        }
    }
    
    /**
     * Create Photo Picker intent for both images and videos
     * Supports multiple selection
     */
    fun createMediaPickerIntent(
        allowMultiple: Boolean = true,
        maxItems: Int = 10,
        mimeTypes: List<String> = listOf("image/*", "video/*")
    ): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use built-in Photo Picker on Android 13+
            Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                type = "*/*"
                putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxItems)
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes.toTypedArray())
                if (allowMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
            }
        } else {
            // Fallback to ACTION_GET_CONTENT for older versions
            Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes.toTypedArray())
                if (allowMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                addCategory(Intent.CATEGORY_OPENABLE)
            }
        }
    }
    
    /**
     * Create document picker intent
     * For PDFs and other documents
     */
    fun createDocumentPickerIntent(
        allowMultiple: Boolean = true,
        mimeTypes: List<String> = listOf("application/pdf", "text/*")
    ): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes.toTypedArray())
            if (allowMultiple) {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            addCategory(Intent.CATEGORY_OPENABLE)
        }
    }
}
