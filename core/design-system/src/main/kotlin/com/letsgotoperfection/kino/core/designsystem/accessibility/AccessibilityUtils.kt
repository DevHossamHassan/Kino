package com.letsgotoperfection.kino.core.designsystem.accessibility

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.core.model.TaskColumn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Comprehensive accessibility utilities for the Kino app.
 * Provides consistent accessibility support across all UI components.
 */
object AccessibilityUtils {

    /**
     * Creates a comprehensive content description for a task card
     */
    fun createTaskCardDescription(task: Task): String = buildString {
        append("Task: ${task.title}")
        if (task.description.isNotBlank()) {
            append(". Description: ${task.description}")
        }
        append(". Priority: ${task.priority.displayName}")
        append(". Section: ${task.section.displayName}")
        append(". Column: ${task.column.displayName}")
        
        val progressPercent = task.progress.coerceIn(0, 100)
        if (progressPercent > 0) {
            append(". Progress: $progressPercent percent")
        }
        
        if (task.attachments.isNotEmpty()) {
            append(". Attachments: ${task.attachments.size}")
        }
        
        if (task.checklist.isNotEmpty()) {
            val completedCount = task.checklist.count { it.isCompleted }
            append(". Checklist: $completedCount of ${task.checklist.size} completed")
        }
        
        if (task.dueDate != null) {
            append(". Due date: ${formatDate(task.dueDate as java.time.LocalDateTime)}")
        }
        
        if (task.labels.isNotEmpty()) {
            val labelNames = task.labels.take(3).map { it.name }
            append(". Labels: ${labelNames.joinToString(", ")}")
            if (task.labels.size > 3) {
                append(" and ${task.labels.size - 3} more")
            }
        }
    }

    /**
     * Creates a content description for a progress indicator
     */
    fun createProgressDescription(current: Int, total: Int): String {
        val percentage = if (total > 0) (current * 100 / total) else 0
        return "Progress: $current of $total completed, $percentage percent"
    }

    /**
     * Creates a content description for a priority indicator
     */
    fun createPriorityDescription(priority: Priority): String {
        return when (priority) {
            Priority.HIGH -> "High priority"
            Priority.MEDIUM -> "Medium priority"
            Priority.LOW -> "Low priority"
        }
    }

    /**
     * Creates a content description for a section
     */
    fun createSectionDescription(section: TaskSection, taskCount: Int): String {
        val sectionName = when (section) {
            TaskSection.PERSONAL -> "Personal"
            TaskSection.WORK -> "Work"
            TaskSection.FAMILY -> "Family"
        }
        return "$sectionName section with $taskCount tasks"
    }

    /**
     * Creates a content description for a column
     */
    fun createColumnDescription(column: TaskColumn, taskCount: Int): String {
        val columnName = when (column) {
            TaskColumn.BACKLOG -> "Backlog"
            TaskColumn.TODO_THIS_WEEK -> "To do this week"
            TaskColumn.IN_PROGRESS -> "In progress"
            TaskColumn.PENDING -> "Pending"
            TaskColumn.DONE -> "Done"
        }
        return "$columnName column with $taskCount tasks"
    }

    /**
     * Creates a content description for drag and drop operations
     */
    fun createDragDescription(taskTitle: String, targetColumn: String): String {
        return "Drag task: $taskTitle to $targetColumn column"
    }

    /**
     * Creates a content description for empty states
     */
    fun createEmptyStateDescription(emptyType: EmptyStateType): String {
        return when (emptyType) {
            EmptyStateType.TASKS -> "No tasks in this section"
            EmptyStateType.NOTES -> "No notes available"
            EmptyStateType.MEDIA -> "No media files"
            EmptyStateType.NOTIFICATIONS -> "No notifications"
            EmptyStateType.SEARCH -> "No search results"
        }
    }

    /**
     * Creates a content description for loading states
     */
    fun createLoadingDescription(loadingType: LoadingType): String {
        return when (loadingType) {
            LoadingType.TASKS -> "Loading tasks"
            LoadingType.NOTES -> "Loading notes"
            LoadingType.MEDIA -> "Loading media"
            LoadingType.SETTINGS -> "Loading settings"
        }
    }

