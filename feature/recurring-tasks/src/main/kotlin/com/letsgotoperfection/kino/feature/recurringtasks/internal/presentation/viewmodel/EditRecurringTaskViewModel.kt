package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.EditRecurringTaskUiState
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskAction
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Edit Recurring Task screen
 */
@HiltViewModel
class EditRecurringTaskViewModel @Inject constructor(
    private val recurringTasksApi: RecurringTasksApi
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EditRecurringTaskUiState())
    val uiState: StateFlow<EditRecurringTaskUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = Channel<RecurringTaskEvent>(Channel.BUFFERED)
    val uiEvent: Flow<RecurringTaskEvent> = _uiEvent.receiveAsFlow()
    
    fun loadRecurringTask(recurringTaskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }
            
            recurringTasksApi.getRecurringTask(recurringTaskId)
                .onSuccess { recurringTask ->
                    _uiState.update { 
                        it.copy(
                            recurringTask = recurringTask,
                            title = recurringTask.title,
                            description = recurringTask.description,
                            section = recurringTask.section,
                            priority = recurringTask.priority,
                            labels = recurringTask.labels,
                            frequency = recurringTask.recurrenceRule.frequency,
                            interval = recurringTask.recurrenceRule.interval,
                            daysOfWeek = recurringTask.recurrenceRule.daysOfWeek,
                            dayOfMonth = recurringTask.recurrenceRule.dayOfMonth,
                            monthOfYear = recurringTask.recurrenceRule.monthOfYear,
                            timeOfDay = recurringTask.recurrenceRule.timeOfDay,
                            startDate = recurringTask.startDate,
                            endDate = recurringTask.endDate,
                            isActive = recurringTask.isActive,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorRes = R.string.error_loading_recurring_task
                        )
                    }
                }
        }
    }
    
    fun onAction(action: RecurringTaskAction) {
        when (action) {
            is RecurringTaskAction.LoadRecurringTask -> {
                loadRecurringTask(action.id)
            }
            is RecurringTaskAction.UpdateRecurringTask -> {
                saveRecurringTask()
            }
            else -> {
                // Handle other actions as needed
            }
        }
    }
    
    // Individual field update methods
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun updateSection(section: com.letsgotoperfection.kino.core.model.TaskSection) {
        _uiState.update { it.copy(section = section) }
    }
    
    fun updatePriority(priority: com.letsgotoperfection.kino.core.model.Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun updateLabels(labels: List<Label>) {
        _uiState.update { it.copy(labels = labels) }
    }
    
    fun updateFrequency(frequency: com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency) {
        _uiState.update {
            it.copy(
                frequency = frequency,
                daysOfWeek = if (frequency == com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency.WEEKLY) {
                    it.daysOfWeek.ifEmpty { setOf(java.time.LocalDate.now().dayOfWeek) }
                } else emptySet(),
                dayOfMonth = if (frequency == com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency.MONTHLY ||
                    frequency == com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency.YEARLY
                ) {
                    it.dayOfMonth ?: java.time.LocalDate.now().dayOfMonth
                } else null,
                monthOfYear = if (frequency == com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency.YEARLY) {
                    it.monthOfYear ?: java.time.LocalDate.now().monthValue
                } else null
            )
        }
    }
    
    fun updateInterval(interval: Int) {
        _uiState.update { it.copy(interval = maxOf(1, interval)) }
    }

    fun updateDaysOfWeek(daysOfWeek: Set<java.time.DayOfWeek>) {
        _uiState.update { it.copy(daysOfWeek = daysOfWeek) }
    }

    fun updateDayOfMonth(dayOfMonth: Int) {
        _uiState.update { it.copy(dayOfMonth = dayOfMonth) }
    }

    fun updateMonthOfYear(monthOfYear: Int) {
        _uiState.update { it.copy(monthOfYear = monthOfYear) }
    }
    
    fun updateTimeOfDay(timeOfDay: java.time.LocalTime) {
        _uiState.update { it.copy(timeOfDay = timeOfDay) }
    }
    
    fun updateStartDate(startDate: java.time.LocalDate) {
        _uiState.update { it.copy(startDate = startDate) }
    }
    
    fun updateEndDate(endDate: java.time.LocalDate?) {
        _uiState.update { it.copy(endDate = endDate) }
    }
    
    fun updateActiveStatus(isActive: Boolean) {
        _uiState.update { it.copy(isActive = isActive) }
    }
    
    private fun saveRecurringTask() {
        val currentState = _uiState.value
        val recurringTask = currentState.recurringTask ?: return
        
        if (!currentState.isValid) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
        val updatedTask = recurringTask.copy(
            title = currentState.title,
            description = currentState.description,
            section = currentState.section,
            priority = currentState.priority,
            labels = currentState.labels,
            startDate = currentState.startDate,
            endDate = currentState.endDate,
            isActive = currentState.isActive,
            recurrenceRule = recurringTask.recurrenceRule.copy(
                frequency = currentState.frequency,
                interval = currentState.interval,
                daysOfWeek = currentState.daysOfWeek,
                dayOfMonth = currentState.dayOfMonth,
                monthOfYear = currentState.monthOfYear,
                timeOfDay = currentState.timeOfDay
            )
        )
            
            recurringTasksApi.updateRecurringTask(updatedTask)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEvent.trySend(RecurringTaskEvent.ShowSuccess(R.string.recurring_task_updated))
                    // Navigation handled by UI callbacks
                }
                .onFailure {
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEvent.trySend(RecurringTaskEvent.ShowError(R.string.error_updating_recurring_task))
                }
        }
    }
}
