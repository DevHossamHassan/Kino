package com.letsgotoperfection.kino.feature.recurringtasks.internal.data.repository

import com.letsgotoperfection.kino.core.database.dao.RecurringTaskDao
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.mapper.toDomain as toTaskDomain
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.recurringtasks.internal.data.mapper.toDomain
import com.letsgotoperfection.kino.feature.recurringtasks.internal.data.mapper.toEntity
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for recurring tasks data operations
 */
@Singleton
class RecurringTasksRepositoryImpl @Inject constructor(
    private val recurringTaskDao: RecurringTaskDao,
    private val taskDao: TaskDao
) : RecurringTasksRepository {
    
    override fun getActiveRecurringTasks(): Flow<List<RecurringTask>> {
        return recurringTaskDao.getActiveRecurringTasks()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun getAllRecurringTasks(): Flow<List<RecurringTask>> {
        return recurringTaskDao.getAllRecurringTasks()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun getRecurringTaskById(id: String): RecurringTask? {
        return recurringTaskDao.getById(id)?.toDomain()
    }
    
    override fun observeRecurringTaskById(id: String): Flow<RecurringTask?> {
        return recurringTaskDao.observeById(id)
            .map { entity -> entity?.toDomain() }
    }
    
    override suspend fun createRecurringTask(recurringTask: RecurringTask): Result<String> {
        return try {
            recurringTaskDao.upsert(recurringTask.toEntity())
            Result.success(recurringTask.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateRecurringTask(recurringTask: RecurringTask): Result<Unit> {
        return try {
            recurringTaskDao.upsert(recurringTask.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteRecurringTask(id: String): Result<Unit> {
        return try {
            recurringTaskDao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun setRecurringTaskActive(id: String, isActive: Boolean): Result<Unit> {
        return try {
            recurringTaskDao.setActive(id, isActive)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateLastGeneratedDate(id: String, date: LocalDate): Result<Unit> {
        return try {
            recurringTaskDao.updateLastGeneratedDate(id, date.toEpochDay())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecurringTasksNeedingGeneration(): List<RecurringTask> {
        val currentDate = LocalDate.now().toEpochDay()
        return recurringTaskDao.getRecurringTasksNeedingGeneration(currentDate)
            .map { it.toDomain() }
    }

    override suspend fun taskInstanceExists(
        recurringTaskId: String,
        scheduledDate: LocalDate
    ): Boolean {
        return taskDao.countInstancesFor(recurringTaskId, scheduledDate.toEpochDay()) > 0
    }

    override fun getTaskInstances(
        recurringTaskId: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Flow<List<Task>> {
        return taskDao.getInstancesForRecurringTask(
            recurringTaskId = recurringTaskId,
            fromEpochDay = fromDate.toEpochDay(),
            toEpochDay = toDate.toEpochDay()
        ).map { entities -> entities.map { it.toTaskDomain() } }
    }
}
