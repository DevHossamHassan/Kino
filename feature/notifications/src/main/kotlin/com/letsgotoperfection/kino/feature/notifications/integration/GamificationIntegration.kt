package com.letsgotoperfection.kino.feature.notifications.integration

import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import javax.inject.Inject

/**
 * Integration example for Gamification feature
 */
class GamificationIntegration @Inject constructor(
    private val notificationApi: NotificationApi
) {
    
    /**
     * Send achievement unlocked notification
     */
    suspend fun sendAchievementNotification(
        achievementTitle: String,
        description: String
    ) {
        notificationApi.sendAchievementNotification(
            achievementTitle = achievementTitle,
            message = description
        )
    }
    
    /**
     * Send streak reminder notification
     */
    suspend fun sendStreakReminderNotification(
        currentStreak: Int,
        streakType: String
    ) {
        notificationApi.sendNotification(
            title = "Keep Your Streak Going! 🔥",
            message = "You're on a $currentStreak day $streakType streak. Don't break it!",
            category = com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory.STREAK_REMINDER,
            deepLink = "kino://achievements"
        )
    }
    
    /**
     * Send progress update notification
     */
    suspend fun sendProgressUpdateNotification(
        progressPercentage: Int,
        goalTitle: String
    ) {
        notificationApi.sendNotification(
            title = "Progress Update",
            message = "You're $progressPercentage% complete with: $goalTitle",
            category = com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory.PROGRESS_UPDATE,
            deepLink = "kino://progress"
        )
    }
}
