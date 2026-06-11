package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import android.util.Log
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.kanban.api.KanbanApi
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskNotFoundException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import com.letsgotoperfection.kino.feature.recurringtasks.internal.notification.RecurringTaskNotificationService
import com.letsgotoperfection.kino.feature.settings.api.SettingsApi
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

/**
 * Single source of truth for materializing a recurring task occurrence into a
 * concrete task on the Kanban board.
 *
 * Responsibilities:
 * - Validates the occurrence against the template (active, date bounds, rule match)
 * - Guards against duplicate instances for the same (template, date) pair
 * - Honors the template's default column, due date offset and checklist
 * - Links the created task back to the template via [Task.recurringTaskId]
 * - Updates the template's last generated date
 * - Optionally notifies the user (respecting the notification settings toggle)
 * - Optionally schedules the next occurrence alarm
 */
class GenerateTaskInstanceUseCase @Inject constructor(
    private val repository: RecurringTasksRepository,
    private val kanbanApi: KanbanApi,
    private val recurrenceCalculator: RecurrenceCalculator,
    private val alarmScheduler: RecurringTaskAlarmScheduler,
    private val notificationService: RecurringTaskNotificationService,
    private val settingsApi: SettingsApi
) {

    /**
     * @return the created task ID, or null when the occurrence was legitimately
     *         skipped (inactive template, out-of-range date, duplicate instance).
     */
    suspend operator fun invoke(
        recurringTaskId: String,
        scheduledDate: LocalDate,
        scheduleNext: Boolean = true,
        notify: Boolean = true
    ): Result<String?> {
        val template = repository.getRecurringTaskById(recurringTaskId)
            ?: return Result.failure(
                RecurringTaskNotFoundException("Recurring task $recurringTaskId not found")
            )

        if (!template.shouldGenerateOn(scheduledDate)) {
            Log.i(TAG, "Skipping $scheduledDate for $recurringTaskId: not a valid occurrence")
            if (scheduleNext) scheduleNextOccurrence(template, scheduledDate)
            return Result.success(null)
        }

        if (repository.taskInstanceExists(recurringTaskId, scheduledDate)) {
            Log.i(TAG, "Skipping $scheduledDate for $recurringTaskId: instance already exists")
            if (scheduleNext) scheduleNextOccurrence(template, scheduledDate)
            return Result.success(null)
        }

        val task = buildTask(template, scheduledDate)

        return kanbanApi.createTask(task).fold(
            onSuccess = { createdTaskId ->
                repository.updateLastGeneratedDate(template.id, scheduledDate)

                if (notify) {
                    sendNotificationIfEnabled(template, task.title, createdTaskId)
                }
                if (scheduleNext) {
                    scheduleNextOccurrence(template, scheduledDate)
                }
                Log.i(TAG, "Created task $createdTaskId from recurring task ${template.id}")
                Result.success(createdTaskId)
            },
            onError = { error ->
                Log.e(TAG, "Failed to create task from recurring task ${template.id}", error)
                Result.failure(error)
            }
        )
    }

    private fun buildTask(template: RecurringTask, scheduledDate: LocalDate): Task {
        // Deterministic ID makes instance creation idempotent at the DB level.
        val taskId = UUID.nameUUIDFromBytes(
            "${template.id}_${scheduledDate.toEpochDay()}".toByteArray()
        ).toString()

        val now = LocalDateTime.now()
        val dueDate = LocalDateTime.of(
            scheduledDate.plusDays(template.dueDateOffsetDays.toLong()),
            template.recurrenceRule.timeOfDay
        )
        val titleSuffix = scheduledDate.format(
            DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
        )

        return Task(
            id = taskId,
            title = "${template.title} - $titleSuffix",
            description = template.description,
            section = template.section,
            column = template.defaultColumn,
            priority = template.priority,
            createdAt = now,
            updatedAt = now,
            dueDate = dueDate,
            progress = 0,
            orderPosition = 0,
            labels = template.labels,
            checklist = template.checklistTemplate.mapIndexed { index, text ->
                ChecklistItem(
                    id = UUID.randomUUID().toString(),
                    taskId = taskId,
                    text = text,
                    isCompleted = false,
                    order = index,
                    createdAt = now
                )
            },
            attachments = emptyList(),
            recurringTaskId = template.id,
            scheduledDate = scheduledDate
        )
    }

    private suspend fun sendNotificationIfEnabled(
        template: RecurringTask,
        taskTitle: String,
        taskId: String
    ) {
        try {
            val enabled = settingsApi.areRecurringTaskNotificationsEnabled().first()
            if (!enabled) return
            notificationService.sendTaskCreated(
                taskTitle = taskTitle,
                column = template.defaultColumn,
                section = template.section,
                taskId = taskId
            )
        } catch (e: Exception) {
            // A notification failure must never fail task generation.
            Log.e(TAG, "Failed to send notification for created task", e)
        }
    }

    private fun scheduleNextOccurrence(template: RecurringTask, fromDate: LocalDate) {
        if (!template.isActive) return
        val next = recurrenceCalculator.nextOccurrenceAfter(
            rule = template.recurrenceRule,
            startDate = template.startDate,
            fromDate = fromDate,
            endDate = template.endDate
        )
        if (next != null) {
            alarmScheduler.scheduleTaskGeneration(template, next)
        } else {
            Log.i(TAG, "No more occurrences for ${template.id} after $fromDate")
        }
    }

    companion object {
        private const val TAG = "GenerateTaskInstance"
    }
}
