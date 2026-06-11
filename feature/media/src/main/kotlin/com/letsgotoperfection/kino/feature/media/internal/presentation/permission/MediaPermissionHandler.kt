package com.letsgotoperfection.kino.feature.media.internal.presentation.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Resolves the runtime permissions needed to read shared device media.
 *
 * Note: media attached through the app is copied into app-specific storage,
 * so browsing and viewing attached media never requires these permissions.
 * They are only relevant when reading directly from shared storage.
 *
 * Android 13+ (API 33+): granular media permissions
 * Android 10-12 (API 29-32): READ_EXTERNAL_STORAGE
 * Below Android 10: READ_EXTERNAL_STORAGE + WRITE_EXTERNAL_STORAGE
 */
internal object MediaPermissionHandler {

    fun getRequiredPermissions(): List<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            else -> listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun hasPermissions(context: Context): Boolean {
        return getRequiredPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
        }
    }
}
