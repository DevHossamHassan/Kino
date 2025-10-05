package com.letsgotoperfection.kino.feature.notifications.api

/**
 * Navigation destinations for Notifications feature
 */
object NotificationsDestinations {
    const val NOTIFICATIONS_SETTINGS = "notifications_settings"
    const val NOTIFICATION_DETAIL = "notification_detail/{notificationId}"
    
    fun notificationDetailRoute(notificationId: String) = "notification_detail/$notificationId"
}

