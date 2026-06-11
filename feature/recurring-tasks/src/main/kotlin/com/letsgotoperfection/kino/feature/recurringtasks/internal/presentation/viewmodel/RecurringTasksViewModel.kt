package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.feature.recurringtasks.R
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
            _uiState.update { it.copy(isLoading = true, errorRes = null) }
            
            recurringTasksApi.getAllRecurringTasks()
                .catch {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorRes = R.string.error_loading_recurring_tasks
                        ) 
                    }
                }
                .collect { recurringTasks ->
                    _uiState.update { 
                        it.copy(
                            recurringTasks = recurringTasks,
                            isLoading = false,
                            errorRes = null
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
                        _uiEvent.trySend(RecurringTaskEvent.ShowSuccess(R.string.recurring_task_deleted))
                        loadRecurringTasks()
                    },
                    onFailure = {
                        _uiEvent.trySend(RecurringTaskEvent.ShowError(R.string.error_deleting_recurring_task))
                    }
                )
        }
    }
    
    private fun handleToggleActive(id: String, isActive: Boolean) {
        viewModelScope.launch {
            recurringTasksApi.setRecurringTaskActive(id, isActive)
                .fold(
                    onSuccess = {
                        val messageRes = if (isActive) {
                            R.string.recurring_task_resumed
                        } else {
                            R.string.recurring_task_paused
                        }
                        _uiEvent.trySend(RecurringTaskEvent.ShowSuccess(messageRes))
                        loadRecurringTasks()
                    },
                    onFailure = {
                        _uiEvent.trySend(RecurringTaskEvent.ShowError(R.string.error_updating_recurring_task))
                    }
                )
        }
    }
}
