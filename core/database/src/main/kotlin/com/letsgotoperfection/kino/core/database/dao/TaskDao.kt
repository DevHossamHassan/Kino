package com.letsgotoperfection.kino.core.database.dao

import androidx.room.*
import com.letsgotoperfection.kino.core.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE section = :section ORDER BY updatedAt DESC")
    fun getTasksBySection(section: String): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE column = :column ORDER BY updatedAt DESC")
    fun getTasksByColumn(column: String): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun observeTaskById(taskId: String): Flow<TaskEntity?>
    
    @Query("SELECT * FROM tasks ORDER BY updatedAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Upsert
    suspend fun upsertTask(task: TaskEntity)
    
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    @Query("UPDATE tasks SET progress = :progress WHERE id = :taskId")
    suspend fun updateProgress(taskId: String, progress: Int)
    
    @Query("UPDATE tasks SET column = :column WHERE id = :taskId")
    suspend fun updateColumn(taskId: String, column: String)
    
    @Query("UPDATE tasks SET section = :section WHERE id = :taskId")
    suspend fun updateSection(taskId: String, section: String)

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
}
