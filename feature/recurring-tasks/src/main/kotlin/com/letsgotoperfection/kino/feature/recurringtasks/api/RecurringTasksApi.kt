package com.letsgotoperfection.kino.feature.recurringtasks.api

import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Public API for Recurring Tasks feature.
 * 
 * This API allows other feature modules to:
 * - Query recurring tasks
 * - Create/update recurring tasks
 * - Navigate to recurring task screens
 * - Get task instances from recurring tasks
 * 
 * @since 1.0.0
 */
interface RecurringTasksApi {
    
    /**
     * Retrieves a recurring task by its unique identifier.
     * 
     * @param recurringTaskId The unique recurring task identifier
     * @return Result containing the RecurringTask or an error
     * @throws RecurringTaskNotFoundException if recurring task doesn't exist
     */
    suspend fun getRecurringTask(recurringTaskId: String): Result<RecurringTask>
    
    /**
     * Retrieves all active recurring tasks.
     * 
     * @return Flow of active recurring tasks
     */
    fun getActiveRecurringTasks(): Flow<List<RecurringTask>>
    
    /**
     * Retrieves all recurring tasks (active and inactive).
     * 
     * @return Flow of all recurring tasks
     */
    fun getAllRecurringTasks(): Flow<List<RecurringTask>>
    
    /**
     * Creates a new recurring task.
     * 
     * @param recurringTask The recurring task to create
     * @return Result containing the created recurring task ID or an error
     */
    suspend fun createRecurringTask(recurringTask: RecurringTask): Result<String>
    
    /**
     * Updates an existing recurring task.
     * 
     * @param recurringTask The recurring task to update
     * @return Result indicating success or failure
     */
    suspend fun updateRecurringTask(recurringTask: RecurringTask): Result<Unit>
    
    /**
     * Deletes a recurring task.
     * 
     * @param recurringTaskId The ID of the recurring task to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteRecurringTask(recurringTaskId: String): Result<Unit>
    
    /**
     * Toggles the active state of a recurring task.
     * 
     * @param recurringTaskId The ID of the recurring task
     * @param isActive The new active state
     * @return Result indicating success or failure
     */
    suspend fun setRecurringTaskActive(recurringTaskId: String, isActive: Boolean): Result<Unit>
    
    /**
     * Gets task instances generated from a recurring task.
     * 
     * @param recurringTaskId The ID of the recurring task
     * @param fromDate Start date for the query (inclusive)
     * @param toDate End date for the query (inclusive)
     * @return Flow of task instances in the date range
     */
    fun getTaskInstances(
        recurringTaskId: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Flow<List<Task>>
    
    /**
     * Manually triggers generation of task instances for a recurring task.
     * 
     * @param recurringTaskId The ID of the recurring task
     * @param fromDate Start date for generation (inclusive)
     * @param toDate End date for generation (inclusive)
     * @return Result indicating success or failure
     */
    suspend fun generateTaskInstances(
        recurringTaskId: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Result<Unit>
    
    /**
     * Gets the next occurrence date for a recurring task.
     * 
     * @param recurringTaskId The ID of the recurring task
     * @param fromDate The date to calculate from
     * @return Result containing the next occurrence date or null if no more occurrences
     */
    suspend fun getNextOccurrence(
        recurringTaskId: String,
        fromDate: LocalDate
    ): Result<LocalDate?>
    
    /**
     * Gets a human-readable description of the recurrence pattern.
     * 
     * @param recurringTaskId The ID of the recurring task
     * @return Result containing the recurrence description
     */
    suspend fun getRecurrenceDescription(recurringTaskId: String): Result<String>
}


/**
 * Exceptions for Recurring Tasks feature
 */
class RecurringTaskNotFoundException(message: String) : Exception(message)
class InvalidRecurrenceRuleException(message: String) : Exception(message)
class RecurringTaskGenerationException(message: String) : Exception(message)
