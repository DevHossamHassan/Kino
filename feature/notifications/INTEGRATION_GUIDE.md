# Notification Module Integration Guide

This guide shows how to integrate the notification module with other feature modules in the Kino app.

## Quick Start

### 1. Add Dependency

In your feature module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":feature:notifications"))
}
```

### 2. Inject NotificationApi

```kotlin
@HiltViewModel
class YourViewModel @Inject constructor(
    private val notificationApi: NotificationApi
) : ViewModel() {
    // Your implementation
}
```

### 3. Send Notifications

```kotlin
// Send immediate notification
notificationApi.sendNotification(
    title = "Your Title",
    message = "Your message",
    category = NotificationCategory.TASK_REMINDER
)

// Schedule notification
notificationApi.scheduleNotification(
    title = "Scheduled Title",
    message = "Scheduled message",
    category = NotificationCategory.TASK_REMINDER,
    scheduledTime = LocalDateTime.now().plusHours(1)
)
```

## Feature-Specific Examples

### Kanban Feature

```kotlin
@HiltViewModel
class KanbanViewModel @Inject constructor(
    private val notificationApi: NotificationApi
) : ViewModel() {
    
    fun createTaskWithReminder(task: Task, reminderTime: LocalDateTime) {
        viewModelScope.launch {
            // Create task logic...
            
            // Schedule reminder
            notificationApi.sendTaskReminder(
                taskId = task.id,
                taskTitle = task.title,
                message = "Don't forget to work on this task",
                scheduledTime = reminderTime
            )
        }
    }
    
    fun sendTaskDueSoonNotification(task: Task) {
        viewModelScope.launch {
            notificationApi.sendNotification(
                title = "Task Due Soon",
                message = "\"${task.title}\" is due soon",
                category = NotificationCategory.TASK_DUE_SOON,
                deepLink = "kino://task/${task.id}"
            )
        }
    }
}
```

### Gamification Feature

```kotlin
@Singleton
class AchievementManager @Inject constructor(
    private val notificationApi: NotificationApi
) {
    
    suspend fun unlockAchievement(achievement: Achievement) {
        // Unlock achievement logic...
        
        notificationApi.sendAchievementNotification(
            achievementTitle = achievement.title,
            message = achievement.description
        )
    }
    
    suspend fun sendStreakReminder(currentStreak: Int) {
        notificationApi.sendNotification(
            title = "Keep Your Streak Going! 🔥",
            message = "You're on a $currentStreak day streak. Don't break it!",
            category = NotificationCategory.STREAK_REMINDER,
            deepLink = "kino://achievements"
        )
    }
}
```

### Notes Feature

```kotlin
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notificationApi: NotificationApi
) : ViewModel() {
    
    fun setNoteReminder(note: Note, reminderTime: LocalDateTime) {
        viewModelScope.launch {
            notificationApi.sendNoteReminder(
                noteId = note.id,
                noteTitle = note.title,
                message = "Reminder to review: ${note.title}",
                scheduledTime = reminderTime
            )
        }
    }
}
```

### AI Analysis Feature

```kotlin
@Singleton
class TaskAnalyzer @Inject constructor(
    private val notificationApi: NotificationApi
) {
    
    suspend fun sendSmartSuggestion(task: Task, suggestion: String) {
        notificationApi.sendSmartSuggestion(
            taskId = task.id,
            suggestion = suggestion
        )
    }
    
    suspend fun sendProductivityInsight(insight: String) {
        notificationApi.sendNotification(
            title = "Productivity Insight",
            message = insight,
            category = NotificationCategory.SMART_SUGGESTION,
            deepLink = "kino://insights"
        )
    }
}
```

## Permission Handling

### In Your Activity/Fragment

```kotlin
@Composable
fun YourScreen() {
    NotificationPermissionRequest(
        onPermissionGranted = {
            // Start sending notifications
        },
        onPermissionDenied = {
            // Show alternative UI or settings button
        }
    )
}
```

### Check Permission Programmatically

```kotlin
if (notificationApi.hasPermission()) {
    // Send notification
} else {
    // Show permission request UI
    notificationApi.openSettings()
}
```

## Deep Link Handling

### In Your Navigation Graph

```kotlin
@Composable
fun AppNavGraph() {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
        
        // Handle deep links from notifications
        composable("task/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            TaskDetailScreen(taskId = taskId)
        }
        
        composable("note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            NoteDetailScreen(noteId = noteId)
        }
        
        composable("achievements") {
            AchievementsScreen()
        }
    }
}
```

## Testing

### Unit Tests

```kotlin
@ExtendWith(CoroutineTestExtension::class)
class YourViewModelTest {
    
