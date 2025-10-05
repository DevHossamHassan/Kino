package com.letsgotoperfection.kino.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    fun loadTasks() {
        viewModelScope.launch {
            combine(
                taskRepository.getTasksBySection(TaskSection.PERSONAL),
                taskRepository.getTasksBySection(TaskSection.WORK),
                taskRepository.getTasksBySection(TaskSection.FAMILY)
            ) { personalTasks, workTasks, familyTasks ->
                TaskUiState(
                    personalTasks = personalTasks,
                    workTasks = workTasks,
                    familyTasks = familyTasks,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun createTask(
        title: String,
        description: String = "",
        section: TaskSection = TaskSection.PERSONAL,
        column: TaskColumn = TaskColumn.TODO_THIS_WEEK,
        priority: Priority = Priority.MEDIUM,
        dueDate: LocalDateTime? = null,
        progress: Int = 0
    ) {
        viewModelScope.launch {
            try {
                taskRepository.createTask(
                    title = title,
                    description = description,
                    section = section,
                    column = column,
                    priority = priority,
                    dueDate = dueDate,
                    progress = progress
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to create task"
                )
            }
        }
    }
    
    fun updateTaskProgress(taskId: String, progress: Int) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskProgress(taskId, progress)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to update task progress"
                )
            }
        }
    }
    
    fun updateTaskColumn(taskId: String, column: TaskColumn) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskColumn(taskId, column)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to update task column"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class TaskUiState(
    val personalTasks: List<Task> = emptyList(),
    val workTasks: List<Task> = emptyList(),
    val familyTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)





