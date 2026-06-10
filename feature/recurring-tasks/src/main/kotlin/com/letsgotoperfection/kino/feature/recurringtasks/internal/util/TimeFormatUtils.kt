package com.letsgotoperfection.kino.feature.recurringtasks.internal.util

import android.content.Context
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Utility functions for time formatting based on device settings.
 * 
 * KEY POINTS:
 * - Internal storage: ALWAYS uses 24-hour format (LocalTime stores 0-23 hours)
 * - Display format: Determined by device setting at runtime
 * - Format changes: Automatically handled when user changes device settings
 * 
 * EXAMPLE BEHAVIOR:
 * ```
 * // Stored as: LocalTime.of(14, 30) // Always 24-hour internally
 * 
 * // Device in 24-hour mode:
 * formatTime(time, context) → "14:30"
 * 
 * // User changes device to 12-hour mode:
 * formatTime(time, context) → "2:30 PM"  // Same time, different display
 * 
 * // User changes back to 24-hour mode:
 * formatTime(time, context) → "14:30"    // Automatically updates
 * ```
 * 
 * This ensures:
 * ✅ Data integrity: Time stored consistently
 * ✅ User preference: Display matches device setting
 * ✅ Dynamic updates: Changes when device format changes
 */
object TimeFormatUtils {
    
    /**
     * Format time according to device's current 12-hour or 24-hour preference.
     * 
     * This function queries the device setting at runtime, so it will
     * automatically adapt if the user changes their time format preference.
     * 
     * @param time The LocalTime to format (stored in 24-hour format internally)
     * @param context Android context to check device settings
     * @return Formatted time string matching device preference
     */
    fun formatTime(time: LocalTime, context: Context): String {
        val is24Hour = android.text.format.DateFormat.is24HourFormat(context)
        val formatter = if (is24Hour) {
            DateTimeFormatter.ofPattern("HH:mm")  // 24-hour: "14:30"
        } else {
            DateTimeFormatter.ofPattern("h:mm a") // 12-hour: "2:30 PM"
        }
        return time.format(formatter)
    }
    
    /**
     * Get time formatter based on current device settings.
     * 
     * @param context Android context to check device settings
     * @return DateTimeFormatter matching device preference
     */
    fun getTimeFormatter(context: Context): DateTimeFormatter {
        val is24Hour = android.text.format.DateFormat.is24HourFormat(context)
        return if (is24Hour) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("h:mm a")
        }
    }
    
    /**
     * Check if device currently uses 24-hour format.
     * 
     * This is checked at runtime, so the result can change if the user
     * modifies their device settings.
     * 
     * @param context Android context to check device settings
     * @return true if device uses 24-hour format, false for 12-hour
     */
    fun is24HourFormat(context: Context): Boolean {
        return android.text.format.DateFormat.is24HourFormat(context)
    }
}

/**
 * Extension function for LocalTime to format according to current device settings.
 * 
 * Usage:
 * ```kotlin
 * val time = LocalTime.of(14, 30)
 * val formatted = time.formatForDevice(context)
 * // On 24-hour device: "14:30"
 * // On 12-hour device: "2:30 PM"
 * ```
 * 
 * @param context Android context to check device settings
 * @return Formatted time string matching device preference
 */
fun LocalTime.formatForDevice(context: Context): String {
    return TimeFormatUtils.formatTime(this, context)
}

