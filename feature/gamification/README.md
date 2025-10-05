# Gamification Module

This module provides gamification features including smart notifications, streak tracking, and achievement systems for the Kino task management app.

## Features

- **Smart Notifications**: AI-powered task reminders and motivational messages (via Notifications module)
- **Streak Tracking**: Tracks daily task completion streaks
- **Achievement System**: Unlocks achievements for milestones and consistency
- **Background Analysis**: WorkManager-based task analysis and notification scheduling
- **Celebration Notifications**: Fun notifications for achievements and milestones (via Notifications module)

## Architecture

```
feature/gamification/
├── api/                           # Public API interface
│   └── GamificationApi.kt
├── internal/
│   ├── domain/
│   │   ├── model/                 # Domain models
│   │   │   ├── GamificationModels.kt
│   │   │   └── Achievement.kt
│   │   └── usecase/               # Business logic
│   │       └── CalculateStreakUseCase.kt
│   ├── notification/              # Notification integration
│   │   └── MotivationalMessages.kt
│   ├── achievement/               # Achievement tracking
│   │   ├── AchievementTracker.kt
│   │   └── RewardCalculator.kt
│   ├── worker/                    # Background workers
│   │   └── GamificationWorker.kt
│   └── data/
│       └── repository/
│           └── GamificationApiImpl.kt
└── di/
    └── GamificationModule.kt
```

## Usage

### Track Task Completion

```kotlin
@Inject
lateinit var gamificationApi: GamificationApi

// Track when a task is completed
gamificationApi.trackTaskCompletion(taskId)

// Track micro-task completion
gamificationApi.trackMicroTaskCompletion(microTaskId, taskId)
```

### Get Streak Information

```kotlin
val streak = gamificationApi.getCurrentStreak()
println("Current streak: ${streak.currentStreak} days")
println("Longest streak: ${streak.longestStreak} days")
```

### Schedule Smart Reminders

```kotlin
// Schedule AI-powered reminders for a task
gamificationApi.scheduleSmartReminders(task)
```

### Get Achievements

```kotlin
val achievements = gamificationApi.getAchievements()
achievements.forEach { achievement ->
    println("🏆 ${achievement.title}: ${achievement.description}")
}
```

## Notification Types

### Micro-task Reminders
- Reminds users of the next step in their task
- Includes action buttons (Mark Done, Snooze)
- Deep links to task detail screen

### Motivational Messages
- AI-generated encouraging messages
- Based on progress, streak, and deadline
- Personalized to user's current situation

### Achievement Celebrations
- Celebrates milestone achievements
- Streak milestones (7, 30, 100 days)
- Productivity achievements
- Comeback celebrations

## Achievement Categories

### Streak Achievements
- **Week Warrior**: 7-day streak
- **Month Master**: 30-day streak
- **Century**: 100-day streak

### Productivity Achievements
- **Getting Started**: First task completed
- **Perfect Week**: All tasks completed in a week
- **Early Bird**: Task completed ahead of deadline

### Consistency Achievements
- **Comeback King**: Return after a break
- **Daily Grind**: Consistent daily completion

## Background Processing

The module uses WorkManager to:

1. **Analyze Tasks**: Every 12 hours, analyze upcoming tasks
2. **Schedule Notifications**: Create smart reminders based on AI analysis
3. **Track Progress**: Monitor task completion and update streaks
4. **Celebrate Achievements**: Send celebration notifications

## Configuration

### Notification Preferences

```kotlin
val preferences = NotificationPreferences(
    enableAiAnalysis = true,
    useCloudAi = false,
    notificationFrequency = NotificationFrequency.MEDIUM,
    quietHoursStart = "22:00",
    quietHoursEnd = "07:00",
    enableCelebrations = true,
    enableStreakReminders = true,
    enableDeadlineWarnings = true
)

gamificationApi.updateNotificationPreferences(preferences)
```

### Quiet Hours

The system respects user-defined quiet hours to avoid disturbing sleep:

```kotlin
val isQuiet = gamificationApi.isInQuietHours()
if (!isQuiet) {
    // Send notification
}
```

## Integration with Other Modules

The gamification module integrates with other modules:

### AI Analysis Module
1. **Get Task Breakdowns**: Use AI to break tasks into micro-tasks
2. **Generate Motivational Messages**: Create personalized encouragement
3. **Smart Scheduling**: Schedule notifications based on AI analysis
4. **Progress Tracking**: Monitor micro-task completion

### Notifications Module
1. **Schedule Notifications**: Use the notifications API for all notification scheduling
2. **Manage Preferences**: Leverage existing notification preferences
3. **Handle Quiet Hours**: Respect user's notification settings
4. **Celebration Notifications**: Send achievement notifications through the notifications system

## Testing

Run the tests to verify functionality:

```bash
./gradlew :feature:gamification:test
```

## Dependencies

- WorkManager for background processing
- AI Analysis module for smart features
- Notifications module for notification management
- Hilt for dependency injection

## Privacy & User Control

- Users can disable AI analysis
- Configurable notification frequency
- Quiet hours support
- Achievement data stored locally
- No personal data transmitted to external services
