package com.letsgotoperfection.kino.feature.notifications.internal.presentation.composable

import android.Manifest
import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.app.NotificationManagerCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.letsgotoperfection.kino.feature.notifications.R
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationPermissionManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionRequest(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val permissionManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            NotificationPermissionEntryPoint::class.java
        ).notificationPermissionManager()
    }
    
    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }
    
    if (permissionState != null) {
        // Android 13+
        LaunchedEffect(permissionState.status) {
            when {
                permissionState.status.isGranted -> {
                    onPermissionGranted()
                }
                permissionState.status.shouldShowRationale -> {
                    // Show rationale
                }
                !permissionState.status.isGranted -> {
                    onPermissionDenied()
                }
            }
        }
        
        if (!permissionState.status.isGranted) {
            NotificationPermissionDialog(
                onRequestPermission = {
                    permissionState.launchPermissionRequest()
                },
                onDismiss = onPermissionDenied
            )
        }
    } else {
        // Pre-Android 13: Check if notifications enabled
        val notificationManager = NotificationManagerCompat.from(context)
        if (notificationManager.areNotificationsEnabled()) {
            onPermissionGranted()
        } else {
            NotificationDisabledDialog(
                onOpenSettings = {
                    permissionManager.openNotificationSettings()
                },
                onDismiss = onPermissionDenied
            )
        }
    }
}

@Composable
private fun NotificationPermissionDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.notification_permission_title)) },
        text = { Text(stringResource(R.string.notification_permission_message)) },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text(stringResource(R.string.notification_grant_permission))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Composable
private fun NotificationDisabledDialog(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.notification_permission_denied)) },
        text = { Text(stringResource(R.string.notification_permission_denied_message)) },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.notification_open_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

// Entry point for accessing NotificationPermissionManager from composable
interface NotificationPermissionEntryPoint {
    fun notificationPermissionManager(): NotificationPermissionManager
}
