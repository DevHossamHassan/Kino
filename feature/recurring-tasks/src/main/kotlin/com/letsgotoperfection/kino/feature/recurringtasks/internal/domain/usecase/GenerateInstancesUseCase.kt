package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskGenerationException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for bulk-generating task instances from recurring tasks.
 *
 * Each individual instance goes through [GenerateTaskInstanceUseCase] so manual
 * generation produces identical results to alarm-driven generation (linkage,
 * default column, checklist, duplicate guard). Bulk generation does not emit
 * per-instance notifications or schedule alarms.
 */
class GenerateInstancesUseCase @Inject constructor(
    private val repository: RecurringTasksRepository,
    private val recurrenceCalculator: RecurrenceCalculator,
    private val generateTaskInstance: GenerateTaskInstanceUseCase
) {

    /**
     * Generate task instances for a specific recurring task within a date range.
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
     * Generate task instances for all active recurring tasks within a date range.
     */
    suspend operator fun invoke(
        fromDate: LocalDate,
        toDate: LocalDate
    ): Result<Unit> {
        return try {
            repository.getRecurringTasksNeedingGeneration().forEach { recurringTask ->
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
        val effectiveEnd = recurringTask.endDate?.let { minOf(it, toDate) } ?: toDate

        val occurrences = recurrenceCalculator.generateOccurrences(
            rule = recurringTask.recurrenceRule,
            startDate = recurringTask.startDate,
            fromDate = fromDate,
            toDate = effectiveEnd
        )

        occurrences.forEach { occurrence ->
            generateTaskInstance(
                recurringTaskId = recurringTask.id,
                scheduledDate = occurrence,
                scheduleNext = false,
                notify = false
            ).getOrThrow()
        }
    }
}
