package com.letsgotoperfection.kino.data

import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.entity.TaskEntity
import com.letsgotoperfection.kino.core.database.mapper.toDomain
import com.letsgotoperfection.kino.core.database.mapper.toEntity
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.Priority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getTasksBySection(section: TaskSection): Flow<List<Task>> {
        return taskDao.getTasksBySection(section.name.lowercase()).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getTasksByColumn(column: TaskColumn): Flow<List<Task>> {
        return taskDao.getTasksByColumn(column.name.lowercase()).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }
    
    fun observeTaskById(taskId: String): Flow<Task?> {
        return taskDao.observeTaskById(taskId).map { entity ->
            entity?.toDomain()
        }
    }
    
    suspend fun createTask(
        title: String,
        description: String = "",
        section: TaskSection = TaskSection.PERSONAL,
        column: TaskColumn = TaskColumn.TODO_THIS_WEEK,
        priority: Priority = Priority.MEDIUM,
        dueDate: LocalDateTime? = null,
        progress: Int = 0
    ): Task {
        val now = LocalDateTime.now()
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            section = section,
            column = column,
            priority = priority,
            createdAt = now,
            updatedAt = now,
            dueDate = dueDate,
            progress = progress
        )
        
        taskDao.upsertTask(task.toEntity())
        return task
    }
    
    suspend fun updateTask(task: Task) {
        taskDao.upsertTask(task.toEntity())
    }
    
    suspend fun deleteTask(taskId: String) {
        taskDao.getTaskById(taskId)?.let { entity ->
            taskDao.deleteTask(entity)
        }
    }
    
    suspend fun updateTaskProgress(taskId: String, progress: Int) {
        taskDao.updateProgress(taskId, progress)
    }
    
    suspend fun updateTaskColumn(taskId: String, column: TaskColumn) {
        taskDao.updateColumn(taskId, column.name.lowercase())
    }
    
    suspend fun updateTaskSection(taskId: String, section: TaskSection) {
        taskDao.updateSection(taskId, section.name.lowercase())
    }
}





