package com.letsgotoperfection.kino.core.database.dao

import androidx.room.*
import com.letsgotoperfection.kino.core.database.entity.RecurringTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTaskDao {
    
    @Query("SELECT * FROM recurring_tasks WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveRecurringTasks(): Flow<List<RecurringTaskEntity>>
    
    @Query("SELECT * FROM recurring_tasks ORDER BY createdAt DESC")
    fun getAllRecurringTasks(): Flow<List<RecurringTaskEntity>>
    
    @Query("SELECT * FROM recurring_tasks WHERE id = :id")
    suspend fun getById(id: String): RecurringTaskEntity?
    
    @Query("SELECT * FROM recurring_tasks WHERE id = :id")
    fun observeById(id: String): Flow<RecurringTaskEntity?>
    
    @Upsert
    suspend fun upsert(recurringTask: RecurringTaskEntity)
    
    @Delete
    suspend fun delete(recurringTask: RecurringTaskEntity)
    
    @Query("DELETE FROM recurring_tasks WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("UPDATE recurring_tasks SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: String, active: Boolean)
    
    @Query("UPDATE recurring_tasks SET lastGeneratedDate = :date WHERE id = :id")
    suspend fun updateLastGeneratedDate(id: String, date: Long)
    
    @Query("SELECT * FROM recurring_tasks WHERE isActive = 1 AND (endDate IS NULL OR endDate >= :currentDate)")
    suspend fun getActiveRecurringTasksForGeneration(currentDate: Long): List<RecurringTaskEntity>
    
    @Query("SELECT * FROM recurring_tasks WHERE isActive = 1 AND (lastGeneratedDate IS NULL OR lastGeneratedDate < :currentDate)")
    suspend fun getRecurringTasksNeedingGeneration(currentDate: Long): List<RecurringTaskEntity>
}

