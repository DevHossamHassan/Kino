package com.letsgotoperfection.kino.feature.media.internal.presentation.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Handles media permissions for different Android versions
 * 
 * Android 13+ (API 33+): Uses granular media permissions
 * Android 10-12 (API 29-32): Uses READ_EXTERNAL_STORAGE
 * Below Android 10: Uses READ_EXTERNAL_STORAGE + WRITE_EXTERNAL_STORAGE
 */
internal class MediaPermissionHandler {
    
    companion object {
        /**
         * Get required permissions based on Android version
         */
        fun getRequiredPermissions(): Array<String> {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ uses granular media permissions
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10-12 uses READ_EXTERNAL_STORAGE
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                // Below Android 10
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        
        /**
         * Check if all required permissions are granted
         */
        fun hasPermissions(context: Context): Boolean {
            return getRequiredPermissions().all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        
        /**
         * Get permission rationale message based on Android version
         */
        fun getPermissionRationaleMessage(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                "To manage your attached media, we need permission to access your photos and videos."
            } else {
                "To manage your attached media, we need permission to access your device storage."
            }
        }
    }
}

/**
 * Composable permission handler with Accompanist
 * 
 * @return MultiplePermissionsState for handling media permissions
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun rememberMediaPermissionState(): MultiplePermissionsState {
    return rememberMultiplePermissionsState(
        permissions = MediaPermissionHandler.getRequiredPermissions().toList()
    )
}
