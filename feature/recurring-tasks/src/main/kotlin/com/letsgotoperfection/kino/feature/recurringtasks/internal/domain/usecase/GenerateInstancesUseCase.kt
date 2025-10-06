package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskGenerationException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * Use case for generating task instances from recurring tasks
 */
class GenerateInstancesUseCase @Inject constructor(
    private val repository: RecurringTasksRepository,
    private val recurrenceCalculator: RecurrenceCalculator
) {
    
    /**
     * Generate task instances for a specific recurring task within a date range
     */
    suspend operator fun invoke(
        recurringTaskId: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Result<Unit> {
        return try {
            val recurringTask = repository.getRecurringTaskById(recurringTaskId)
                ?: return Result.failure(RecurringTaskGenerationException("Recurring task not found"))
            
            generateInstancesForTask(recurringTask, fromDate, toDate)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(RecurringTaskGenerationException("Failed to generate instances: ${e.message}"))
        }
    }
    
    /**
     * Generate task instances for all active recurring tasks within a date range
     */
    suspend operator fun invoke(
        fromDate: LocalDate,
        toDate: LocalDate
    ): Result<Unit> {
        return try {
            val recurringTasks = repository.getRecurringTasksNeedingGeneration()
            
            recurringTasks.forEach { recurringTask ->
                generateInstancesForTask(recurringTask, fromDate, toDate)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(RecurringTaskGenerationException("Failed to generate instances: ${e.message}"))
        }
    }
    
    private suspend fun generateInstancesForTask(
        recurringTask: RecurringTask,
        fromDate: LocalDate,
        toDate: LocalDate
    ) {
        val lastGenerated = recurringTask.lastGeneratedDate ?: recurringTask.startDate.minusDays(1)
        val actualFromDate = maxOf(fromDate, lastGenerated.plusDays(1))
        
        // Check if task has ended
        if (recurringTask.endDate != null && actualFromDate.isAfter(recurringTask.endDate)) {
            return
        }
        
        val actualEndDate = minOfNotNull(
            toDate,
            recurringTask.endDate ?: LocalDate.MAX
        )
        
        // Generate occurrences for the date range
        val occurrences = recurrenceCalculator.generateOccurrences(
            rule = recurringTask.recurrenceRule,
            startDate = actualFromDate,
            endDate = actualEndDate
        )
        
        // Create task instances for each occurrence
        occurrences.forEach { occurrence ->
            createTaskInstance(recurringTask, occurrence)
        }
        
        // Update last generated date
        if (occurrences.isNotEmpty()) {
            repository.updateLastGeneratedDate(recurringTask.id, occurrences.last())
        }
    }
    
    private suspend fun createTaskInstance(
        recurringTask: RecurringTask,
        scheduledDate: LocalDate
    ) {
        repository.createTaskInstance(recurringTask, scheduledDate)
            .getOrElse { throw it }
    }
    
    private fun minOfNotNull(vararg values: LocalDate): LocalDate {
        return values.filterNotNull().minOrNull() ?: LocalDate.MAX
    }
}
