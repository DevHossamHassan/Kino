package com.letsgotoperfection.kino.feature.recurringtasks.internal.permission

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for checking and requesting exact alarm permissions.
 * Required for Android 12+ (API 31+) to schedule exact alarms.
 */
@Singleton
class AlarmPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * Check if the app can schedule exact alarms.
     * - Android 12+: Requires SCHEDULE_EXACT_ALARM permission
     * - Below Android 12: Always returns true
     */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
    
    /**
     * Open system settings to grant exact alarm permission.
     * Only works on Android 12+.
     */
    fun openAlarmPermissionSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                Log.i(TAG, "Opened exact alarm permission settings")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open alarm permission settings", e)
                // Fallback to general app settings
                openAppSettings()
            }
        } else {
            Log.w(TAG, "Alarm permission settings not needed for Android ${Build.VERSION.SDK_INT}")
        }
    }
    
    /**
     * Open general app settings as fallback.
     */
    fun openAppSettings() {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Log.i(TAG, "Opened app settings")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open app settings", e)
        }
    }
    
    /**
     * Whether the exact alarm permission flow is relevant on this device
     * (Android 12+ only) and currently not granted.
     */
    fun isExactAlarmPermissionMissing(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarms()
    }
    
    companion object {
        private const val TAG = "AlarmPermissionManager"
    }
}





