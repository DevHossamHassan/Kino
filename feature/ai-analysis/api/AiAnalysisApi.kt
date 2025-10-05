package com.letsgotoperfection.kino.feature.ai_analysis.api

import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MicroTask
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.TaskAnalysis
import java.time.LocalDateTime

/**
 * Public API for AI Analysis feature.
 * 
 * This API allows other feature modules to:
 * - Analyze tasks and break them into micro-tasks
 * - Generate motivational messages
 * - Estimate task complexity and urgency
 * 
 * @since 1.0.0
 */
interface AiAnalysisApi {
    
    /**
     * Analyzes a task and generates micro-tasks breakdown
     * 
     * @param task The task to analyze
     * @param currentTime Current timestamp for urgency calculation
     * @return Result containing TaskAnalysis or an error
     */
    suspend fun analyzeTask(
        task: Task,
        currentTime: LocalDateTime = LocalDateTime.now()
    ): Result<TaskAnalysis>
    
    /**
     * Generates micro-tasks for a given task
     * 
     * @param task The task to break down
     * @return Result containing list of micro-tasks
     */
    suspend fun generateMicroTasks(task: Task): Result<List<MicroTask>>
    
    /**
     * Estimates the complexity of a task based on its description
     * 
     * @param description Task description
     * @return Result containing complexity score (0.0-1.0)
     */
    suspend fun estimateComplexity(description: String): Result<Float>
    
    /**
     * Suggests labels for a task based on its content
     * 
     * @param description Task description
     * @return Result containing suggested labels
     */
    suspend fun suggestLabels(description: String): Result<List<String>>
    
    /**
     * Generates a motivational message for the user
     * 
     * @param taskTitle Title of the task
     * @param progress Current progress (0-100)
     * @param streakDays Current streak in days
     * @param timeUntilDeadline Time remaining until deadline
     * @param completedToday Number of tasks completed today
     * @return Result containing motivational message
     */
    suspend fun generateMotivationalMessage(
        taskTitle: String,
        progress: Int,
        streakDays: Int,
        timeUntilDeadline: java.time.Duration,
        completedToday: Int
    ): Result<String>
    
    /**
     * Checks if AI analysis is available
     * 
     * @return true if AI analysis can be performed
     */
    suspend fun isAvailable(): Boolean
}
