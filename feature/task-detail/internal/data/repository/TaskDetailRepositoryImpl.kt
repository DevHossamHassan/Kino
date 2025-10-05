package com.letsgotoperfection.kino.feature.taskdetail.internal.data.repository

import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.database.dao.AttachmentDao
import com.letsgotoperfection.kino.core.database.dao.ChecklistDao
import com.letsgotoperfection.kino.core.database.dao.LabelDao
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.mapper.toDomain
import com.letsgotoperfection.kino.core.database.mapper.toDomainDetail
import com.letsgotoperfection.kino.core.database.mapper.toEntity
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model.TaskDetail
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository.TaskDetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TaskDetailRepository.
 * Handles data operations for task detail functionality.
 */
@Singleton
internal class TaskDetailRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val checklistDao: ChecklistDao,
    private val attachmentDao: AttachmentDao,
    private val labelDao: LabelDao
) : TaskDetailRepository {
    
    override fun getTaskDetail(taskId: String): Flow<TaskDetail> {
        return combine(
            taskDao.observeTaskById(taskId),
            checklistDao.getChecklistItems(taskId),
            attachmentDao.getAttachments(taskId, "task"),
            labelDao.getTaskLabels(taskId)
        ) { taskEntity, checklist, attachments, labels ->
            taskEntity?.toDomainDetail(
                checklist = checklist.map { it.toDomain() },
                attachments = attachments.map { it.toDomain() },
                labels = labels.map { it.toDomain() }
            ) ?: throw TaskNotFoundException("Task not found: $taskId")
        }.flowOn(Dispatchers.IO)
    }
    
    override fun getChecklist(taskId: String): Flow<List<ChecklistItem>> {
        return checklistDao.getChecklistItems(taskId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }
    
    override suspend fun toggleChecklistItem(itemId: String) {
        withContext(Dispatchers.IO) {
            val item = checklistDao.getById(itemId)
                ?: throw ChecklistItemNotFoundException("Item not found: $itemId")
            
            checklistDao.updateCompletion(itemId, !item.isCompleted)
        }
    }
    
    override suspend fun addChecklistItem(item: ChecklistItem) {
        withContext(Dispatchers.IO) {
            checklistDao.upsertChecklistItem(item.toEntity())
        }
    }
    
    override suspend fun deleteChecklistItem(itemId: String) {
        withContext(Dispatchers.IO) {
            val item = checklistDao.getById(itemId)
                ?: throw ChecklistItemNotFoundException("Item not found: $itemId")
            
            checklistDao.deleteChecklistItem(item)
        }
    }
    
    override suspend fun updateProgress(taskId: String, progress: Int) {
        withContext(Dispatchers.IO) {
            taskDao.updateProgress(taskId, progress.coerceIn(0, 100))
        }
    }
    
    override fun getChecklistCount(taskId: String): Flow<Int> {
        return checklistDao.getChecklistItems(taskId)
            .map { it.size }
            .flowOn(Dispatchers.IO)
    }
    
    override suspend fun updateTask(
        taskId: String,
        title: String?,
        description: String?,
        priority: Priority?,
        dueDate: LocalDateTime?,
        labels: List<Label>?
    ) {
        withContext(Dispatchers.IO) {
            val task = taskDao.getTaskById(taskId)
                ?: throw TaskNotFoundException("Task not found: $taskId")
            
            val updatedTitle = title ?: task.title
            val updatedDescription = description ?: task.description
            val updatedPriority = priority?.name?.lowercase() ?: task.priority
            val updatedDueDate = dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: task.dueDate
            val updatedAt = System.currentTimeMillis()
            
            taskDao.updateTaskDetails(
                taskId = taskId,
                title = updatedTitle,
                description = updatedDescription,
                priority = updatedPriority,
                dueDate = updatedDueDate,
                updatedAt = updatedAt
            )
            
            // Update labels if provided
            labels?.let { newLabels ->
                // Remove existing labels
                val existingLabels = labelDao.getTaskLabels(taskId).first()
                existingLabels.forEach { label ->
                    labelDao.removeTaskLabel(
                        com.letsgotoperfection.kino.core.database.entity.TaskLabelCrossRef(taskId, label.id)
                    )
                }
                
                // Add new labels
                newLabels.forEach { label ->
                    labelDao.addTaskLabel(
                        com.letsgotoperfection.kino.core.database.entity.TaskLabelCrossRef(taskId, label.id)
                    )
                }
            }
        }
    }
}

/**
 * Exception thrown when a task is not found.
 */
internal class TaskNotFoundException(message: String) : Exception(message)

/**
 * Exception thrown when a checklist item is not found.
 */
internal class ChecklistItemNotFoundException(message: String) : Exception(message)
