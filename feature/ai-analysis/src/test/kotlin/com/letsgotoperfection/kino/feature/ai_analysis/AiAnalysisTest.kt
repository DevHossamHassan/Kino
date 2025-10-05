package com.letsgotoperfection.kino.feature.ai_analysis

import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime
import android.content.Context

class AiAnalysisTest {
    
    @Test
    fun `test basic task creation`() {
        // Given
        val task = Task(
            id = "1",
            title = "Design landing page",
            description = "Create mockups for new product launch page with responsive design",
            section = TaskSection.WORK,
            column = TaskColumn.IN_PROGRESS,
            priority = Priority.HIGH,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            dueDate = LocalDateTime.now().plusDays(3)
        )
        
        // Then
        assertEquals("1", task.id)
        assertEquals("Design landing page", task.title)
        assertEquals(TaskSection.WORK, task.section)
        assertEquals(Priority.HIGH, task.priority)
    }
    
    @Test
    fun `test task with different priorities`() {
        // Given
        val highPriorityTask = Task(
            id = "2",
            title = "Urgent bug fix",
            description = "Fix critical bug in production",
            section = TaskSection.WORK,
            column = TaskColumn.IN_PROGRESS,
            priority = Priority.HIGH,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            dueDate = LocalDateTime.now().plusHours(2)
        )
        
        val lowPriorityTask = Task(
            id = "3",
            title = "Documentation update",
            description = "Update user documentation",
            section = TaskSection.WORK,
            column = TaskColumn.TODO_THIS_WEEK,
            priority = Priority.LOW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            dueDate = LocalDateTime.now().plusDays(7)
        )
        
        // Then
        assertEquals(Priority.HIGH, highPriorityTask.priority)
        assertEquals(Priority.LOW, lowPriorityTask.priority)
        assertTrue(highPriorityTask.dueDate!!.isBefore(lowPriorityTask.dueDate!!))
    }
}
