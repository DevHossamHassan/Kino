package com.letsgotoperfection.kino.feature.recurringtasks.internal.api

import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskNotFoundException
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.DeleteRecurringTaskUseCase
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.GenerateInstancesUseCase
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.SetRecurringTaskActiveUseCase
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.UpdateRecurringTaskUseCase
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.formatter.RecurrenceDescriptionFormatter
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [RecurringTasksApi].
 *
 * Mutations are delegated to use cases so alarm scheduling and validation stay
 * consistent regardless of whether a change originates from this API or the
 * feature's own UI.
 */
@Singleton
class RecurringTasksApiImpl @Inject constructor(
    private val repository: RecurringTasksRepository,
    private val recurrenceCalculator: RecurrenceCalculator,
    private val descriptionFormatter: RecurrenceDescriptionFormatter,
    private val updateRecurringTaskUseCase: UpdateRecurringTaskUseCase,
    private val deleteRecurringTaskUseCase: DeleteRecurringTaskUseCase,
    private val setRecurringTaskActiveUseCase: SetRecurringTaskActiveUseCase,
    private val generateInstancesUseCase: GenerateInstancesUseCase
) : RecurringTasksApi {

    override suspend fun getRecurringTask(recurringTaskId: String): Result<RecurringTask> {
        return try {
            val recurringTask = repository.getRecurringTaskById(recurringTaskId)
                ?: return Result.failure(
                    RecurringTaskNotFoundException("Recurring task with ID $recurringTaskId not found")
                )
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
        return updateRecurringTaskUseCase(recurringTask)
    }

    override suspend fun deleteRecurringTask(recurringTaskId: String): Result<Unit> {
        return deleteRecurringTaskUseCase(recurringTaskId)
    }

    override suspend fun setRecurringTaskActive(recurringTaskId: String, isActive: Boolean): Result<Unit> {
        return setRecurringTaskActiveUseCase(recurringTaskId, isActive)
    }

    override fun getTaskInstances(
        recurringTaskId: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Flow<List<Task>> {
        return repository.getTaskInstances(recurringTaskId, fromDate, toDate)
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
                ?: return Result.failure(
                    RecurringTaskNotFoundException("Recurring task with ID $recurringTaskId not found")
                )

            val nextOccurrence = recurrenceCalculator.nextOccurrenceAfter(
                rule = recurringTask.recurrenceRule,
                startDate = recurringTask.startDate,
                fromDate = fromDate.minusDays(1),
                endDate = recurringTask.endDate
            )

            Result.success(nextOccurrence)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecurrenceDescription(recurringTaskId: String): Result<String> {
        return try {
            val recurringTask = repository.getRecurringTaskById(recurringTaskId)
                ?: return Result.failure(
                    RecurringTaskNotFoundException("Recurring task with ID $recurringTaskId not found")
                )
            Result.success(descriptionFormatter.format(recurringTask.recurrenceRule))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
