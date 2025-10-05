package com.letsgotoperfection.kino.feature.ai_analysis.internal.data.repository

import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.ai_analysis.api.AiAnalysisApi
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.analyzer.TaskAnalyzer
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MicroTask
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MotivationContext
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.TaskAnalysis
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AiAnalysisApi that delegates to the configured TaskAnalyzer
 */
@Singleton
internal class AiAnalysisApiImpl @Inject constructor(
    private val taskAnalyzer: TaskAnalyzer
) : AiAnalysisApi {
    
    override suspend fun analyzeTask(
        task: Task,
        currentTime: LocalDateTime
    ): Result<TaskAnalysis> {
        return taskAnalyzer.analyzeTask(task, currentTime)
    }
    
    override suspend fun generateMicroTasks(task: Task): Result<List<MicroTask>> {
        return taskAnalyzer.analyzeTask(task).map { it.microTasks }
    }
    
    override suspend fun estimateComplexity(description: String): Result<Float> {
        // Create a temporary task for analysis
        val tempTask = Task(
            id = "temp",
            title = "Analysis",
            description = description,
            section = com.letsgotoperfection.kino.core.model.TaskSection.PERSONAL,
            column = com.letsgotoperfection.kino.core.model.TaskColumn.TODO_THIS_WEEK,
            priority = com.letsgotoperfection.kino.core.model.Priority.MEDIUM,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            dueDate = null
        )
        
        return taskAnalyzer.analyzeTask(tempTask).map { it.complexityScore }
    }
    
    override suspend fun suggestLabels(description: String): Result<List<String>> {
        // Simple keyword-based label suggestion
        val keywords = mapOf(
            "urgent" to listOf("urgent", "asap", "immediately", "critical"),
            "work" to listOf("meeting", "project", "deadline", "client", "business"),
            "personal" to listOf("home", "family", "health", "hobby", "personal"),
            "learning" to listOf("study", "learn", "course", "tutorial", "education"),
            "creative" to listOf("design", "art", "write", "create", "draw", "paint"),
            "technical" to listOf("code", "programming", "debug", "fix", "develop", "api")
        )
        
        val lowerDescription = description.lowercase()
        val suggestedLabels = keywords.entries
            .filter { (_, words) -> words.any { lowerDescription.contains(it) } }
            .map { it.key }
            .take(3) // Limit to 3 suggestions
        
        return Result.success(suggestedLabels)
    }
    
    override suspend fun generateMotivationalMessage(
        taskTitle: String,
        progress: Int,
        streakDays: Int,
        timeUntilDeadline: Duration,
        completedToday: Int
    ): Result<String> {
        val context = MotivationContext(
            taskTitle = taskTitle,
            progress = progress,
            streakDays = streakDays,
            timeUntilDeadline = timeUntilDeadline,
            completedToday = completedToday
        )
        
        return taskAnalyzer.generateMotivationalMessage(context)
    }
    
    override suspend fun isAvailable(): Boolean {
        return taskAnalyzer.isAvailable()
    }
}
