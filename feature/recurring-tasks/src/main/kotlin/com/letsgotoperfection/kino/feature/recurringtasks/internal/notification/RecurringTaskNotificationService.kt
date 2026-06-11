package com.letsgotoperfection.kino.feature.recurringtasks.internal.notification

import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection

/**
 * Contract for sending localized recurring task notifications.
 *
 * Abstracted from the Android implementation so use cases that notify the
 * user stay unit-testable on the JVM.
 *
 * @see RecurringTaskNotificationServiceImpl for the notification API-backed implementation
 */
interface RecurringTaskNotificationService {

    /**
     * Notify the user that a task instance was created from a recurring template.
     */
    suspend fun sendTaskCreated(
        taskTitle: String,
        column: TaskColumn,
        section: TaskSection,
        taskId: String
    )

    /**
     * Notify the user about multiple task instances created at once.
     */
    suspend fun sendTasksGrouped(tasks: List<RecurringTaskInfo>)

    /**
     * Data class for recurring task notification information.
     */
    data class RecurringTaskInfo(
        val taskTitle: String,
        val column: TaskColumn,
        val section: TaskSection,
        val taskId: String
    )
}
