package com.letsgotoperfection.kino.data

import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.dao.LabelDao
import com.letsgotoperfection.kino.core.database.entity.TaskEntity
import com.letsgotoperfection.kino.core.database.entity.TaskLabelCrossRef
import com.letsgotoperfection.kino.core.database.mapper.toDomain
import com.letsgotoperfection.kino.core.database.mapper.toEntity
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.Label
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val labelDao: LabelDao
) {
    
    /**
     * Get all tasks with their labels.
     * 
     * PERFORMANCE: Uses @Transaction and @Relation to eliminate N+1 query problem.
     * Single query instead of 1 + N queries.
     */
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasksWithLabels().map { tasksWithLabels ->
            tasksWithLabels.map { taskWithLabels ->
                taskWithLabels.task.toDomain(
                    labels = taskWithLabels.labels.map { it.toDomain() }
                )
            }
        }
    }
    
    /**
     * Get tasks by section with their labels.
     * 
     * PERFORMANCE: Uses @Transaction and @Relation to eliminate N+1 query problem.
     */
    fun getTasksBySection(section: TaskSection): Flow<List<Task>> {
        return taskDao.getTasksWithLabelsBySection(section.name.lowercase()).map { tasksWithLabels ->
            tasksWithLabels.map { taskWithLabels ->
                taskWithLabels.task.toDomain(
                    labels = taskWithLabels.labels.map { it.toDomain() }
                )
            }
        }
    }
    
    /**
     * Get tasks by column with their labels.
     * 
     * PERFORMANCE: Uses @Transaction and @Relation to eliminate N+1 query problem.
     */
    fun getTasksByColumn(column: TaskColumn): Flow<List<Task>> {
        return taskDao.getTasksWithLabelsByColumn(column.name.lowercase()).map { tasksWithLabels ->
            tasksWithLabels.map { taskWithLabels ->
                taskWithLabels.task.toDomain(
                    labels = taskWithLabels.labels.map { it.toDomain() }
                )
            }
        }
    }
    
    /**
     * Get a single task by ID with its labels.
     * 
     * PERFORMANCE: Uses @Transaction to fetch task and labels in single query.
     */
    suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskWithLabelsById(taskId)?.let { taskWithLabels ->
            taskWithLabels.task.toDomain(
                labels = taskWithLabels.labels.map { it.toDomain() }
            )
        }
    }
    
    /**
     * Observe a task by ID with its labels.
     * 
     * PERFORMANCE: Uses @Transaction to fetch task and labels in single query.
     */
    fun observeTaskById(taskId: String): Flow<Task?> {
        return taskDao.observeTaskWithLabelsById(taskId).map { taskWithLabels ->
            taskWithLabels?.let {
                it.task.toDomain(labels = it.labels.map { label -> label.toDomain() })
            }
        }
    }
    
    suspend fun createTask(
        title: String,
        description: String = "",
        section: TaskSection = TaskSection.PERSONAL,
        column: TaskColumn = TaskColumn.TODO_THIS_WEEK,
        priority: Priority = Priority.MEDIUM,
        dueDate: LocalDateTime? = null,
        progress: Int = 0,
        labels: List<Label> = emptyList()
    ): Task {
        val now = LocalDateTime.now()
        val taskId = UUID.randomUUID().toString()
        val task = Task(
            id = taskId,
            title = title,
            description = description,
            section = section,
            column = column,
            priority = priority,
            createdAt = now,
            updatedAt = now,
            dueDate = dueDate,
            progress = progress,
            labels = labels
        )
        
        // Save the task
        taskDao.upsertTask(task.toEntity())
        
        // Save labels and their relationships
        labels.forEach { label ->
            // Save the label if it doesn't exist
            labelDao.upsertLabel(label.toEntity())
            // Create the relationship
            labelDao.addTaskLabel(
                TaskLabelCrossRef(
                    taskId = taskId,
                    labelId = label.id
                )
            )
        }
        
        return task
    }
    
    suspend fun updateTask(task: Task) {
        val now = LocalDateTime.now()
        val updatedTask = task.copy(updatedAt = now)
        taskDao.upsertTask(updatedTask.toEntity())
    }
    
    suspend fun deleteTask(taskId: String): Boolean {
        val entity = taskDao.getTaskById(taskId)
        return if (entity != null) {
            taskDao.deleteTask(entity)
            true
        } else {
            false
        }
    }
    
    suspend fun updateTaskProgress(taskId: String, progress: Int) {
        taskDao.updateProgress(taskId, progress)
        taskDao.updateTimestamp(taskId, LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
    }
    
    suspend fun updateTaskColumn(taskId: String, column: TaskColumn) {
        taskDao.updateColumn(taskId, column.name.lowercase())
        taskDao.updateTimestamp(taskId, LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
    }
    
    suspend fun updateTaskSection(taskId: String, section: TaskSection) {
        taskDao.updateSection(taskId, section.name.lowercase())
        taskDao.updateTimestamp(taskId, LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
    }
}





