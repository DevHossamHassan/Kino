package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            recurringTasksApi.getRecurringTask(recurringTaskId)
                .onSuccess { recurringTask ->
                    _uiState.update { 
                        it.copy(
                            recurringTask = recurringTask,
                            title = recurringTask.title,
                            description = recurringTask.description,
                            section = recurringTask.section,
                            priority = recurringTask.priority,
                            frequency = recurringTask.recurrenceRule.frequency,
                            interval = recurringTask.recurrenceRule.interval,
                            daysOfWeek = recurringTask.recurrenceRule.daysOfWeek,
                            dayOfMonth = recurringTask.recurrenceRule.dayOfMonth,
                            monthOfYear = recurringTask.recurrenceRule.monthOfYear,
                            startDate = recurringTask.startDate,
                            endDate = recurringTask.endDate,
                            isActive = recurringTask.isActive,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load recurring task"
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
                // Handle update action
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
    
    fun updateFrequency(frequency: com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency) {
        _uiState.update { it.copy(frequency = frequency) }
    }
    
    fun updateInterval(interval: Int) {
        _uiState.update { it.copy(interval = interval) }
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
                startDate = currentState.startDate,
                endDate = currentState.endDate,
                isActive = currentState.isActive,
                recurrenceRule = recurringTask.recurrenceRule.copy(
                    frequency = currentState.frequency,
                    interval = currentState.interval,
                    daysOfWeek = currentState.daysOfWeek,
                    dayOfMonth = currentState.dayOfMonth,
                    monthOfYear = currentState.monthOfYear
                )
            )
            
            recurringTasksApi.updateRecurringTask(updatedTask)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEvent.trySend(RecurringTaskEvent.ShowSuccess("Recurring task updated successfully"))
                    // Navigation handled by UI callbacks
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEvent.trySend(RecurringTaskEvent.ShowError(error.message ?: "Failed to update recurring task"))
                }
        }
    }
}
