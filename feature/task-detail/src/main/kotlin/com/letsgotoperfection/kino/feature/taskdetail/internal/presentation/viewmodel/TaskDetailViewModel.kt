package com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.media.api.MediaApi
import com.letsgotoperfection.kino.feature.media.api.MediaSourceType
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model.TaskDetail
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.AddChecklistItemUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.DeleteChecklistItemUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.DeleteTaskUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.GetTaskDetailUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.ToggleChecklistItemUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase.UpdateTaskUseCase
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailAction
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailEvent
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
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
class TaskDetailViewModel @Inject constructor(
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val toggleChecklistItemUseCase: ToggleChecklistItemUseCase,
    private val addChecklistItemUseCase: AddChecklistItemUseCase,
    private val deleteChecklistItemUseCase: DeleteChecklistItemUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val mediaApi: MediaApi,
    private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val taskId: String = savedStateHandle.get<String>("taskId")
        ?: throw IllegalArgumentException(context.getString(R.string.error_task_id_required))
    
    private val _uiState = MutableStateFlow<TaskDetailUiState>(TaskDetailUiState(isLoading = true))
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = Channel<TaskDetailEvent>(Channel.BUFFERED)
    val uiEvent: Flow<TaskDetailEvent> = _uiEvent.receiveAsFlow()
    
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
                dueDate = action.dueDate,
                section = action.section,
                column = action.column
            )
            is TaskDetailAction.AttachMedia -> attachMedia(action.uris)
            is TaskDetailAction.DismissSnackbar -> dismissSnackbar()
        }
    }
    
    private fun loadTask() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                getTaskDetailUseCase(taskId).collect { taskDetail ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        taskDetail = taskDetail.toTask(),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: context.getString(R.string.error_failed_to_load_task)
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
        viewModelScope.launch {
            val currentTask = _uiState.value.taskDetail
            if (currentTask != null) {
                updateTaskUseCase(
                    taskId = currentTask.id,
                    title = currentTask.title,
                    description = currentTask.description,
                    priority = currentTask.priority,
                    dueDate = currentTask.dueDate,
                    section = currentTask.section,
                    column = currentTask.column
                ).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(editMode = false)
                        _uiEvent.send(TaskDetailEvent.TaskSaved)
                    },
                    onError = { error ->
                        _uiEvent.send(TaskDetailEvent.ShowSnackbar(context.getString(R.string.error_failed_to_save_changes, error.message)))
                    }
                )
            }
        }
    }
    
    private fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }
    
    private fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }
    
    private fun deleteTask() {
        viewModelScope.launch {
            deleteTaskUseCase(taskId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(showDeleteDialog = false)
                    _uiEvent.send(TaskDetailEvent.TaskDeleted)
                },
                onError = { error ->
                    _uiEvent.send(
                        TaskDetailEvent.ShowSnackbar(
                            context.getString(R.string.error_failed_to_delete_task, error.message)
                        )
                    )
                }
            )
        }
    }
    
    private fun toggleChecklistItem(itemId: String) {
        viewModelScope.launch {
            toggleChecklistItemUseCase(taskId, itemId)
                .fold(
                    onSuccess = {
                        _uiEvent.send(TaskDetailEvent.ShowSnackbar(context.getString(R.string.success_checklist_item_updated)))
                        loadTask()
                    },
                    onError = { error ->
                        _uiEvent.send(TaskDetailEvent.ShowSnackbar(context.getString(R.string.error_failed_to_update_checklist_item, error.message)))
                    }
                )
        }
    }
    
    private fun addChecklistItem(text: String) {
        viewModelScope.launch {
            addChecklistItemUseCase(taskId, text)
                .fold(
                    onSuccess = {
                        _uiEvent.send(TaskDetailEvent.ShowSnackbar(context.getString(R.string.success_checklist_item_added)))
                        loadTask()
                    },
                    onError = { error ->
                        _uiEvent.send(TaskDetailEvent.ShowSnackbar(context.getString(R.string.error_failed_to_add_checklist_item, error.message)))
                    }
                )
        }
    }
    
    private fun deleteChecklistItem(itemId: String) {
        viewModelScope.launch {
            deleteChecklistItemUseCase(taskId, itemId)
                .fold(
                    onSuccess = {
                        _uiEvent.send(TaskDetailEvent.ShowSnackbar(context.getString(R.string.success_checklist_item_deleted)))
                        loadTask()
                    },
                    onError = { error ->
                        _uiEvent.send(TaskDetailEvent.ShowSnackbar(context.getString(R.string.error_failed_to_delete_checklist_item, error.message)))
                    }
                )
        }
    }

    private fun updateTask(
        title: String? = null,
        description: String? = null,
        priority: Priority? = null,
        dueDate: LocalDateTime? = null,
        section: com.letsgotoperfection.kino.core.model.TaskSection? = null,
        column: com.letsgotoperfection.kino.core.model.TaskColumn? = null
    ) {
        viewModelScope.launch {
            updateTaskUseCase(
                taskId = taskId,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                section = section,
                column = column
            ).fold(
                onSuccess = {
                    _uiEvent.send(TaskDetailEvent.TaskSaved)
                    loadTask()
                },
                onError = { error ->
                    _uiEvent.send(TaskDetailEvent.ShowSnackbar(context.getString(R.string.error_failed_to_update_task, error.message)))
                }
            )
        }
    }
    
    private fun attachMedia(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            val results = mediaApi.attachMedia(
                uris = uris,
                sourceType = MediaSourceType.TASK,
                sourceId = taskId
            )

            val successCount = results.count { it is Result.Success<*> }
            val errorMessages = results
                .filterIsInstance<Result.Error>()
                .mapNotNull { it.exception.message }

            if (successCount > 0) {
                _uiEvent.send(
                    TaskDetailEvent.ShowSnackbar(
                        if (successCount == 1) context.getString(R.string.success_media_attached_single) else context.getString(R.string.success_media_attached_multiple, successCount)
                    )
                )
                loadTask()
            }

            if (errorMessages.isNotEmpty()) {
                _uiEvent.send(
                    TaskDetailEvent.ShowSnackbar(
                        errorMessages.firstOrNull() ?: context.getString(R.string.error_failed_to_attach_media)
                    )
                )
            }
        }
    }
    
    private fun dismissSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}

/**
 * Extension function to convert TaskDetail to Task for UI display
 */
private fun TaskDetail.toTask(): com.letsgotoperfection.kino.core.model.Task {
    return com.letsgotoperfection.kino.core.model.Task(
        id = this.id,
        title = this.title,
        description = this.description,
        section = this.section,
        column = this.column,
        priority = this.priority,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        dueDate = this.dueDate,
        progress = this.progress,
        labels = this.labels,
        checklist = this.checklist,
        attachments = this.attachments
    )
}
