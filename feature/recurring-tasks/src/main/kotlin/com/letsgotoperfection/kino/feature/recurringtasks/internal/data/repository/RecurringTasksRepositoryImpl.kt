package com.letsgotoperfection.kino.feature.recurringtasks.internal.data.repository

import com.letsgotoperfection.kino.core.database.dao.RecurringTaskDao
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.entity.TaskEntity
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskNotFoundException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.data.mapper.toDomain
import com.letsgotoperfection.kino.feature.recurringtasks.internal.data.mapper.toEntity
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
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

    override suspend fun createTaskInstance(
        recurringTask: RecurringTask,
        scheduledDate: LocalDate
    ): Result<Unit> {
        return try {
            val scheduledDateTime = scheduledDate.atTime(recurringTask.recurrenceRule.timeOfDay)
            val now = LocalDateTime.now()
            val zone = ZoneId.systemDefault()
            val formatter = DateTimeFormatter.ofPattern("MMM dd")
            val taskIdSeed = "${recurringTask.id}_${scheduledDate.toEpochDay()}"
            val taskEntity = TaskEntity(
                id = UUID.nameUUIDFromBytes(taskIdSeed.toByteArray()).toString(),
                title = "${recurringTask.title} - ${scheduledDate.format(formatter)}",
                description = recurringTask.description,
                section = recurringTask.section.name.lowercase(),
                column = TaskColumn.TODO_THIS_WEEK.name.lowercase(),
                priority = recurringTask.priority.name.lowercase(),
                createdAt = now.atZone(zone).toInstant().toEpochMilli(),
                updatedAt = now.atZone(zone).toInstant().toEpochMilli(),
                dueDate = scheduledDateTime.atZone(zone).toInstant().toEpochMilli(),
                progress = 0,
                recurringTaskId = recurringTask.id,
                scheduledDate = scheduledDate.toEpochDay()
            )

            taskDao.upsertTask(taskEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
