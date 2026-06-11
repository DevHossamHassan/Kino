package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskInstancesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the Recurring Task Instances screen.
 *
 * Loads the recurring task template and observes the task instances that have
 * been generated from it, from its start date through the upcoming window.
 */
@HiltViewModel
class RecurringTaskInstancesViewModel @Inject constructor(
    private val recurringTasksApi: RecurringTasksApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecurringTaskInstancesUiState())
    val uiState: StateFlow<RecurringTaskInstancesUiState> = _uiState.asStateFlow()

    fun load(recurringTaskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }

            recurringTasksApi.getRecurringTask(recurringTaskId).fold(
                onSuccess = { recurringTask ->
                    _uiState.update { it.copy(recurringTask = recurringTask) }
                    observeInstances(recurringTaskId, recurringTask.startDate)
                },
                onFailure = {
                    _uiState.update {
                        it.copy(isLoading = false, errorRes = R.string.error_loading_recurring_task)
                    }
                }
            )
        }
    }

    private fun observeInstances(recurringTaskId: String, startDate: LocalDate) {
        viewModelScope.launch {
            recurringTasksApi.getTaskInstances(
                recurringTaskId = recurringTaskId,
                fromDate = startDate,
                toDate = LocalDate.now().plusMonths(UPCOMING_WINDOW_MONTHS)
            )
                .catch {
                    _uiState.update {
                        it.copy(isLoading = false, errorRes = R.string.error_loading_recurring_task)
                    }
                }
                .collect { instances ->
                    _uiState.update {
                        it.copy(
                            instances = instances.sortedByDescending { task -> task.scheduledDate },
                            isLoading = false,
                            errorRes = null
                        )
                    }
                }
        }
    }

    companion object {
        private const val UPCOMING_WINDOW_MONTHS = 2L
    }
}
