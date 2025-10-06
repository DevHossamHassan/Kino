package com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state

import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import java.time.LocalDateTime

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val taskDetail: Task? = null,
    val error: String? = null,
    val editMode: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val snackbarMessage: String? = null,
    val editForm: TaskEditForm? = null
)

data class TaskEditForm(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val section: TaskSection = TaskSection.PERSONAL,
    val column: TaskColumn = TaskColumn.BACKLOG,
    val dueDate: LocalDateTime? = null
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
    data class EditTitle(val title: String) : TaskDetailAction
    data class EditDescription(val description: String) : TaskDetailAction
    data class EditPriority(val priority: Priority) : TaskDetailAction
    data class EditSection(val section: TaskSection) : TaskDetailAction
    data class EditColumn(val column: TaskColumn) : TaskDetailAction
    data class EditDueDate(val dueDate: LocalDateTime?) : TaskDetailAction
    data class UpdateTask(
        val title: String? = null,
        val description: String? = null,
        val priority: Priority? = null,
        val dueDate: LocalDateTime? = null,
        val section: TaskSection? = null,
        val column: TaskColumn? = null,
        val dueDateExplicit: Boolean = false
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
