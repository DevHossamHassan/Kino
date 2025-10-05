package com.letsgotoperfection.kino.feature.recurringtasks.internal.api

import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskNotFoundException
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.GenerateInstancesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RecurringTasksApi
 */
@Singleton
class RecurringTasksApiImpl @Inject constructor(
    private val repository: RecurringTasksRepository,
    private val taskDao: TaskDao,
    private val recurrenceCalculator: RecurrenceCalculator,
    private val generateInstancesUseCase: GenerateInstancesUseCase
) : RecurringTasksApi {
    
    override suspend fun getRecurringTask(recurringTaskId: String): Result<RecurringTask> {
        return try {
            val recurringTask = repository.getRecurringTaskById(recurringTaskId)
                ?: return Result.failure(RecurringTaskNotFoundException("Recurring task with ID $recurringTaskId not found"))
            Result.success(recurringTask)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getActiveRecurringTasks(): Flow<List<RecurringTask>> {
        return repository.getActiveRecurringTasks()
    }
    
    override fun getAllRecurringTasks(): Flow<List<RecurringTask>> {
        return repository.getAllRecurringTasks()
    }
    
    override suspend fun createRecurringTask(recurringTask: RecurringTask): Result<String> {
        return repository.createRecurringTask(recurringTask)
    }
    
    override suspend fun updateRecurringTask(recurringTask: RecurringTask): Result<Unit> {
        return repository.updateRecurringTask(recurringTask)
    }
    
    override suspend fun deleteRecurringTask(recurringTaskId: String): Result<Unit> {
        return repository.deleteRecurringTask(recurringTaskId)
    }
    
    override suspend fun setRecurringTaskActive(recurringTaskId: String, isActive: Boolean): Result<Unit> {
        return repository.setRecurringTaskActive(recurringTaskId, isActive)
    }
    
    override fun getTaskInstances(
        recurringTaskId: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Flow<List<Task>> {
        return taskDao.getAllTasks()
            .map { taskEntities ->
                taskEntities
                    .filter { it.recurringTaskId == recurringTaskId }
                    .filter { 
                        val scheduledDate = it.scheduledDate?.let { LocalDate.ofEpochDay(it) }
                        scheduledDate != null && 
                        !scheduledDate.isBefore(fromDate) && 
                        !scheduledDate.isAfter(toDate)
                    }
                    .map { taskEntity ->
                        // Convert TaskEntity to Task domain model
                        // This would need proper mapping - for now returning basic conversion
                        Task(
                            id = taskEntity.id,
                            title = taskEntity.title,
                            description = taskEntity.description,
                            section = com.letsgotoperfection.kino.core.model.TaskSection.valueOf(taskEntity.section.uppercase()),
                            column = com.letsgotoperfection.kino.core.model.TaskColumn.valueOf(taskEntity.column.uppercase()),
                            priority = com.letsgotoperfection.kino.core.model.Priority.valueOf(taskEntity.priority.uppercase()),
                            createdAt = java.time.LocalDateTime.ofInstant(
                                java.time.Instant.ofEpochMilli(taskEntity.createdAt),
                                java.time.ZoneId.systemDefault()
                            ),
                            updatedAt = java.time.LocalDateTime.ofInstant(
                                java.time.Instant.ofEpochMilli(taskEntity.updatedAt),
                                java.time.ZoneId.systemDefault()
                            ),
                            dueDate = taskEntity.dueDate?.let {
                                java.time.LocalDateTime.ofInstant(
                                    java.time.Instant.ofEpochMilli(it),
                                    java.time.ZoneId.systemDefault()
                                )
                            },
                            progress = taskEntity.progress,
                            labels = emptyList(), // Would need to fetch labels separately
                            checklist = emptyList(), // Would need to fetch checklist separately
                            attachments = emptyList() // Would need to fetch attachments separately
                        )
                    }
            }
    }
    
    override suspend fun generateTaskInstances(
        recurringTaskId: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Result<Unit> {
        return generateInstancesUseCase(recurringTaskId, fromDate, toDate)
    }
    
    override suspend fun getNextOccurrence(
        recurringTaskId: String,
        fromDate: LocalDate
    ): Result<LocalDate?> {
        return try {
            val recurringTask = repository.getRecurringTaskById(recurringTaskId)
                ?: return Result.failure(RecurringTaskNotFoundException("Recurring task with ID $recurringTaskId not found"))
            
            val nextOccurrence = recurrenceCalculator.calculateNextOccurrence(
                recurringTask.recurrenceRule,
                fromDate
            )
            
            Result.success(nextOccurrence)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecurrenceDescription(recurringTaskId: String): Result<String> {
        return try {
            val recurringTask = repository.getRecurringTaskById(recurringTaskId)
                ?: return Result.failure(RecurringTaskNotFoundException("Recurring task with ID $recurringTaskId not found"))
            
            val description = recurrenceCalculator.getRecurrenceDescription(recurringTask.recurrenceRule)
            Result.success(description)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
