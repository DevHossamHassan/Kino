package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state

import androidx.compose.runtime.Immutable
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import java.time.LocalDate
import java.time.LocalTime

/**
 * UI State for Recurring Tasks feature
 */

@Immutable
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@Immutable
data class RecurringTasksListUiState(
    val recurringTasks: List<RecurringTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@Immutable
data class CreateRecurringTaskUiState(
    val title: String = "",
    val description: String = "",
    val section: TaskSection = TaskSection.PERSONAL,
    val priority: Priority = Priority.MEDIUM,
    val labels: List<Label> = emptyList(),
    val defaultColumn: com.letsgotoperfection.kino.core.model.TaskColumn = com.letsgotoperfection.kino.core.model.TaskColumn.TODO_THIS_WEEK,
    val checklistTemplate: List<String> = emptyList(), // Template checklist items
    val dueDateOffsetDays: Int = 0, // Days to add to creation date for due date (0 = same day)
    val frequency: RecurrenceFrequency = RecurrenceFrequency.DAILY,
    val interval: Int = 1,
    val daysOfWeek: Set<java.time.DayOfWeek> = emptySet(),
    val dayOfMonth: Int? = null,
    val monthOfYear: Int? = null,
    val timeOfDay: LocalTime = LocalTime.of(9, 0),
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val isActive: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isValid: Boolean
        get() = title.isNotBlank() && 
                interval > 0 &&
                (frequency != RecurrenceFrequency.WEEKLY || daysOfWeek.isNotEmpty()) &&
                (frequency != RecurrenceFrequency.MONTHLY || (dayOfMonth != null && dayOfMonth in 1..31)) &&
                (frequency != RecurrenceFrequency.YEARLY || (monthOfYear != null && monthOfYear in 1..12 && dayOfMonth != null && dayOfMonth in 1..31)) &&
                (endDate == null || !endDate.isBefore(startDate))
}

@Immutable
data class EditRecurringTaskUiState(
    val recurringTask: RecurringTask? = null,
    val title: String = "",
    val description: String = "",
    val section: TaskSection = TaskSection.PERSONAL,
    val priority: Priority = Priority.MEDIUM,
    val labels: List<Label> = emptyList(),
    val frequency: RecurrenceFrequency = RecurrenceFrequency.DAILY,
    val interval: Int = 1,
    val daysOfWeek: Set<java.time.DayOfWeek> = emptySet(),
    val dayOfMonth: Int? = null,
    val monthOfYear: Int? = null,
    val timeOfDay: LocalTime = LocalTime.of(9, 0),
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val isActive: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
) {
    val isValid: Boolean
        get() = title.isNotBlank() && 
                description.isNotBlank() && 
                interval > 0 &&
                (frequency != RecurrenceFrequency.WEEKLY || daysOfWeek.isNotEmpty()) &&
                (frequency != RecurrenceFrequency.MONTHLY || (dayOfMonth != null && dayOfMonth in 1..31)) &&
                (frequency != RecurrenceFrequency.YEARLY || (monthOfYear != null && monthOfYear in 1..12 && dayOfMonth != null && dayOfMonth in 1..31)) &&
                (endDate == null || !endDate.isBefore(startDate))
}

@Immutable
data class RecurringTaskInstancesUiState(
    val recurringTask: RecurringTask? = null,
    val instances: List<com.letsgotoperfection.kino.core.model.Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * UI Events for Recurring Tasks feature
 * Navigation events removed - handled by UI callbacks
 */
sealed interface RecurringTaskEvent {
    data class ShowError(val message: String) : RecurringTaskEvent
    data class ShowSuccess(val message: String) : RecurringTaskEvent
}

/**
 * UI Actions for Recurring Tasks feature
 */
sealed interface RecurringTaskAction {
    data class CreateRecurringTask(
        val title: String,
        val description: String,
        val section: TaskSection,
        val priority: Priority,
        val labels: List<Label>,
        val frequency: RecurrenceFrequency,
        val interval: Int,
        val daysOfWeek: Set<java.time.DayOfWeek>,
        val dayOfMonth: Int?,
        val monthOfYear: Int?,
        val timeOfDay: LocalTime,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val isActive: Boolean
    ) : RecurringTaskAction
    
    data class UpdateRecurringTask(
        val id: String,
        val title: String,
        val description: String,
        val section: TaskSection,
        val priority: Priority,
        val labels: List<Label>,
        val frequency: RecurrenceFrequency,
        val interval: Int,
        val daysOfWeek: Set<java.time.DayOfWeek>,
        val dayOfMonth: Int?,
        val monthOfYear: Int?,
        val timeOfDay: LocalTime,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val isActive: Boolean
    ) : RecurringTaskAction
    
    data class DeleteRecurringTask(val id: String) : RecurringTaskAction
    data class ToggleRecurringTaskActive(val id: String, val isActive: Boolean) : RecurringTaskAction
    data class LoadRecurringTask(val id: String) : RecurringTaskAction
    data class LoadRecurringTaskInstances(val id: String, val fromDate: LocalDate, val toDate: LocalDate) : RecurringTaskAction
    data object RefreshRecurringTasks : RecurringTaskAction
}
