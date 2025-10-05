package com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalDateTime

/**
 * Result of AI analysis on a task
 */
@Serializable
internal data class TaskAnalysis(
    val taskId: String,
    val urgencyScore: Float,          // 0.0 to 1.0 (1.0 = most urgent)
    val complexityScore: Float,       // 0.0 to 1.0 (1.0 = most complex)
    val estimatedDuration: Duration,  // Total time needed
    val recommendedStartTime: LocalDateTime,
    val microTasks: List<MicroTask>,
    val analyzedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(urgencyScore in 0f..1f) { "Urgency score must be between 0.0 and 1.0" }
        require(complexityScore in 0f..1f) { "Complexity score must be between 0.0 and 1.0" }
    }
}

/**
 * A small, actionable step broken down from a larger task
 */
@Serializable
internal data class MicroTask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val parentTaskId: String,
    val title: String,
    val description: String,
    val order: Int,
    val estimatedDuration: Duration,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null
) {
    init {
        require(order >= 0) { "Order must be non-negative" }
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(description.isNotBlank()) { "Description cannot be blank" }
    }
}

/**
 * Context for generating motivational messages
 */
@Serializable
internal data class MotivationContext(
    val taskTitle: String,
    val progress: Int,                // 0-100
    val streakDays: Int,
    val timeUntilDeadline: Duration,
    val completedToday: Int,
    val totalTasksToday: Int = 0
) {
    init {
        require(progress in 0..100) { "Progress must be between 0 and 100" }
        require(streakDays >= 0) { "Streak days must be non-negative" }
        require(completedToday >= 0) { "Completed today must be non-negative" }
    }
}

/**
 * Task type for template matching
 */
internal enum class TaskType {
    DESIGN,
    CODE,
    WRITE,
    MEETING,
    RESEARCH,
    GENERIC
}

/**
 * Analysis preferences for AI
 */
@Serializable
internal data class AnalysisPreferences(
    val useCloudAi: Boolean = false,
    val maxMicroTasks: Int = 5,
    val minMicroTaskDuration: Duration = Duration.ofMinutes(15),
    val maxMicroTaskDuration: Duration = Duration.ofMinutes(45),
    val includeMotivationalTitles: Boolean = true
) {
    init {
        require(maxMicroTasks in 1..10) { "Max micro tasks must be between 1 and 10" }
        require(minMicroTaskDuration <= maxMicroTaskDuration) { 
            "Min duration must be <= max duration" 
        }
    }
}
