package com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state

import androidx.compose.runtime.Immutable
import com.letsgotoperfection.kino.core.common.UiState
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model.TaskDetail

/**
 * UI state for the task detail screen.
 */
@Immutable
data class TaskDetailUiState(
    val task: TaskDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val editMode: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val snackbarMessage: String? = null
)

/**
 * Actions that can be performed on the task detail screen.
 */
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
        val dueDate: java.time.LocalDateTime? = null
    ) : TaskDetailAction
    data class AttachMedia(val uris: List<android.net.Uri>) : TaskDetailAction
    data object DismissSnackbar : TaskDetailAction
}

/**
 * One-time events for the task detail screen.
 */
sealed interface TaskDetailEvent {
    data class ShowSnackbar(val message: String) : TaskDetailEvent
    data object NavigateBack : TaskDetailEvent
    data class NavigateToMedia(val mediaId: String) : TaskDetailEvent
}
