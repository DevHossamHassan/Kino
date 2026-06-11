package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.CreateRecurringTaskUseCase
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.formatter.RecurrenceDescriptionFormatter
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.CreateRecurringTaskUiState
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurrencePreview
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel for Create Recurring Task screen.
 *
 * Alarm scheduling happens inside [CreateRecurringTaskUseCase]; this ViewModel
 * only manages form state and exposes a derived [RecurrencePreview].
 */
@HiltViewModel
class CreateRecurringTaskViewModel @Inject constructor(
    private val createRecurringTaskUseCase: CreateRecurringTaskUseCase,
    private val recurrenceCalculator: RecurrenceCalculator,
    private val descriptionFormatter: RecurrenceDescriptionFormatter
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRecurringTaskUiState())
    val uiState: StateFlow<CreateRecurringTaskUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<RecurringTaskEvent>(Channel.BUFFERED)
    val uiEvent: Flow<RecurringTaskEvent> = _uiEvent.receiveAsFlow()

    /**
     * Live preview of the configured recurrence, recomputed only when the form changes.
     */
    val recurrencePreview: StateFlow<RecurrencePreview> = _uiState
        .map { state -> buildPreview(state) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildPreview(_uiState.value)
        )

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateSection(section: TaskSection) {
        _uiState.update { it.copy(section = section) }
    }

    fun updatePriority(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun updateLabels(labels: List<Label>) {
        _uiState.update { it.copy(labels = labels) }
    }

    fun updateDefaultColumn(column: TaskColumn) {
        _uiState.update { it.copy(defaultColumn = column) }
    }

    fun updateChecklistTemplate(checklist: List<String>) {
        _uiState.update { it.copy(checklistTemplate = checklist) }
    }

    fun addChecklistItem(item: String) {
        _uiState.update {
            it.copy(checklistTemplate = it.checklistTemplate + item)
        }
    }

    fun removeChecklistItem(index: Int) {
        _uiState.update {
            it.copy(checklistTemplate = it.checklistTemplate.filterIndexed { i, _ -> i != index })
        }
    }

    fun updateDueDateOffsetDays(days: Int) {
        _uiState.update { it.copy(dueDateOffsetDays = days) }
    }

    fun updateFrequency(frequency: RecurrenceFrequency) {
        _uiState.update {
            it.copy(
                frequency = frequency,
                // Reset frequency-specific fields when changing frequency
                daysOfWeek = if (frequency == RecurrenceFrequency.WEEKLY) {
                    // If switching to weekly and no days selected, default to current day
                    it.daysOfWeek.ifEmpty { setOf(LocalDate.now().dayOfWeek) }
                } else emptySet(),
                dayOfMonth = if (frequency == RecurrenceFrequency.MONTHLY || frequency == RecurrenceFrequency.YEARLY) {
                    it.dayOfMonth ?: LocalDate.now().dayOfMonth
                } else null,
                monthOfYear = if (frequency == RecurrenceFrequency.YEARLY) {
                    it.monthOfYear ?: LocalDate.now().monthValue
                } else null
            )
        }
    }

    fun updateInterval(interval: Int) {
        _uiState.update { it.copy(interval = maxOf(1, interval)) }
    }

    fun updateDaysOfWeek(daysOfWeek: Set<DayOfWeek>) {
        _uiState.update { it.copy(daysOfWeek = daysOfWeek) }
    }

    fun updateDayOfMonth(dayOfMonth: Int) {
        _uiState.update { it.copy(dayOfMonth = dayOfMonth) }
    }

    fun updateMonthOfYear(monthOfYear: Int) {
        _uiState.update { it.copy(monthOfYear = monthOfYear) }
    }

    fun updateTimeOfDay(timeOfDay: LocalTime) {
        _uiState.update { it.copy(timeOfDay = timeOfDay) }
    }

    fun updateStartDate(startDate: LocalDate) {
        _uiState.update { it.copy(startDate = startDate) }
    }

    fun updateEndDate(endDate: LocalDate?) {
        _uiState.update { it.copy(endDate = endDate) }
    }

    fun createRecurringTask() {
        val currentState = _uiState.value
        if (!currentState.isValid) {
            _uiEvent.trySend(RecurringTaskEvent.ShowError(R.string.fill_required_fields))
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }

            val recurrenceRule = RecurrenceRule(
                frequency = currentState.frequency,
                interval = currentState.interval,
                daysOfWeek = currentState.daysOfWeek,
                dayOfMonth = currentState.dayOfMonth,
                monthOfYear = currentState.monthOfYear,
                timeOfDay = currentState.timeOfDay
            )

            createRecurringTaskUseCase(
                title = currentState.title,
                description = currentState.description,
                section = currentState.section,
                priority = currentState.priority,
                labels = currentState.labels,
                recurrenceRule = recurrenceRule,
                startDate = currentState.startDate,
                endDate = currentState.endDate,
                isActive = currentState.isActive,
                defaultColumn = currentState.defaultColumn,
                checklistTemplate = currentState.checklistTemplate,
                dueDateOffsetDays = currentState.dueDateOffsetDays
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.trySend(RecurringTaskEvent.ShowSuccess(R.string.recurring_task_created))
                    // Navigation handled by UI callbacks
                },
                onFailure = {
                    _uiState.update {
                        it.copy(isLoading = false, errorRes = R.string.error_creating_recurring_task)
                    }
                    _uiEvent.trySend(RecurringTaskEvent.ShowError(R.string.error_creating_recurring_task))
                }
            )
        }
    }

    private fun buildPreview(state: CreateRecurringTaskUiState): RecurrencePreview {
        val promptRes = when (state.frequency) {
            RecurrenceFrequency.WEEKLY ->
                R.string.select_days_of_week.takeIf { state.daysOfWeek.isEmpty() }
            RecurrenceFrequency.MONTHLY ->
                R.string.select_day_of_month.takeIf { state.dayOfMonth == null }
            RecurrenceFrequency.YEARLY ->
                R.string.select_month_and_day.takeIf { state.monthOfYear == null || state.dayOfMonth == null }
            RecurrenceFrequency.DAILY -> null
        }
        if (promptRes != null) {
            return RecurrencePreview.Prompt(promptRes)
        }

        val rule = RecurrenceRule(
            frequency = state.frequency,
            interval = state.interval,
            daysOfWeek = state.daysOfWeek,
            dayOfMonth = state.dayOfMonth,
            monthOfYear = state.monthOfYear,
            timeOfDay = state.timeOfDay
        )
        return RecurrencePreview.Ready(
            description = descriptionFormatter.format(rule),
            nextOccurrences = recurrenceCalculator.getNextOccurrences(
                rule = rule,
                startDate = state.startDate,
                fromDate = maxOf(LocalDate.now(), state.startDate),
                count = PREVIEW_OCCURRENCES,
                endDate = state.endDate
            )
        )
    }

    companion object {
        private const val PREVIEW_OCCURRENCES = 5
    }
}