    /**
     * Creates a content description for error states
     */
    fun createErrorDescription(errorType: ErrorType): String {
        return when (errorType) {
            ErrorType.NETWORK -> "Network connection error"
            ErrorType.LOADING -> "Failed to load data"
            ErrorType.SAVING -> "Failed to save data"
            ErrorType.PERMISSION -> "Permission denied"
            ErrorType.STORAGE -> "Storage error"
            ErrorType.UNKNOWN -> "Unknown error occurred"
        }
    }

    /**
     * Creates a content description for success states
     */
    fun createSuccessDescription(successType: SuccessType): String {
        return when (successType) {
            SuccessType.TASK_CREATED -> "Task created successfully"
            SuccessType.TASK_UPDATED -> "Task updated successfully"
            SuccessType.TASK_DELETED -> "Task deleted successfully"
            SuccessType.NOTE_CREATED -> "Note created successfully"
            SuccessType.NOTE_UPDATED -> "Note updated successfully"
            SuccessType.NOTE_DELETED -> "Note deleted successfully"
            SuccessType.MEDIA_UPLOADED -> "Media uploaded successfully"
            SuccessType.SETTINGS_SAVED -> "Settings saved successfully"
        }
    }

    /**
     * Creates a content description for media items
     */
    fun createMediaDescription(
        filename: String,
        fileType: String,
        size: String? = null,
        duration: String? = null
    ): String = buildString {
        append("Media file: $filename")
        append(", Type: $fileType")
        size?.let { append(", Size: $it") }
        duration?.let { append(", Duration: $it") }
    }

    /**
     * Creates a content description for form controls
     */
    fun createFormControlDescription(
        controlType: FormControlType,
        label: String,
        isRequired: Boolean = false,
        hasError: Boolean = false
    ): String = buildString {
        append("$label input field")
        if (isRequired) append(", required")
        if (hasError) append(", has error")
    }

    /**
     * Creates a content description for navigation elements
     */
    fun createNavigationDescription(
        elementType: NavigationType,
        destination: String,
        isSelected: Boolean = false
    ): String = buildString {
        append("$destination $elementType")
        if (isSelected) append(", selected")
    }

    /**
     * Formats a date for accessibility
     */
    private fun formatDate(date: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        return date.format(formatter)
    }

    /**
     * Creates a modifier with comprehensive accessibility support
     */
    fun Modifier.accessibility(
        contentDescription: String? = null,
        role: Role? = null,
        stateDescription: String? = null,
        testTag: String? = null
    ): Modifier = this.semantics {
        contentDescription?.let { this.contentDescription = it }
        role?.let { this.role = it }
        stateDescription?.let { this.stateDescription = it }
        testTag?.let { this.testTag = it }
    }
}

/**
 * Enum for different empty state types
 */
enum class EmptyStateType {
    TASKS, NOTES, MEDIA, NOTIFICATIONS, SEARCH
}

/**
 * Enum for different loading state types
 */
enum class LoadingType {
    TASKS, NOTES, MEDIA, SETTINGS
}

/**
 * Enum for different error state types
 */
enum class ErrorType {
    NETWORK, LOADING, SAVING, PERMISSION, STORAGE, UNKNOWN
}

/**
 * Enum for different success state types
 */
enum class SuccessType {
    TASK_CREATED, TASK_UPDATED, TASK_DELETED,
    NOTE_CREATED, NOTE_UPDATED, NOTE_DELETED,
    MEDIA_UPLOADED, SETTINGS_SAVED
}

/**
 * Enum for different form control types
 */
enum class FormControlType {
    TEXT_INPUT, DROPDOWN, CHECKBOX, RADIO_BUTTON, SWITCH, SLIDER, BUTTON
}

/**
 * Enum for different navigation element types
 */
enum class NavigationType {
    TAB, BUTTON, LINK, MENU_ITEM
}
