package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository

import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for recurring tasks data operations
 */
interface RecurringTasksRepository {
    
    /**
     * Get all active recurring tasks
     */
    fun getActiveRecurringTasks(): Flow<List<RecurringTask>>
    
    /**
     * Get all recurring tasks (active and inactive)
     */
    fun getAllRecurringTasks(): Flow<List<RecurringTask>>
    
    /**
     * Get a specific recurring task by ID
     */
    suspend fun getRecurringTaskById(id: String): RecurringTask?
    
    /**
     * Observe a specific recurring task by ID
     */
    fun observeRecurringTaskById(id: String): Flow<RecurringTask?>
    
    /**
     * Create a new recurring task
     */
    suspend fun createRecurringTask(recurringTask: RecurringTask): Result<String>
    
    /**
     * Update an existing recurring task
     */
    suspend fun updateRecurringTask(recurringTask: RecurringTask): Result<Unit>
    
    /**
     * Delete a recurring task
     */
    suspend fun deleteRecurringTask(id: String): Result<Unit>
    
    /**
     * Set the active state of a recurring task
     */
    suspend fun setRecurringTaskActive(id: String, isActive: Boolean): Result<Unit>
    
    /**
     * Update the last generated date for a recurring task
     */
    suspend fun updateLastGeneratedDate(id: String, date: java.time.LocalDate): Result<Unit>
    
    /**
     * Get recurring tasks that need instance generation
     */
    suspend fun getRecurringTasksNeedingGeneration(): List<RecurringTask>
}
