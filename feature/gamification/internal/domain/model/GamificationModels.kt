package com.letsgotoperfection.kino.feature.gamification.internal.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Gamification events for achievements and milestones
 */
@Serializable
internal sealed class GamificationEvent {
    @Serializable
    data class MicroTaskCompleted(
        val taskTitle: String,
        val streakCount: Int
    ) : GamificationEvent()
    
    @Serializable
    data class TaskCompletedEarly(
        val taskTitle: String,
        val daysEarly: Int
    ) : GamificationEvent()
    
    @Serializable
    data class StreakMilestone(
        val streakDays: Int
    ) : GamificationEvent()
    
    @Serializable
    data class PerfectWeek(
        val tasksCompleted: Int
    ) : GamificationEvent()
    
    @Serializable
    data class FirstTaskOfDay(
        val taskTitle: String
    ) : GamificationEvent()
    
    @Serializable
    data class ComebackStreak(
        val daysSinceLastTask: Int
    ) : GamificationEvent()
}

/**
 * Notification data for task reminders and celebrations
 */
@Serializable
internal data class TaskNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val taskId: String,
    val microTaskId: String?,
    val type: NotificationType,
    val title: String,
    val message: String,
    val scheduledTime: LocalDateTime,
    val isDelivered: Boolean = false,
    val deliveredAt: LocalDateTime? = null
)

@Serializable
internal enum class NotificationType {
    MICRO_TASK_REMINDER,
    ENCOURAGEMENT,
    DEADLINE_WARNING,
    STREAK_REMINDER,
    CELEBRATION,
    COMEBACK_MOTIVATION
}

/**
 * User achievement data
 */
@Serializable
internal data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlockedAt: LocalDateTime,
    val category: AchievementCategory
)

@Serializable
internal enum class AchievementCategory {
    STREAK,
    PRODUCTIVITY,
    CONSISTENCY,
    SPEED,
    COMEBACK
}

/**
 * User streak information
 */
@Serializable
internal data class Streak(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastTaskDate: LocalDateTime?,
    val streakStartDate: LocalDateTime?
)

/**
 * Daily productivity summary
 */
@Serializable
internal data class DailyProductivity(
    val date: LocalDateTime,
    val tasksCompleted: Int,
    val totalTasks: Int,
    val microTasksCompleted: Int,
    val streakDays: Int,
    val achievements: List<Achievement>
)

/**
 * Gamification-specific notification preferences
 */
@Serializable
internal data class GamificationPreferences(
    val enableAiAnalysis: Boolean = true,
    val useCloudAi: Boolean = false,
    val enableCelebrations: Boolean = true,
    val enableStreakReminders: Boolean = true,
    val enableDeadlineWarnings: Boolean = true
)

/**
 * Gamification statistics
 */
@Serializable
internal data class GamificationStats(
    val totalTasksCompleted: Int,
    val totalMicroTasksCompleted: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val achievementsUnlocked: Int,
    val totalAchievements: Int,
    val averageTasksPerDay: Float,
    val mostProductiveDay: String?,
    val streakStartDate: LocalDateTime?
)
