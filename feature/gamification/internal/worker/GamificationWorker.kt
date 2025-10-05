package com.letsgotoperfection.kino.feature.gamification.internal.worker

import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.feature.ai_analysis.api.AiAnalysisApi
import com.letsgotoperfection.kino.feature.gamification.api.GamificationApi
import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import com.letsgotoperfection.kino.feature.notifications.api.NotificationRequest
import com.letsgotoperfection.kino.feature.notifications.api.NotificationType
import com.letsgotoperfection.kino.feature.notifications.api.NotificationPriority
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Background worker that analyzes tasks and schedules smart notifications
 */
@HiltWorker
internal class GamificationWorker @AssistedInject constructor(
    @Assisted appContext: android.content.Context,
    @Assisted workerParams: WorkerParameters,
    private val aiAnalysisApi: AiAnalysisApi,
    private val gamificationApi: GamificationApi,
    private val notificationApi: NotificationApi,
    private val kanbanRepository: com.letsgotoperfection.kino.core.data.repository.KanbanRepository
) : CoroutineWorker(appContext, workerParams) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            analyzeAndScheduleNotifications()
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("GamificationWorker", "Error in background analysis", e)
            Result.retry()
        }
    }
    
    private suspend fun analyzeAndScheduleNotifications() {
        val currentTime = LocalDateTime.now()
        val upcomingDeadline = currentTime.plusDays(7)
        
        // Get tasks with upcoming deadlines
        val upcomingTasks = getUpcomingTasks(currentTime, upcomingDeadline)
        
        upcomingTasks.forEach { task ->
            analyzeAndScheduleForTask(task, currentTime)
        }
    }
    
    private suspend fun getUpcomingTasks(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Task> {
        // This would typically come from the kanban repository
        // For now, we'll return an empty list as a placeholder
        return emptyList()
    }
    
    private suspend fun analyzeAndScheduleForTask(
        task: Task,
        currentTime: LocalDateTime
    ) {
        // Check if AI analysis is available
        if (!aiAnalysisApi.isAvailable()) {
            return
        }
        
        // Check if already analyzed recently
        val lastAnalysis = getLastAnalysisTime(task.id)
        if (lastAnalysis != null && 
            java.time.Duration.between(lastAnalysis, currentTime).toHours() < 12) {
            return // Skip if analyzed in last 12 hours
        }
        
        // Analyze task with AI
        val analysis = aiAnalysisApi.analyzeTask(task, currentTime)
            .getOrNull() ?: return
        
        // Save analysis timestamp
        saveAnalysisTime(task.id, currentTime)
        
        // Schedule micro-task notifications
        scheduleMicroTaskNotifications(task, analysis)
        
        // Schedule motivational notifications
        scheduleMotivationalNotifications(task, analysis)
    }
    
    private suspend fun scheduleMicroTaskNotifications(
        task: Task,
        analysis: com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.TaskAnalysis
    ) {
        val currentTime = LocalDateTime.now()
        var scheduledTime = analysis.recommendedStartTime
        
        analysis.microTasks.forEachIndexed { index, microTask ->
            // Schedule notification for each micro-task
            gamificationApi.scheduleSmartReminders(task)
            
            // Next notification after estimated duration + buffer
            scheduledTime = scheduledTime
                .plus(microTask.estimatedDuration)
                .plusMinutes(30) // 30-minute buffer
        }
    }
    
    private suspend fun scheduleMotivationalNotifications(
        task: Task,
        analysis: com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.TaskAnalysis
    ) {
        val currentTime = LocalDateTime.now()
        val timeUntilDue = task.dueDate?.let { 
            java.time.Duration.between(currentTime, it) 
        } ?: java.time.Duration.ofDays(7)
        
        // Schedule encouragement notifications based on urgency
        val notificationTimes = when {
            timeUntilDue.toHours() < 24 -> {
                // Urgent: Notify every 4 hours
                listOf(
                    currentTime.plusHours(4),
                    currentTime.plusHours(8),
                    currentTime.plusHours(12)
                )
            }
            timeUntilDue.toHours() < 72 -> {
                // Medium urgency: Notify twice a day
                listOf(
                    currentTime.plusHours(8),
                    currentTime.plusHours(20)
                )
            }
            else -> {
                // Low urgency: Daily reminder
                listOf(currentTime.plusDays(1))
            }
        }
        
        notificationTimes.forEach { scheduledTime ->
            val context = com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MotivationContext(
                taskTitle = task.title,
                progress = task.progress,
                streakDays = gamificationApi.getCurrentStreak().currentStreak,
                timeUntilDeadline = java.time.Duration.between(scheduledTime, task.dueDate ?: scheduledTime),
                completedToday = getCompletedToday()
            )
            
            val message = aiAnalysisApi.generateMotivationalMessage(
                taskTitle = context.taskTitle,
                progress = context.progress,
                streakDays = context.streakDays,
                timeUntilDeadline = context.timeUntilDeadline,
                completedToday = context.completedToday
            ).getOrNull() ?: "Keep going! You're doing great!"
            
            // Schedule the motivational notification using the notifications module
            val notificationRequest = NotificationRequest(
                id = "motivation_${task.id}_${scheduledTime.toEpochSecond(java.time.ZoneOffset.UTC)}",
                title = "Stay on track!",
                message = message,
                scheduledTime = scheduledTime.toEpochSecond(java.time.ZoneOffset.UTC) * 1000,
                type = NotificationType.TASK_REMINDER,
                priority = NotificationPriority.NORMAL
            )
            
            notificationApi.scheduleNotification(notificationRequest)
        }
    }
    
    private suspend fun getLastAnalysisTime(taskId: String): LocalDateTime? {
        // This would typically come from a local database
        // For now, return null to always analyze
        return null
    }
    
    private suspend fun saveAnalysisTime(taskId: String, time: LocalDateTime) {
        // This would typically save to a local database
        // For now, do nothing
    }
    
    private suspend fun getCompletedToday(): Int {
        val today = java.time.LocalDate.now()
        // This would typically come from the kanban repository
        return 0
    }
    
    companion object {
        const val WORK_NAME = "gamification_worker"
        
        fun schedulePeriodicWork(workManager: WorkManager) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // For cloud AI
                .setRequiresBatteryNotLow(true)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<GamificationWorker>(
                repeatInterval = 12,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.EXPONENTIAL,
                    15,
                    TimeUnit.MINUTES
                )
                .build()
            
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
