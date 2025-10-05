package com.letsgotoperfection.kino.feature.ai_analysis.internal.ai.gemini

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.analyzer.TaskAnalyzer
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MicroTask
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MotivationContext
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.TaskAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gemini AI implementation for task analysis
 */
@Singleton
internal class GeminiTaskAnalyzer @Inject constructor(
    private val generativeModel: GenerativeModel
) : TaskAnalyzer {
    
    override suspend fun analyzeTask(
        task: Task,
        currentTime: LocalDateTime
    ): Result<TaskAnalysis> = withContext(Dispatchers.IO) {
        runCatching {
            val prompt = buildTaskAnalysisPrompt(task, currentTime)
            val response = generativeModel.generateContent(prompt)
            parseTaskAnalysisResponse(response.text ?: "", task)
        }
    }
    
    override suspend fun generateMotivationalMessage(
        context: MotivationContext
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val prompt = buildMotivationalPrompt(context)
            val response = generativeModel.generateContent(prompt)
            response.text?.trim() ?: "Keep going! You're doing great!"
        }
    }
    
    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            // Test with a simple prompt to check connectivity
            generativeModel.generateContent("Hello")
            true
        }.getOrElse { false }
    }
    
    private fun buildTaskAnalysisPrompt(
        task: Task,
        currentTime: LocalDateTime
    ): String {
        val timeUntilDue = task.dueDate?.let { 
            Duration.between(currentTime, it).toHours() 
        } ?: 168L // Default: 1 week
        
        val progress = task.progress
        val checklistProgress = if (task.checklistTotal > 0) {
            "${task.checklistCompleted}/${task.checklistTotal}"
        } else "No checklist"
        
        return """
            You are a productivity assistant helping break down tasks into manageable steps.
            
            Task Title: ${task.title}
            Task Description: ${task.description}
            Priority: ${task.priority.displayName}
            Time until deadline: $timeUntilDue hours
            Current progress: $progress%
            Checklist progress: $checklistProgress
            Labels: ${task.labels.joinToString(", ") { it.name }}
            
            Please analyze this task and provide:
            
            1. Urgency Score (0.0-1.0): How urgent is this task?
               - Consider time until deadline
               - Consider task complexity
               - Consider current progress
               - Consider priority level
            
            2. Complexity Score (0.0-1.0): How complex is this task?
               - Consider description details
               - Consider number of steps needed
               - Consider checklist size
            
            3. Estimated Duration: Total hours needed to complete
            
            4. Recommended Start Time: When should the user start? (hours from now)
            
            5. Break the task into 3-5 micro-tasks (small, actionable steps):
               - Each micro-task should take 15-45 minutes
               - Make them specific and actionable
               - Order them logically
               - Make titles fun and motivating with emojis
               - Include clear descriptions
            
            Format your response as JSON:
            {
              "urgencyScore": 0.8,
              "complexityScore": 0.6,
              "estimatedDurationHours": 4,
              "recommendedStartHours": 2,
              "microTasks": [
                {
                  "title": "🎯 Fun action-oriented title",
                  "description": "Specific instructions for this step",
                  "estimatedMinutes": 20
                }
              ]
            }
            
            Return ONLY valid JSON, no other text.
        """.trimIndent()
    }
    
    private fun buildMotivationalPrompt(context: MotivationContext): String {
        val hoursUntilDeadline = context.timeUntilDeadline.toHours()
        
        return """
            Generate a short, fun, and motivating notification message.
            
            Context:
            - Task: ${context.taskTitle}
            - Progress: ${context.progress}%
            - Current streak: ${context.streakDays} days
            - Time until deadline: $hoursUntilDeadline hours
            - Tasks completed today: ${context.completedToday}
            - Total tasks today: ${context.totalTasksToday}
            
            Requirements:
            - Maximum 2 sentences
            - Upbeat and encouraging tone
            - Use 1-2 relevant emojis
            - Make it personal and specific to the context
            - Avoid generic phrases like "good job" or "keep it up"
            - Consider the user's progress and streak
            - Be motivating but not overwhelming
            
            Examples of good messages:
            - "You're crushing it with ${context.streakDays} days! ${context.progress}% done on '${context.taskTitle}' - finish strong! 💪"
            - "Almost there! Just ${100 - context.progress}% left on '${context.taskTitle}'. You've got this! 🎯"
            - "Perfect timing to tackle '${context.taskTitle}'! Your ${context.streakDays}-day streak is impressive! 🔥"
            
            Return only the message text, nothing else.
        """.trimIndent()
    }
    
    private fun parseTaskAnalysisResponse(
        response: String,
        task: Task
    ): TaskAnalysis {
        // Remove markdown code blocks if present
        val jsonText = response
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        
        try {
            // Parse JSON manually to avoid adding kotlinx.serialization dependency
            val jsonResponse = parseJsonObject(jsonText)
            
            val urgencyScore = jsonResponse["urgencyScore"]?.toFloatOrNull()?.coerceIn(0f, 1f) ?: 0.5f
            val complexityScore = jsonResponse["complexityScore"]?.toFloatOrNull()?.coerceIn(0f, 1f) ?: 0.5f
            val durationHours = jsonResponse["estimatedDurationHours"]?.toIntOrNull() ?: 2
            val startHours = jsonResponse["recommendedStartHours"]?.toIntOrNull() ?: 1
            
            val microTasksJson = jsonResponse["microTasks"] as? List<Map<String, Any>> ?: emptyList()
            val microTasks = microTasksJson.mapIndexed { index, microTaskJson ->
                MicroTask(
                    parentTaskId = task.id,
                    title = microTaskJson["title"]?.toString() ?: "Step ${index + 1}",
                    description = microTaskJson["description"]?.toString() ?: "",
                    order = index,
                    estimatedDuration = Duration.ofMinutes(
                        microTaskJson["estimatedMinutes"]?.toString()?.toLongOrNull() ?: 20
                    )
                )
            }
            
            return TaskAnalysis(
                taskId = task.id,
                urgencyScore = urgencyScore,
                complexityScore = complexityScore,
                estimatedDuration = Duration.ofHours(durationHours.toLong()),
                recommendedStartTime = LocalDateTime.now().plusHours(startHours.toLong()),
                microTasks = microTasks
            )
        } catch (e: Exception) {
            // Fallback to basic analysis if JSON parsing fails
            return createFallbackAnalysis(task)
        }
    }
    
    private fun parseJsonObject(json: String): Map<String, Any> {
        // Simple JSON parser for our specific use case
        // In production, you might want to use a proper JSON library
        val result = mutableMapOf<String, Any>()
        
        // Extract urgencyScore
        val urgencyMatch = Regex("\"urgencyScore\"\\s*:\\s*(\\d+\\.?\\d*)").find(json)
        urgencyMatch?.let { result["urgencyScore"] = it.groupValues[1] }
        
        // Extract complexityScore
        val complexityMatch = Regex("\"complexityScore\"\\s*:\\s*(\\d+\\.?\\d*)").find(json)
        complexityMatch?.let { result["complexityScore"] = it.groupValues[1] }
        
        // Extract estimatedDurationHours
        val durationMatch = Regex("\"estimatedDurationHours\"\\s*:\\s*(\\d+)").find(json)
        durationMatch?.let { result["estimatedDurationHours"] = it.groupValues[1] }
        
        // Extract recommendedStartHours
        val startMatch = Regex("\"recommendedStartHours\"\\s*:\\s*(\\d+)").find(json)
        startMatch?.let { result["recommendedStartHours"] = it.groupValues[1] }
        
        // Extract microTasks array
        val microTasksMatch = Regex("\"microTasks\"\\s*:\\s*\\[(.*?)\\]", RegexOption.DOT_MATCHES_ALL).find(json)
        microTasksMatch?.let { match ->
            val microTasksArray = match.groupValues[1]
            val microTasks = parseMicroTasksArray(microTasksArray)
            result["microTasks"] = microTasks
        }
        
        return result
    }
    
    private fun parseMicroTasksArray(arrayJson: String): List<Map<String, Any>> {
        val microTasks = mutableListOf<Map<String, Any>>()
        
        // Split by micro-task objects
        val taskMatches = Regex("\\{[^}]+\\}").findAll(arrayJson)
        taskMatches.forEach { match ->
            val taskJson = match.value
            val task = mutableMapOf<String, Any>()
            
            // Extract title
            val titleMatch = Regex("\"title\"\\s*:\\s*\"([^\"]+)\"").find(taskJson)
            titleMatch?.let { task["title"] = it.groupValues[1] }
            
            // Extract description
            val descMatch = Regex("\"description\"\\s*:\\s*\"([^\"]+)\"").find(taskJson)
            descMatch?.let { task["description"] = it.groupValues[1] }
            
            // Extract estimatedMinutes
            val minutesMatch = Regex("\"estimatedMinutes\"\\s*:\\s*(\\d+)").find(taskJson)
            minutesMatch?.let { task["estimatedMinutes"] = it.groupValues[1] }
            
            if (task.isNotEmpty()) {
                microTasks.add(task)
            }
        }
        
        return microTasks
    }
    
    private fun createFallbackAnalysis(task: Task): TaskAnalysis {
        val urgencyScore = calculateFallbackUrgency(task)
        val complexityScore = calculateFallbackComplexity(task)
        
        val microTasks = listOf(
            MicroTask(
                parentTaskId = task.id,
                title = "🎯 Plan your approach",
                description = "Break down the task and create a strategy",
                order = 0,
                estimatedDuration = Duration.ofMinutes(15)
            ),
            MicroTask(
                parentTaskId = task.id,
                title = "🚀 Get started",
                description = "Begin working on the main part of the task",
                order = 1,
                estimatedDuration = Duration.ofMinutes(30)
            ),
            MicroTask(
                parentTaskId = task.id,
                title = "✨ Polish and finish",
                description = "Review and complete the task",
                order = 2,
                estimatedDuration = Duration.ofMinutes(20)
            )
        )
        
        return TaskAnalysis(
            taskId = task.id,
            urgencyScore = urgencyScore,
            complexityScore = complexityScore,
            estimatedDuration = Duration.ofHours(1),
            recommendedStartTime = LocalDateTime.now().plusHours(1),
            microTasks = microTasks
        )
    }
    
    private fun calculateFallbackUrgency(task: Task): Float {
        val priorityMultiplier = when (task.priority) {
            Priority.HIGH -> 0.8f
            Priority.MEDIUM -> 0.5f
            Priority.LOW -> 0.2f
        }
        
        val progressMultiplier = (100 - task.progress) / 100f
        return (priorityMultiplier + progressMultiplier) / 2f
    }
    
    private fun calculateFallbackComplexity(task: Task): Float {
        val descriptionLength = task.description.length
        val checklistSize = task.checklistTotal
        
        val lengthScore = (descriptionLength / 500f).coerceIn(0f, 1f)
        val checklistScore = (checklistSize / 10f).coerceIn(0f, 1f)
        
        return (lengthScore + checklistScore) / 2f
    }
}
