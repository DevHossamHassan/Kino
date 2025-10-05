# Notification Module

A comprehensive notification system for the Kino task management app that provides centralized notification management across all features.

## Features

- **Modern Android Notification APIs** - Supports Android 13+ runtime permissions and notification channels
- **Scheduled Notifications** - Uses WorkManager for reliable background scheduling
- **Interactive Actions** - Quick actions directly from notifications (Complete, Snooze, etc.)
- **Deep Linking** - Navigate to specific screens when tapping notifications
- **Permission Management** - Handles Android 13+ runtime permissions gracefully
- **Notification Tracking** - Tracks delivery, dismissal, and user engagement
- **Multi-Channel Support** - Different channels for different types of notifications

## Architecture

```
feature/notifications/
├── api/                           # Public API for other modules
│   └── NotificationApi.kt
├── internal/
│   ├── domain/
│   │   ├── model/                 # Domain models
│   │   └── repository/            # Repository interfaces
│   ├── data/
│   │   ├── local/                 # Room database
│   │   └── repository/            # Repository implementations
│   ├── manager/                   # Android-specific managers
│   ├── worker/                    # WorkManager workers
│   ├── receiver/                  # Broadcast receivers
│   └── presentation/              # UI components
└── di/                           # Dependency injection
```

## Usage

### Basic Notification

```kotlin
@Inject
lateinit var notificationApi: NotificationApi

// Send immediate notification
notificationApi.sendNotification(
    title = "Task Reminder",
    message = "Don't forget to complete your task",
    category = NotificationCategory.TASK_REMINDER,
    deepLink = "kino://task/123"
)
```

### Scheduled Notification

```kotlin
// Schedule notification for later
notificationApi.scheduleNotification(
    title = "Meeting Reminder",
    message = "Team meeting in 30 minutes",
    category = NotificationCategory.TASK_REMINDER,
    scheduledTime = LocalDateTime.now().plusMinutes(30),
    deepLink = "kino://task/456"
)
```

### Task Reminder

```kotlin
// Send task reminder with actions
notificationApi.sendTaskReminder(
    taskId = "task_123",
    taskTitle = "Complete project proposal",
    message = "This task is due today",
    scheduledTime = LocalDateTime.now().plusHours(2)
)
```

### Achievement Notification

```kotlin
// Send achievement notification
notificationApi.sendAchievementNotification(
    achievementTitle = "Task Master",
    message = "You've completed 10 tasks this week!"
)
```

### Smart Suggestion

```kotlin
// Send AI-powered suggestion
notificationApi.sendSmartSuggestion(
    taskId = "task_123",
    suggestion = "Consider breaking this task into smaller steps"
)
```

## Notification Channels

The module creates 4 notification channels:

1. **Task Reminders** - High priority, vibration, LED
2. **Smart Suggestions** - Medium priority, no vibration
3. **Achievements** - Celebratory, vibration, LED
4. **Notes** - Low priority, subtle

## Permission Handling

The module automatically handles Android 13+ runtime permissions:

```kotlin
@Composable
fun MyScreen() {
    NotificationPermissionRequest(
        onPermissionGranted = { /* Start notifications */ },
        onPermissionDenied = { /* Show alternative UI */ }
    )
}
```

## Integration Examples

### From Kanban Feature

```kotlin
@HiltViewModel
class KanbanViewModel @Inject constructor(
    private val notificationApi: NotificationApi
) : ViewModel() {
    
    fun createTaskWithReminder(task: Task, reminderTime: LocalDateTime) {
        viewModelScope.launch {
            // Create task
            createTask(task)
            
            // Schedule reminder
            notificationApi.sendTaskReminder(
                taskId = task.id,
                taskTitle = task.title,
                message = "Don't forget to work on this task",
                scheduledTime = reminderTime
            )
        }
    }
}
```

### From Gamification Feature

```kotlin
@Singleton
class AchievementManager @Inject constructor(
    private val notificationApi: NotificationApi
) {
    
    suspend fun unlockAchievement(achievement: Achievement) {
        // Unlock achievement logic...
        
        // Send notification
        notificationApi.sendAchievementNotification(
            achievementTitle = achievement.title,
            message = achievement.description
        )
    }
}
```

## Deep Links

The module supports deep linking to various screens:

- `kino://task/{taskId}` - Task detail screen
- `kino://note/{noteId}` - Note detail screen
- `kino://achievements` - Achievements screen
- `kino://progress` - Progress screen
- `kino://insights` - AI insights screen

## Testing

The module includes comprehensive testing support:

```kotlin
@Test
fun `send notification with permission granted`() = runTest {
    // Given
    val notificationApi = FakeNotificationApi()
    
    // When
    val result = notificationApi.sendNotification(
        title = "Test",
        message = "Test message",
        category = NotificationCategory.TASK_REMINDER
    )
    
    // Then
    assertThat(result.isSuccess).isTrue()
}
```

## Dependencies

- **WorkManager** - For scheduled notifications
- **Room** - For notification persistence
- **Hilt** - For dependency injection
- **Accompanist Permissions** - For runtime permission handling
- **Kotlinx Serialization** - For notification data serialization

## Requirements

- Android API 26+ (Android 8.0)
- Kotlin 1.9+
- Jetpack Compose
- Hilt for dependency injection
