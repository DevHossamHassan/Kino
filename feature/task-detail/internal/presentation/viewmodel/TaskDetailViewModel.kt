package com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.common.UiState
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.feature.media.api.MediaApi
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.AddChecklistItemUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.GetTaskDetailUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.ToggleChecklistItemUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.UpdateTaskUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailAction
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailEvent
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for the task detail screen.
 * Manages task detail state and handles user actions.
 */
@HiltViewModel
internal class TaskDetailViewModel @Inject constructor(
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val toggleChecklistItemUseCase: ToggleChecklistItemUseCase,
    private val addChecklistItemUseCase: AddChecklistItemUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val mediaApi: MediaApi,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val taskId: String = savedStateHandle.get<String>("taskId")
        ?: throw IllegalArgumentException("taskId is required")
    
    private val _uiState = MutableStateFlow<TaskDetailUiState>(TaskDetailUiState(isLoading = true))
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = Channel<TaskDetailEvent>(Channel.BUFFERED)
    val uiEvent: SharedFlow<TaskDetailEvent> = _uiEvent.receiveAsFlow()
    
    init {
        loadTask()
    }
    
    fun onAction(action: TaskDetailAction) {
        when (action) {
            is TaskDetailAction.LoadTask -> loadTask()
            is TaskDetailAction.ToggleEditMode -> toggleEditMode()
            is TaskDetailAction.SaveChanges -> saveChanges()
            is TaskDetailAction.ShowDeleteDialog -> showDeleteDialog()
            is TaskDetailAction.HideDeleteDialog -> hideDeleteDialog()
            is TaskDetailAction.DeleteTask -> deleteTask()
            is TaskDetailAction.ToggleChecklistItem -> toggleChecklistItem(action.itemId)
            is TaskDetailAction.AddChecklistItem -> addChecklistItem(action.text)
            is TaskDetailAction.DeleteChecklistItem -> deleteChecklistItem(action.itemId)
            is TaskDetailAction.UpdateTask -> updateTask(
                title = action.title,
                description = action.description,
                priority = action.priority,
                dueDate = action.dueDate
            )
            is TaskDetailAction.AttachMedia -> attachMedia(action.uris)
            is TaskDetailAction.DismissSnackbar -> dismissSnackbar()
        }
    }
    
    private fun loadTask() {
        viewModelScope.launch {
            getTaskDetailUseCase(taskId)
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load task"
                    )
                }
                .collect { task ->
                    _uiState.value = _uiState.value.copy(
                        task = task,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }
    
    private fun toggleEditMode() {
        _uiState.value = _uiState.value.copy(
            editMode = !_uiState.value.editMode
        )
    }
    
    private fun saveChanges() {
        // This will be handled by the UI when the user clicks save
        _uiState.value = _uiState.value.copy(editMode = false)
    }
    
    private fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }
    
    private fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }
    
    private fun deleteTask() {
        viewModelScope.launch {
            // TODO: Implement delete task use case
            _uiEvent.send(TaskDetailEvent.NavigateBack)
        }
    }
    
    private fun toggleChecklistItem(itemId: String) {
        viewModelScope.launch {
            toggleChecklistItemUseCase(taskId, itemId)
                .onFailure { error ->
                    _uiEvent.send(TaskDetailEvent.ShowSnackbar(
                        error.message ?: "Failed to update checklist"
                    ))
                }
        }
    }
    
    private fun addChecklistItem(text: String) {
        viewModelScope.launch {
            addChecklistItemUseCase(taskId, text)
                .onFailure { error ->
                    _uiEvent.send(TaskDetailEvent.ShowSnackbar(
                        error.message ?: "Failed to add checklist item"
                    ))
                }
        }
    }
    
    private fun deleteChecklistItem(itemId: String) {
        viewModelScope.launch {
            // TODO: Implement delete checklist item use case
        }
    }
    
    private fun updateTask(
        title: String? = null,
        description: String? = null,
        priority: Priority? = null,
        dueDate: LocalDateTime? = null
    ) {
        viewModelScope.launch {
            updateTaskUseCase(
                taskId = taskId,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate
            ).fold(
                onSuccess = {
                    _uiEvent.send(TaskDetailEvent.ShowSnackbar("Task updated"))
                },
                onFailure = { error ->
                    _uiEvent.send(TaskDetailEvent.ShowSnackbar(
                        error.message ?: "Failed to update task"
                    ))
                }
            )
        }
    }
    
    private fun attachMedia(uris: List<android.net.Uri>) {
        viewModelScope.launch {
            uris.forEach { uri ->
                mediaApi.attachMedia(
                    uri = uri,
                    sourceType = com.letsgotoperfection.kino.feature.media.api.MediaSourceType.TASK,
                    sourceId = taskId
                ).onFailure { error ->
                    _uiEvent.send(TaskDetailEvent.ShowSnackbar(
                        error.message ?: "Failed to attach media"
                    ))
                }
            }
        }
    }
    
    private fun dismissSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
