package com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state

import com.letsgotoperfection.kino.core.model.Task

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val taskDetail: Task? = null,
    val error: String? = null,
    val editMode: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val snackbarMessage: String? = null
)

sealed interface TaskDetailAction {
    data object LoadTask : TaskDetailAction
    data object ToggleEditMode : TaskDetailAction
    data object SaveChanges : TaskDetailAction
    data object ShowDeleteDialog : TaskDetailAction
    data object HideDeleteDialog : TaskDetailAction
    data object DeleteTask : TaskDetailAction
    data class ToggleChecklistItem(val itemId: String) : TaskDetailAction
    data class AddChecklistItem(val text: String) : TaskDetailAction
    data class DeleteChecklistItem(val itemId: String) : TaskDetailAction
    data class UpdateTask(
        val title: String? = null,
        val description: String? = null,
        val priority: com.letsgotoperfection.kino.core.model.Priority? = null,
        val dueDate: java.time.LocalDateTime? = null,
        val section: com.letsgotoperfection.kino.core.model.TaskSection? = null,
        val column: com.letsgotoperfection.kino.core.model.TaskColumn? = null
    ) : TaskDetailAction
    data class AttachMedia(val uris: List<android.net.Uri>) : TaskDetailAction
    data object DismissSnackbar : TaskDetailAction
}

sealed interface TaskDetailEvent {
    data class ShowSnackbar(val message: String) : TaskDetailEvent
    data object TaskSaved : TaskDetailEvent
    data object TaskDeleted : TaskDetailEvent
    // Navigation events removed - handled by callbacks in the UI layer
}