    @Test
    fun `send notification when task created`() = runTest {
        // Given
        val fakeNotificationApi = FakeNotificationApi()
        val viewModel = YourViewModel(fakeNotificationApi)
        
        // When
        viewModel.createTaskWithReminder(testTask, reminderTime)
        
        // Then
        assertThat(fakeNotificationApi.notificationsSent).hasSize(1)
        assertThat(fakeNotificationApi.notificationsSent.first().title)
            .contains("Task Reminder")
    }
}
```

### Fake NotificationApi for Testing

```kotlin
class FakeNotificationApi : NotificationApi {
    val notificationsSent = mutableListOf<NotificationData>()
    var shouldFail = false
    
    override suspend fun sendNotification(
        title: String,
        message: String,
        category: NotificationCategory,
        deepLink: String?,
        actions: List<NotificationAction>
    ): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Test failure"))
        } else {
            notificationsSent.add(
                NotificationData(
                    id = UUID.randomUUID().toString(),
                    channelId = "test_channel",
                    title = title,
                    message = message,
                    priority = NotificationPriority.DEFAULT,
                    category = category,
                    deepLink = deepLink,
                    actions = actions
                )
            )
            Result.success(Unit)
        }
    }
    
    // Implement other methods...
}
```

## Best Practices

### 1. Use Appropriate Categories

```kotlin
// ✅ Good - Use specific categories
NotificationCategory.TASK_REMINDER
NotificationCategory.ACHIEVEMENT
NotificationCategory.SMART_SUGGESTION

// ❌ Avoid - Generic categories
NotificationCategory.DEFAULT
```

### 2. Provide Meaningful Deep Links

```kotlin
// ✅ Good - Specific deep links
"kino://task/123"
"kino://note/456"
"kino://achievements"

// ❌ Avoid - Generic deep links
"kino://app"
"kino://home"
```

### 3. Handle Errors Gracefully

```kotlin
notificationApi.sendNotification(...)
    .onSuccess {
        // Log success
    }
    .onFailure { error ->
        // Log error, show user message
        Log.e("Notifications", "Failed to send notification", error)
    }
```

### 4. Don't Spam Users

```kotlin
// ✅ Good - Reasonable frequency
if (shouldSendReminder && !recentlySent) {
    notificationApi.sendNotification(...)
}

// ❌ Avoid - Too frequent
notificationApi.sendNotification(...) // Every time user opens app
```

### 5. Use Scheduled Notifications Wisely

```kotlin
// ✅ Good - Meaningful scheduling
val reminderTime = task.dueDate.minusHours(1)
notificationApi.scheduleNotification(..., scheduledTime = reminderTime)

// ❌ Avoid - Random scheduling
val randomTime = LocalDateTime.now().plusMinutes(Random.nextInt(60))
notificationApi.scheduleNotification(..., scheduledTime = randomTime)
```

## Troubleshooting

### Common Issues

1. **Notifications not showing**
   - Check if permission is granted
   - Verify notification channels are created
   - Check if notifications are enabled in settings

2. **Deep links not working**
   - Verify deep link format
   - Check navigation graph setup
   - Test deep link manually

3. **Scheduled notifications not working**
   - Check WorkManager setup
   - Verify exact alarm permission (Android 12+)
   - Check battery optimization settings

### Debug Commands

```bash
# Check notification channels
adb shell dumpsys notification

# Check WorkManager
adb shell dumpsys jobscheduler

# Check permissions
adb shell dumpsys package com.letsgotoperfection.kino | grep permission
```

## Migration from Old Notification System

If you're migrating from an existing notification system:

1. **Update Dependencies**
   ```kotlin
   // Remove old notification dependencies
   // Add new notification module
   implementation(project(":feature:notifications"))
   ```

2. **Replace Notification Calls**
   ```kotlin
   // Old way
   NotificationManagerCompat.from(context).notify(id, notification)
   
   // New way
   notificationApi.sendNotification(title, message, category)
   ```

3. **Update Permission Handling**
   ```kotlin
   // Old way
   if (ContextCompat.checkSelfPermission(...) == PERMISSION_GRANTED)
   
   // New way
   if (notificationApi.hasPermission())
   ```

4. **Test Thoroughly**
   - Test all notification types
   - Test permission flows
   - Test deep links
   - Test scheduled notifications
