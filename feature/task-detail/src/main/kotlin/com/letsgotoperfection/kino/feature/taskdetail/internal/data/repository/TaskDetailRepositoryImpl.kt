package com.letsgotoperfection.kino.feature.taskdetail.internal.data.repository

import android.content.Context
import com.letsgotoperfection.kino.core.database.dao.AttachmentDao
import com.letsgotoperfection.kino.core.database.dao.ChecklistDao
import com.letsgotoperfection.kino.core.database.dao.LabelDao
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.mapper.toDomain
import com.letsgotoperfection.kino.core.database.mapper.toEntity
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model.TaskDetail
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository.TaskDetailRepository
import com.letsgotoperfection.kino.feature.taskdetail.internal.data.mapper.toTaskDetail
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
class TaskDetailRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val checklistDao: ChecklistDao,
    private val attachmentDao: AttachmentDao,
    private val labelDao: LabelDao,
    private val context: Context
) : TaskDetailRepository {
    
    override fun getTaskDetail(taskId: String): Flow<TaskDetail> {
        return combine(
            taskDao.observeTaskById(taskId),
            checklistDao.getChecklistItems(taskId),
            attachmentDao.getAttachments(taskId, context.getString(R.string.attachment_type_task)),
            labelDao.getTaskLabels(taskId)
        ) { taskEntity, checklist, attachments, labels ->
            taskEntity?.toTaskDetail(
                checklist = checklist.map { it.toDomain() },
                attachments = attachments.map { it.toDomain() },
                labels = labels.map { it.toDomain() }
            ) ?: throw TaskNotFoundException(context.getString(R.string.error_task_not_found, taskId))
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
                ?: throw ChecklistItemNotFoundException(context.getString(R.string.error_checklist_item_not_found, itemId))
            
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
                ?: throw ChecklistItemNotFoundException(context.getString(R.string.error_checklist_item_not_found, itemId))
            
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
        dueDateExplicit: Boolean,
        labels: List<Label>?,
        section: TaskSection?,
        column: TaskColumn?
    ) {
        withContext(Dispatchers.IO) {
            val task = taskDao.getTaskById(taskId)
                ?: throw TaskNotFoundException(context.getString(R.string.error_task_not_found, taskId))
            
            val updatedTitle = title ?: task.title
            val updatedDescription = description ?: task.description
            val updatedPriority = priority?.name?.lowercase() ?: task.priority
            val updatedDueDate = when {
                dueDateExplicit && dueDate == null -> null
                dueDate != null -> dueDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                else -> task.dueDate
            }
            val updatedAt = System.currentTimeMillis()
            
            taskDao.updateTaskDetails(
                taskId = taskId,
                title = updatedTitle,
                description = updatedDescription,
                priority = updatedPriority,
                dueDate = updatedDueDate,
                updatedAt = updatedAt
            )

            section?.let { newSection ->
                taskDao.updateSection(taskId, newSection.name.lowercase())
            }

            column?.let { newColumn ->
                taskDao.updateColumn(taskId, newColumn.name.lowercase())
            }
            
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

    override suspend fun deleteTask(taskId: String) {
        withContext(Dispatchers.IO) {
            val task = taskDao.getTaskById(taskId)
                ?: throw TaskNotFoundException("Task not found: $taskId")

            attachmentDao.deleteAllForTarget(taskId, "task")
            checklistDao.deleteAllForTask(taskId)
            labelDao.removeAllTaskLabels(taskId)
            taskDao.deleteTask(task)
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
