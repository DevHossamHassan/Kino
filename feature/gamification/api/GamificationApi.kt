package com.letsgotoperfection.kino.feature.gamification.api

import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.gamification.internal.domain.model.Achievement
import com.letsgotoperfection.kino.feature.gamification.internal.domain.model.GamificationStats
import com.letsgotoperfection.kino.feature.gamification.internal.domain.model.Streak
import com.letsgotoperfection.kino.feature.notifications.api.NotificationPreferences
import java.time.LocalDateTime

/**
 * Public API for Gamification feature.
 * 
 * This API allows other feature modules to:
 * - Track task completion and progress
 * - Manage streaks and achievements
 * - Schedule smart notifications
 * - Get gamification statistics
 * 
 * @since 1.0.0
 */
interface GamificationApi {
    
    /**
     * Tracks when a task is completed and updates streaks/achievements
     * 
     * @param taskId The completed task ID
     * @param completedAt When the task was completed
     */
    suspend fun trackTaskCompletion(
        taskId: String,
        completedAt: LocalDateTime = LocalDateTime.now()
    )
    
    /**
     * Tracks when a micro-task is completed
     * 
     * @param microTaskId The completed micro-task ID
     * @param taskId The parent task ID
     * @param completedAt When the micro-task was completed
     */
    suspend fun trackMicroTaskCompletion(
        microTaskId: String,
        taskId: String,
        completedAt: LocalDateTime = LocalDateTime.now()
    )
    
    /**
     * Gets the current streak information
     * 
     * @return Current streak data
     */
    suspend fun getCurrentStreak(): Streak
    
    /**
     * Gets all unlocked achievements
     * 
     * @return List of achievements
     */
    suspend fun getAchievements(): List<Achievement>
    
    /**
     * Schedules smart reminders for a task using AI analysis
     * 
     * @param task The task to schedule reminders for
     */
    suspend fun scheduleSmartReminders(task: Task)
    
    /**
     * Cancels all notifications for a specific task
     * 
     * @param taskId The task ID to cancel notifications for
     */
    suspend fun cancelTaskNotifications(taskId: String)
    
    /**
     * Gets gamification statistics
     * 
     * @return User's gamification stats
     */
    suspend fun getGamificationStats(): GamificationStats
    
    /**
     * Updates notification preferences
     * 
     * @param preferences New notification preferences
     */
    suspend fun updateNotificationPreferences(preferences: NotificationPreferences)
    
    /**
     * Gets current notification preferences
     * 
     * @return Current preferences
     */
    suspend fun getNotificationPreferences(): NotificationPreferences
    
    /**
     * Checks if user is in quiet hours
     * 
     * @return true if currently in quiet hours
     */
    suspend fun isInQuietHours(): Boolean
    
    /**
     * Sends a celebration notification for an achievement
     * 
     * @param achievement The achievement to celebrate
     */
    suspend fun celebrateAchievement(achievement: Achievement)
    
    /**
     * Resets all gamification data (for testing or user reset)
     */
    suspend fun resetGamificationData()
}
