package com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.analyzer

import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MotivationContext
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.TaskAnalysis
import java.time.LocalDateTime

/**
 * Interface for AI-based task analysis
 */
internal interface TaskAnalyzer {
    
    /**
     * Analyze a task and generate breakdown recommendations
     * 
     * @param task The task to analyze
     * @param currentTime Current timestamp for urgency calculation
     * @return TaskAnalysis with micro-tasks and recommendations
     */
    suspend fun analyzeTask(
        task: Task,
        currentTime: LocalDateTime = LocalDateTime.now()
    ): Result<TaskAnalysis>
    
    /**
     * Generate a motivational message based on context
     * 
     * @param context Information about task progress and user streaks
     * @return Motivational message text
     */
    suspend fun generateMotivationalMessage(
        context: MotivationContext
    ): Result<String>
    
    /**
     * Check if the analyzer is available (e.g., network connectivity for cloud AI)
     * 
     * @return true if analyzer can be used
     */
    suspend fun isAvailable(): Boolean
}
