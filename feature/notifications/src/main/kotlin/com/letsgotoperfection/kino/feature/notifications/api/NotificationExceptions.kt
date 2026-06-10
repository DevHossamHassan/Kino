package com.letsgotoperfection.kino.feature.notifications.api

/**
 * Exceptions for notification operations
 */
sealed class NotificationException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Thrown when trying to send a notification to a channel that doesn't exist or is disabled
 */
class ChannelNotEnabledException(
    channelId: String,
    cause: Throwable? = null
) : NotificationException(
    message = "Channel '$channelId' does not exist or is disabled. Create the channel first using NotificationChannelManager.",
    cause = cause
)

/**
 * Thrown when trying to create a channel that already exists
 */
class ChannelAlreadyExistsException(
    channelId: String,
    cause: Throwable? = null
) : NotificationException(
    message = "Channel '$channelId' already exists. Use updateChannel() to modify existing channels.",
    cause = cause
)

/**
 * Thrown when notification system is not properly initialized
 */
class NotificationSystemNotInitializedException(
    cause: Throwable? = null
) : NotificationException(
    message = "Notification system is not initialized. Call NotificationInitializer.initialize() first.",
    cause = cause
)



