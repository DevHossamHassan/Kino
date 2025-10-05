package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.CreateRecurringTaskUiState
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel for Create Recurring Task screen
 */
@HiltViewModel
class CreateRecurringTaskViewModel @Inject constructor(
    private val recurringTasksApi: RecurringTasksApi,
    private val recurrenceCalculator: RecurrenceCalculator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CreateRecurringTaskUiState())
    val uiState: StateFlow<CreateRecurringTaskUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = Channel<RecurringTaskEvent>(Channel.BUFFERED)
    val uiEvent: Flow<RecurringTaskEvent> = _uiEvent.receiveAsFlow()
    
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
    
    fun updateFrequency(frequency: RecurrenceFrequency) {
        _uiState.update { 
            it.copy(
                frequency = frequency,
                // Reset frequency-specific fields when changing frequency
                daysOfWeek = if (frequency == RecurrenceFrequency.WEEKLY) {
                    // If switching to weekly and no days selected, default to current day
                    if (it.daysOfWeek.isEmpty()) {
                        setOf(java.time.LocalDate.now().dayOfWeek)
                    } else {
                        it.daysOfWeek
                    }
                } else emptySet(),
                dayOfMonth = if (frequency == RecurrenceFrequency.MONTHLY || frequency == RecurrenceFrequency.YEARLY) {
                    it.dayOfMonth ?: java.time.LocalDate.now().dayOfMonth
                } else null,
                monthOfYear = if (frequency == RecurrenceFrequency.YEARLY) {
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
            _uiEvent.trySend(RecurringTaskEvent.ShowError("Please fill in all required fields correctly"))
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val recurrenceRule = RecurrenceRule(
                frequency = currentState.frequency,
                interval = currentState.interval,
                daysOfWeek = currentState.daysOfWeek,
                dayOfMonth = currentState.dayOfMonth,
                monthOfYear = currentState.monthOfYear,
                timeOfDay = currentState.timeOfDay
            )
            
            recurringTasksApi.createRecurringTask(
                com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask(
                    id = "", // Will be generated by the use case
                    title = currentState.title,
                    description = currentState.description,
                    section = currentState.section,
                    priority = currentState.priority,
                    labels = emptyList(), // TODO: Add label selection
                    recurrenceRule = recurrenceRule,
                    startDate = currentState.startDate,
                    endDate = currentState.endDate,
                    isActive = currentState.isActive,
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now(),
                    lastGeneratedDate = null
                )
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.trySend(RecurringTaskEvent.ShowSuccess("Recurring task created successfully"))
                    _uiEvent.trySend(RecurringTaskEvent.NavigateBack)
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to create recurring task"
                        ) 
                    }
                    _uiEvent.trySend(RecurringTaskEvent.ShowError(error.message ?: "Failed to create recurring task"))
                }
            )
        }
    }
    
    fun getRecurrenceDescription(): String {
        val currentState = _uiState.value
        
        // Validate that required fields are present before creating RecurrenceRule
        when (currentState.frequency) {
            RecurrenceFrequency.WEEKLY -> {
                if (currentState.daysOfWeek.isEmpty()) {
                    return "Select days of the week"
                }
            }
            RecurrenceFrequency.MONTHLY -> {
                if (currentState.dayOfMonth == null) {
                    return "Select day of the month"
                }
            }
            RecurrenceFrequency.YEARLY -> {
                if (currentState.monthOfYear == null || currentState.dayOfMonth == null) {
                    return "Select month and day"
                }
            }
            else -> {}
        }
        
        val recurrenceRule = RecurrenceRule(
            frequency = currentState.frequency,
            interval = currentState.interval,
            daysOfWeek = currentState.daysOfWeek,
            dayOfMonth = currentState.dayOfMonth,
            monthOfYear = currentState.monthOfYear,
            timeOfDay = currentState.timeOfDay
        )
        return recurrenceCalculator.getRecurrenceDescription(recurrenceRule)
    }
    
    fun getNextOccurrences(count: Int = 5): List<LocalDate> {
        val currentState = _uiState.value
        
        // Validate that required fields are present before creating RecurrenceRule
        when (currentState.frequency) {
            RecurrenceFrequency.WEEKLY -> {
                if (currentState.daysOfWeek.isEmpty()) {
                    return emptyList()
                }
            }
            RecurrenceFrequency.MONTHLY -> {
                if (currentState.dayOfMonth == null) {
                    return emptyList()
                }
            }
            RecurrenceFrequency.YEARLY -> {
                if (currentState.monthOfYear == null || currentState.dayOfMonth == null) {
                    return emptyList()
                }
            }
            else -> {}
        }
        
        val recurrenceRule = RecurrenceRule(
            frequency = currentState.frequency,
            interval = currentState.interval,
            daysOfWeek = currentState.daysOfWeek,
            dayOfMonth = currentState.dayOfMonth,
            monthOfYear = currentState.monthOfYear,
            timeOfDay = currentState.timeOfDay
        )
        return recurrenceCalculator.getNextOccurrences(
            rule = recurrenceRule,
            fromDate = currentState.startDate,
            count = count
        )
    }
}
