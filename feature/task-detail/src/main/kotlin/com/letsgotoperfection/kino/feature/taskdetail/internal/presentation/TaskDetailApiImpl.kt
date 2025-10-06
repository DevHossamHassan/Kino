package com.letsgotoperfection.kino.feature.taskdetail.internal.presentation

import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.Attachment
import com.letsgotoperfection.kino.feature.taskdetail.api.TaskDetailApi
import com.letsgotoperfection.kino.feature.taskdetail.api.TaskStatus
import com.letsgotoperfection.kino.feature.taskdetail.api.TaskComment
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDetailApiImpl @Inject constructor() : TaskDetailApi {

    override suspend fun getTask(taskId: String): Result<Task> {
        // Mock implementation - returns sample task data
        val mockTask = createMockTask(taskId)
        return Result.Success(mockTask)
    }

    override fun observeTask(taskId: String) = 
        flowOf(createMockTask(taskId))

    override suspend fun updateTask(
        taskId: String,
        title: String?,
        description: String?,
        dueDate: String?
    ): Result<Unit> {
        // Mock implementation - simulate successful update
        return Result.Success(Unit)
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Unit> {
        // Mock implementation - simulate successful status update
        return Result.Success(Unit)
    }

    override suspend fun assignTask(taskId: String, userId: String): Result<Unit> {
        // Mock implementation - simulate successful assignment
        return Result.Success(Unit)
    }

    override suspend fun unassignTask(taskId: String): Result<Unit> {
        // Mock implementation - simulate successful unassignment
        return Result.Success(Unit)
    }

    override suspend fun addComment(taskId: String, comment: String): Result<Unit> {
        // Mock implementation - simulate successful comment addition
        return Result.Success(Unit)
    }

    override fun getTaskComments(taskId: String) = 
        flowOf(emptyList<TaskComment>())

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        // Mock implementation - simulate successful deletion
        return Result.Success(Unit)
    }
    
    private fun createMockTask(taskId: String): Task {
        val now = LocalDateTime.now()
        return Task(
            id = taskId,
            title = "Sample Task: $taskId",
            description = "This is a sample task description for demonstration purposes. It shows how the task detail screen displays task information.",
            section = TaskSection.PERSONAL,
            column = TaskColumn.TODO_THIS_WEEK,
            priority = Priority.HIGH,
            createdAt = now.minusDays(5),
            updatedAt = now.minusHours(2),
            dueDate = now.plusDays(3),
            progress = 33, // 1 of 3 checklist items completed
            labels = listOf(
                Label(id = "label1", name = "Important", color = "#FF5722"),
                Label(id = "label2", name = "Work", color = "#2196F3")
            ),
            checklist = listOf(
                ChecklistItem(
                    id = "item1", 
                    taskId = taskId,
                    text = "Review requirements", 
                    isCompleted = true, 
                    order = 1,
                    createdAt = now.minusDays(4)
                ),
                ChecklistItem(
                    id = "item2", 
                    taskId = taskId,
                    text = "Create wireframes", 
                    isCompleted = false, 
                    order = 2,
                    createdAt = now.minusDays(3)
                ),
                ChecklistItem(
                    id = "item3", 
                    taskId = taskId,
                    text = "Implement UI", 
                    isCompleted = false, 
                    order = 3,
                    createdAt = now.minusDays(2)
                )
            ),
            attachments = listOf(
                Attachment(
                    id = "attach1", 
                    targetId = taskId,
                    targetType = "task",
                    uri = "file:///path/to/requirements.pdf",
                    filename = "requirements.pdf", 
                    mimeType = "application/pdf",
                    size = 1024000L,
                    addedAt = now.minusDays(3)
                ),
                Attachment(
                    id = "attach2", 
                    targetId = taskId,
                    targetType = "task",
                    uri = "file:///path/to/design.png",
                    filename = "design.png", 
                    mimeType = "image/png",
                    size = 512000L,
                    addedAt = now.minusDays(2)
                )
            )
        )
    }
}
