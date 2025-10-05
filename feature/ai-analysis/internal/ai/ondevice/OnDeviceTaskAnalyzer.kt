package com.letsgotoperfection.kino.feature.ai_analysis.internal.ai.ondevice

import android.content.Context
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.analyzer.TaskAnalyzer
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MicroTask
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MotivationContext
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.TaskAnalysis
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.TaskType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device ML implementation for task analysis using templates and heuristics
 */
@Singleton
internal class OnDeviceTaskAnalyzer @Inject constructor(
    @androidx.annotation.RecentlyNonNull private val context: Context
) : TaskAnalyzer {
    
    private val microTaskTemplates = loadMicroTaskTemplates()
    private val motivationalMessages = MotivationalMessages()
    
    override suspend fun analyzeTask(
        task: Task,
        currentTime: LocalDateTime
    ): Result<TaskAnalysis> = withContext(Dispatchers.Default) {
        runCatching {
            // Calculate urgency using heuristics
            val urgencyScore = calculateUrgency(task, currentTime)
            
            // Estimate complexity using text analysis
            val complexityScore = estimateComplexity(task)
            
            // Generate micro-tasks using template matching
            val microTasks = generateMicroTasksFromTemplates(task, complexityScore)
            
            // Calculate recommended start time
            val estimatedDuration = microTasks.sumOf { it.estimatedDuration.toMinutes() }
            val timeUntilDue = task.dueDate?.let { 
                Duration.between(currentTime, it).toMinutes() 
            } ?: 1440L // Default: 1 day
            
            val bufferTime = (estimatedDuration * 1.5).toLong() // 50% buffer
            val recommendedStart = task.dueDate?.minusMinutes(bufferTime) 
                ?: currentTime.plusHours(1)
            
            TaskAnalysis(
                taskId = task.id,
                urgencyScore = urgencyScore,
                complexityScore = complexityScore,
                estimatedDuration = Duration.ofMinutes(estimatedDuration),
                recommendedStartTime = recommendedStart,
                microTasks = microTasks
            )
        }
    }
    
    override suspend fun generateMotivationalMessage(
        context: MotivationContext
    ): Result<String> = withContext(Dispatchers.Default) {
        runCatching {
            motivationalMessages.get(context)
        }
    }
    
    override suspend fun isAvailable(): Boolean = true // Always available offline
    
    private fun calculateUrgency(task: Task, currentTime: LocalDateTime): Float {
        val timeUntilDue = task.dueDate?.let { 
            Duration.between(currentTime, it).toHours().toFloat()
        } ?: 168f // Default: 1 week
        
        val progress = task.progress / 100f
        val priorityMultiplier = when (task.priority) {
            Priority.HIGH -> 0.8f
            Priority.MEDIUM -> 0.5f
            Priority.LOW -> 0.2f
        }
        
        // Normalize time urgency (closer to deadline = higher urgency)
        val timeUrgency = when {
            timeUntilDue <= 24 -> 1.0f
            timeUntilDue <= 72 -> 0.8f
            timeUntilDue <= 168 -> 0.6f
            else -> 0.3f
        }
        
        // Progress urgency (less progress = higher urgency)
        val progressUrgency = 1.0f - progress
        
        // Checklist urgency
        val checklistUrgency = if (task.checklistTotal > 0) {
            (task.checklistTotal - task.checklistCompleted).toFloat() / task.checklistTotal
        } else 0.5f
        
        return (timeUrgency * 0.4f + progressUrgency * 0.3f + 
                priorityMultiplier * 0.2f + checklistUrgency * 0.1f)
            .coerceIn(0f, 1f)
    }
    
    private fun estimateComplexity(task: Task): Float {
        val descriptionLength = task.description.length
        val wordCount = task.description.split("\\s+".toRegex()).size
        val checklistSize = task.checklistTotal
        val labelCount = task.labels.size
        
        // Length complexity
        val lengthScore = (descriptionLength / 500f).coerceIn(0f, 1f)
        
        // Word complexity
        val wordScore = (wordCount / 100f).coerceIn(0f, 1f)
        
        // Checklist complexity
        val checklistScore = (checklistSize / 10f).coerceIn(0f, 1f)
        
        // Label complexity (more labels = more complex)
        val labelScore = (labelCount / 5f).coerceIn(0f, 1f)
        
        return (lengthScore * 0.3f + wordScore * 0.3f + 
                checklistScore * 0.3f + labelScore * 0.1f)
            .coerceIn(0f, 1f)
    }
    
    private fun generateMicroTasksFromTemplates(
        task: Task,
        complexityScore: Float
    ): List<MicroTask> {
        // Determine number of micro-tasks based on complexity
        val microTaskCount = when {
            complexityScore > 0.7f -> 5
            complexityScore > 0.4f -> 4
            else -> 3
        }
        
        // Match task type to templates using keywords
        val taskType = identifyTaskType(task.description)
        val templates = microTaskTemplates[taskType] ?: microTaskTemplates[TaskType.GENERIC]!!
        
        return templates.take(microTaskCount).mapIndexed { index, template ->
            MicroTask(
                parentTaskId = task.id,
                title = template.title.replace("{task}", task.title),
                description = template.description,
                order = index,
                estimatedDuration = template.estimatedDuration
            )
        }
    }
    
    private fun identifyTaskType(description: String): TaskType {
        val keywords = mapOf(
            TaskType.DESIGN to listOf("design", "mockup", "prototype", "ui", "ux", "wireframe", "sketch"),
            TaskType.CODE to listOf("code", "implement", "develop", "program", "bug", "fix", "feature", "api"),
            TaskType.WRITE to listOf("write", "document", "article", "blog", "report", "content", "draft"),
            TaskType.MEETING to listOf("meeting", "call", "discussion", "presentation", "demo", "review"),
            TaskType.RESEARCH to listOf("research", "analyze", "investigate", "study", "explore", "find")
        )
        
        val lowerDescription = description.lowercase()
        
        return keywords.entries
            .firstOrNull { (_, words) -> words.any { lowerDescription.contains(it) } }
            ?.key ?: TaskType.GENERIC
    }
    
    private fun loadMicroTaskTemplates(): Map<TaskType, List<MicroTaskTemplate>> {
        return mapOf(
            TaskType.DESIGN to listOf(
                MicroTaskTemplate("🎨 Gather inspiration for {task}", "Browse design galleries and collect references", Duration.ofMinutes(15)),
                MicroTaskTemplate("✏️ Sketch initial concepts", "Create rough sketches of main ideas", Duration.ofMinutes(20)),
                MicroTaskTemplate("🖼️ Create wireframes", "Build low-fidelity wireframes", Duration.ofMinutes(30)),
                MicroTaskTemplate("🎯 Design high-fidelity mockups", "Create detailed, polished designs", Duration.ofMinutes(45)),
                MicroTaskTemplate("👥 Get feedback and iterate", "Share with team and make improvements", Duration.ofMinutes(20))
            ),
            TaskType.CODE to listOf(
                MicroTaskTemplate("📋 Break down requirements for {task}", "List all features and edge cases", Duration.ofMinutes(15)),
                MicroTaskTemplate("🏗️ Set up project structure", "Create files and basic setup", Duration.ofMinutes(20)),
                MicroTaskTemplate("⚙️ Implement core functionality", "Build main features", Duration.ofMinutes(45)),
                MicroTaskTemplate("✅ Add tests", "Write unit and integration tests", Duration.ofMinutes(30)),
                MicroTaskTemplate("🔍 Code review and refactor", "Clean up and optimize code", Duration.ofMinutes(25))
            ),
            TaskType.WRITE to listOf(
                MicroTaskTemplate("📚 Research and gather sources", "Collect information and references", Duration.ofMinutes(25)),
                MicroTaskTemplate("📝 Create outline", "Structure main points and flow", Duration.ofMinutes(15)),
                MicroTaskTemplate("✍️ Write first draft", "Get ideas down without editing", Duration.ofMinutes(40)),
                MicroTaskTemplate("🔧 Edit and refine", "Improve clarity and flow", Duration.ofMinutes(30)),
                MicroTaskTemplate("👀 Final proofread", "Check for errors and polish", Duration.ofMinutes(15))
            ),
            TaskType.MEETING to listOf(
                MicroTaskTemplate("📅 Prepare agenda for {task}", "Outline topics and objectives", Duration.ofMinutes(10)),
                MicroTaskTemplate("📊 Gather materials", "Collect documents and data needed", Duration.ofMinutes(15)),
                MicroTaskTemplate("🎯 Conduct the meeting", "Lead discussion and take notes", Duration.ofMinutes(60)),
                MicroTaskTemplate("📝 Follow up", "Send summary and action items", Duration.ofMinutes(20))
            ),
            TaskType.RESEARCH to listOf(
                MicroTaskTemplate("🔍 Define research scope for {task}", "Clarify what you need to find out", Duration.ofMinutes(10)),
                MicroTaskTemplate("📚 Gather initial sources", "Find relevant articles and data", Duration.ofMinutes(30)),
                MicroTaskTemplate("📊 Analyze findings", "Review and synthesize information", Duration.ofMinutes(40)),
                MicroTaskTemplate("📝 Document results", "Write up findings and conclusions", Duration.ofMinutes(25))
            ),
            TaskType.GENERIC to listOf(
                MicroTaskTemplate("🎯 Plan your approach to {task}", "Break it down and strategize", Duration.ofMinutes(15)),
                MicroTaskTemplate("🚀 Get started on the first step", "Begin with the easiest part", Duration.ofMinutes(25)),
                MicroTaskTemplate("⚡ Power through the main work", "Focus on core completion", Duration.ofMinutes(30)),
                MicroTaskTemplate("✨ Polish and finalize", "Add finishing touches", Duration.ofMinutes(20)),
                MicroTaskTemplate("🎉 Review and celebrate", "Check your work and enjoy the win!", Duration.ofMinutes(10))
            )
        )
    }
    
    private data class MicroTaskTemplate(
        val title: String,
        val description: String,
        val estimatedDuration: Duration
    )
    
    private class MotivationalMessages {
        fun get(context: MotivationContext): String {
            return when {
                // High streak, approaching deadline
                context.streakDays >= 7 && context.timeUntilDeadline.toHours() < 24 -> listOf(
                    "🔥 ${context.streakDays}-day streak! Let's keep it alive and finish \"${context.taskTitle}\"!",
                    "You're on fire with ${context.streakDays} days! One more push for \"${context.taskTitle}\"!",
                    "Legend mode activated! ${context.streakDays} days strong. Finish this and keep going!"
                ).random()
                
                // Good progress
                context.progress >= 75 -> listOf(
                    "Almost there! ${context.progress}% complete on \"${context.taskTitle}\". Finish strong! 💪",
                    "You're crushing it! ${context.progress}% done. The finish line is in sight!",
                    "${context.progress}% complete! You've got this. One final push! 🎯"
                ).random()
                
                // Just started
                context.progress < 25 && context.timeUntilDeadline.toHours() > 48 -> listOf(
                    "Perfect timing to start \"${context.taskTitle}\"! Let's do this! 🚀",
                    "Great momentum today! Time to tackle \"${context.taskTitle}\".",
                    "You're on a roll with ${context.completedToday} tasks done. Keep going!"
                ).random()
                
                // Deadline approaching, low progress
                context.timeUntilDeadline.toHours() < 24 && context.progress < 50 -> listOf(
                    "Quick heads up! \"${context.taskTitle}\" needs attention soon. You got this! ⏰",
                    "Time to focus! \"${context.taskTitle}\" is due soon. Let's make progress!",
                    "Deadline approaching! Break \"${context.taskTitle}\" into small steps and start now."
                ).random()
                
                // Mid-progress, plenty of time
                else -> listOf(
                    "Great progress on \"${context.taskTitle}\"! Keep the momentum going! ✨",
                    "You're doing amazing! ${context.progress}% done on \"${context.taskTitle}\".",
                    "Steady wins the race! \"${context.taskTitle}\" is coming along nicely.",
                    "Nice work! ${context.completedToday} tasks done today. You're productive! 🎉"
                ).random()
            }
        }
    }
}
