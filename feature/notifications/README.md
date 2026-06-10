# Notifications Module

A clean, simple, and powerful notification module that handles all the complexity internally. Other modules just provide the essentials and everything else is handled automatically.

## Features

- **Ultra-simple API** - Just provide channel ID, title, message, and optional parameters
- **Automatic channel management** - Channels are created lazily with sensible defaults
- **Automatic settings checking** - Respects user notification preferences
- **Automatic grouping** - Notifications are grouped by channel automatically
- **Deep linking support** - Easy deep link creation for navigation
- **Multiple notification styles** - Simple, big text, grouped, and progress notifications
- **Custom sounds** - Support for custom notification sounds
- **Action buttons** - Support for notification actions
- **Type-safe** - All APIs are type-safe and well-documented
- **SOLID Principles** - Follows Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion principles

## Quick Start

### 1. Inject the API

```kotlin
@Singleton
class MyFeatureService @Inject constructor(
    private val notificationApi: UltraSimpleNotificationApi
) {
    // Your code here
}
```

### 2. Send a simple notification

```kotlin
suspend fun sendNotification() {
    notificationApi.send(
        channelId = "my_feature",
        title = "Task Created",
        message = "New task added to board",
        icon = android.R.drawable.ic_dialog_info,
        deepLink = "kino://task/123"
    )
}
```

That's it! No channel management, no settings checks, no complex configuration.

## API Reference

### UltraSimpleNotificationApi

The main API that other modules should use.

#### send()
Send a simple notification with minimal required data.

```kotlin
suspend fun send(
    channelId: String,           // Unique identifier for this type of notification
    title: String,               // Notification title
    message: String,             // Notification message
    icon: Int = android.R.drawable.ic_dialog_info,  // Icon (optional)
    deepLink: String? = null,    // Deep link when tapped (optional)
    sound: String? = null,       // Custom sound URI (optional)
    actions: List<NotificationAction> = emptyList()  // Action buttons (optional)
)
```

#### sendBigText()
Send a notification with expandable big text.

```kotlin
suspend fun sendBigText(
    channelId: String,
    title: String,
    message: String,             // Short message (shown when collapsed)
    bigText: String,             // Long text (shown when expanded)
    icon: Int = android.R.drawable.ic_dialog_info,
    deepLink: String? = null,
    sound: String? = null,
    actions: List<NotificationAction> = emptyList()
)
```

#### sendGrouped()
Send a grouped notification (inbox style) for multiple items.

```kotlin
suspend fun sendGrouped(
    channelId: String,
    title: String,
    summaryText: String,
    items: List<String>,         // List of items to show in the inbox
    icon: Int = android.R.drawable.ic_dialog_info,
    deepLink: String? = null,
    sound: String? = null,
    actions: List<NotificationAction> = emptyList()
)
```

#### sendProgress()
Send a progress notification.

```kotlin
suspend fun sendProgress(
    channelId: String,
    title: String,
    message: String,
    progress: Int,               // Current progress (0-100)
    maxProgress: Int = 100,      // Maximum progress
    indeterminate: Boolean = false,  // Whether progress is indeterminate
    icon: Int = android.R.drawable.ic_dialog_info,
    deepLink: String? = null,
    sound: String? = null,
    actions: List<NotificationAction> = emptyList()
)
```

#### cancelAll()
Cancel all notifications for a specific channel.

```kotlin
fun cancelAll(channelId: String)
```

#### cancel()
Cancel a specific notification by ID.

```kotlin
fun cancel(notificationId: Int)
```

## Examples

### Basic Usage

```kotlin
// Simple notification
notificationApi.send(
    channelId = "recurring_tasks",
    title = "🔄 Recurring Task Created",
    message = "✨ Daily standup has been added to Backlog in Work",
    deepLink = "kino://task/123"
)

// Notification with custom sound
notificationApi.send(
    channelId = "alerts",
    title = "Important Alert",
    message = "Something needs your attention",
    sound = "android.resource://com.letsgotoperfection.kino/raw/alert_sound"
)

// Big text notification
notificationApi.sendBigText(
    channelId = "notes",
    title = "Note Updated",
    message = "Your note has been updated",
    bigText = "This is a long text that will be shown when the user expands the notification...",
    deepLink = "kino://note/456"
)

// Grouped notification
notificationApi.sendGrouped(
    channelId = "recurring_tasks",
    title = "🔄 4 Recurring Tasks Created",
    summaryText = "Tasks have been added to your board",
    items = listOf(
        "• Daily standup → Backlog (Work)",
        "• Weekly review → In Progress (Personal)",
        "• Team meeting → Done (Work)",
        "• Gym session → Backlog (Personal)"
    ),
    deepLink = "kino://kanban"
)

// Progress notification
notificationApi.sendProgress(
    channelId = "sync",
    title = "Syncing Data",
    message = "Uploading files to cloud",
    progress = 75,
    maxProgress = 100,
    deepLink = "kino://sync"
)
```

## What's Handled Automatically

The notification module handles all the complexity internally:

- **Channel Creation**: Channels are created lazily with sensible defaults
- **Settings Checking**: Automatically checks if notifications are enabled
- **Grouping**: Notifications are grouped by channel automatically
- **Deep Links**: Creates proper PendingIntents for deep linking
- **Styling**: Applies proper notification styling and formatting
- **ID Management**: Generates unique notification IDs
- **Android Complexity**: Handles all Android notification API complexity

## Channel Management

Channels are created automatically with these defaults:

- **Name**: User-friendly name based on channel ID (e.g., "recurring_tasks" → "Recurring Tasks")
- **Description**: "Notifications for [Channel Name]"
- **Importance**: High (for most channels)
- **Vibration**: Enabled
- **Lights**: Enabled
- **Sound**: Default system sound (or custom if provided)

## Deep Linking

The module supports deep linking with the `kino://` scheme:

- `kino://task/123` - Navigate to specific task
- `kino://kanban` - Navigate to kanban board
- `kino://note/456` - Navigate to specific note
- `kino://settings` - Navigate to settings

## Dependencies

This module depends on:
- `:core:common` - Shared utilities and extensions
- AndroidX Core
- AndroidX Lifecycle
- Hilt for dependency injection

## Integration

To use this module in your feature:

1. Add dependency in your `build.gradle.kts`:
   ```kotlin
   implementation(project(":feature:notifications"))
   ```

2. Inject the API in your service:
   ```kotlin
   @Singleton
   class MyService @Inject constructor(
       private val notificationApi: UltraSimpleNotificationApi
   )
   ```

3. Send notifications:
   ```kotlin
   notificationApi.send(
       channelId = "my_feature",
       title = "Something happened",
       message = "Here's what happened"
   )
   ```

That's it! No complex setup, no boilerplate code, just simple and powerful notifications.