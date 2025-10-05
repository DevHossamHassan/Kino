package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting recurring tasks
 */
class GetRecurringTasksUseCase @Inject constructor(
    private val repository: RecurringTasksRepository
) {
    
    /**
     * Get all active recurring tasks
     */
    fun getActiveRecurringTasks(): Flow<List<RecurringTask>> {
        return repository.getActiveRecurringTasks()
    }
    
    /**
     * Get all recurring tasks (active and inactive)
     */
    fun getAllRecurringTasks(): Flow<List<RecurringTask>> {
        return repository.getAllRecurringTasks()
    }
    
    /**
     * Get a specific recurring task by ID
     */
    suspend fun getRecurringTaskById(id: String): RecurringTask? {
        return repository.getRecurringTaskById(id)
    }
    
    /**
     * Observe a specific recurring task by ID
     */
    fun observeRecurringTaskById(id: String): Flow<RecurringTask?> {
        return repository.observeRecurringTaskById(id)
    }
}
