package com.letsgotoperfection.kino.core.database.dao

import androidx.room.*
import com.letsgotoperfection.kino.core.database.entity.TaskEntity
import com.letsgotoperfection.kino.core.database.entity.TaskWithLabels
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // ========== Optimized queries with relations (eliminates N+1 problem) ==========
    
    @Transaction
    @Query("SELECT * FROM tasks WHERE section = :section ORDER BY orderPosition ASC, updatedAt DESC")
    fun getTasksWithLabelsBySection(section: String): Flow<List<TaskWithLabels>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE `column` = :column ORDER BY orderPosition ASC, updatedAt DESC")
    fun getTasksWithLabelsByColumn(column: String): Flow<List<TaskWithLabels>>

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY `column`, orderPosition ASC, updatedAt DESC")
    fun getAllTasksWithLabels(): Flow<List<TaskWithLabels>>
    
    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskWithLabelsById(taskId: String): TaskWithLabels?
    
    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun observeTaskWithLabelsById(taskId: String): Flow<TaskWithLabels?>
    
    // ========== Legacy queries (kept for backward compatibility) ==========

    @Query("SELECT * FROM tasks WHERE section = :section ORDER BY updatedAt DESC")
    fun getTasksBySection(section: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE `column`  = :column ORDER BY updatedAt DESC")
    fun getTasksByColumn(column: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun observeTaskById(taskId: String): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks ORDER BY `column`, orderPosition ASC, updatedAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET progress = :progress WHERE id = :taskId")
    suspend fun updateProgress(taskId: String, progress: Int)

    @Query("UPDATE tasks SET `column`  = :column WHERE id = :taskId")
    suspend fun updateColumn(taskId: String, column: String)

    @Query("UPDATE tasks SET section = :section WHERE id = :taskId")
    suspend fun updateSection(taskId: String, section: String)

    @Query("UPDATE tasks SET updatedAt = :timestamp WHERE id = :taskId")
    suspend fun updateTimestamp(taskId: String, timestamp: Long)

    @Query("UPDATE tasks SET section = :newSection WHERE section = :oldSection")
    suspend fun renameSection(oldSection: String, newSection: String): Int

    @Query("UPDATE tasks SET title = :title, description = :description, priority = :priority, dueDate = :dueDate, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskDetails(
        taskId: String,
        title: String,
        description: String,
        priority: String,
        dueDate: Long?,
        updatedAt: Long
    )
    
    // ========== Reordering functions ==========
    
    @Query("UPDATE tasks SET orderPosition = :newPosition WHERE id = :taskId")
    suspend fun updateOrderPosition(taskId: String, newPosition: Int)
    
    @Query("UPDATE tasks SET `column` = :column, orderPosition = :orderPosition WHERE id = :taskId")
    suspend fun updateColumnAndOrder(taskId: String, column: String, orderPosition: Int)
    
    @Query("SELECT MAX(orderPosition) FROM tasks WHERE `column` = :column")
    suspend fun getMaxOrderPosition(column: String): Int?
}
