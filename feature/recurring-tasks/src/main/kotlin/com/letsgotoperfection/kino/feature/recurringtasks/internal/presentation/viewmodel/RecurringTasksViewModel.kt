package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskAction
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskEvent
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTasksListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Recurring Tasks List screen
 */
@HiltViewModel
class RecurringTasksViewModel @Inject constructor(
    private val recurringTasksApi: RecurringTasksApi
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RecurringTasksListUiState())
    val uiState: StateFlow<RecurringTasksListUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = Channel<RecurringTaskEvent>(Channel.BUFFERED)
    val uiEvent: Flow<RecurringTaskEvent> = _uiEvent.receiveAsFlow()
    
    init {
        loadRecurringTasks()
    }
    
    fun onAction(action: RecurringTaskAction) {
        when (action) {
            is RecurringTaskAction.DeleteRecurringTask -> handleDeleteRecurringTask(action.id)
            is RecurringTaskAction.ToggleRecurringTaskActive -> handleToggleActive(action.id, action.isActive)
            RecurringTaskAction.RefreshRecurringTasks -> loadRecurringTasks()
            else -> {} // Other actions handled by specific ViewModels
        }
    }
    
    private fun loadRecurringTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            recurringTasksApi.getAllRecurringTasks()
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load recurring tasks"
                        ) 
                    }
                }
                .collect { recurringTasks ->
                    _uiState.update { 
                        it.copy(
                            recurringTasks = recurringTasks,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
        }
    }
    
    private fun handleDeleteRecurringTask(id: String) {
        viewModelScope.launch {
            recurringTasksApi.deleteRecurringTask(id)
                .fold(
                    onSuccess = {
                        _uiEvent.trySend(RecurringTaskEvent.ShowSuccess("Recurring task deleted successfully"))
                        loadRecurringTasks()
                    },
                    onFailure = { error ->
                        _uiEvent.trySend(RecurringTaskEvent.ShowError(error.message ?: "Failed to delete recurring task"))
                    }
                )
        }
    }
    
    private fun handleToggleActive(id: String, isActive: Boolean) {
        viewModelScope.launch {
            recurringTasksApi.setRecurringTaskActive(id, isActive)
                .fold(
                    onSuccess = {
                        val message = if (isActive) "Recurring task resumed" else "Recurring task paused"
                        _uiEvent.trySend(RecurringTaskEvent.ShowSuccess(message))
                        loadRecurringTasks()
                    },
                    onFailure = { error ->
                        val message = if (isActive) "Failed to resume recurring task" else "Failed to pause recurring task"
                        _uiEvent.trySend(RecurringTaskEvent.ShowError(error.message ?: message))
                    }
                )
        }
    }
}
