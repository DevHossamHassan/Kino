package com.letsgotoperfection.kino.feature.notifications.api

import android.app.PendingIntent

/**
 * Data class for notification actions.
 * Used to add action buttons to notifications.
 */
data class NotificationAction(
    val id: String,
    val title: String,
    val icon: Int,
    val actionIntent: PendingIntent
)
