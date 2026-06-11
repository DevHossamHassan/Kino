package com.letsgotoperfection.kino.feature.recurringtasks.internal.notification

import android.content.Context
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.notifications.api.UltraSimpleNotificationApi
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.internal.notification.RecurringTaskNotificationService.RecurringTaskInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [RecurringTaskNotificationService] backed by the notifications feature API.
 */
@Singleton
class RecurringTaskNotificationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationApi: UltraSimpleNotificationApi
) : RecurringTaskNotificationService {

    override suspend fun sendTaskCreated(
        taskTitle: String,
        column: TaskColumn,
        section: TaskSection,
        taskId: String
    ) {
        notificationApi.send(
            channelId = CHANNEL_ID,
            title = context.getString(R.string.notification_recurring_task_created_title),
            message = context.getString(
                R.string.notification_recurring_task_created_message,
                taskTitle,
                columnDisplayName(column),
                sectionDisplayName(section)
            ),
            icon = R.drawable.ic_recurring_task,
            deepLink = "kino://app/task/$taskId"
        )
    }

    override suspend fun sendTasksGrouped(tasks: List<RecurringTaskInfo>) {
        if (tasks.isEmpty()) return

        if (tasks.size == 1) {
            val task = tasks.first()
            sendTaskCreated(
                taskTitle = task.taskTitle,
                column = task.column,
                section = task.section,
                taskId = task.taskId
            )
            return
        }

        val lines = tasks.take(MAX_GROUPED_LINES).map { task ->
            context.getString(
                R.string.notification_recurring_task_group_line,
                task.taskTitle,
                columnDisplayName(task.column),
                sectionDisplayName(task.section)
            )
        }

        val summaryText = if (tasks.size > MAX_GROUPED_LINES) {
            context.getString(
                R.string.notification_recurring_tasks_group_summary_more,
                tasks.size - MAX_GROUPED_LINES
            )
        } else {
            context.getString(R.string.notification_recurring_tasks_group_summary)
        }

        notificationApi.sendGrouped(
            channelId = CHANNEL_ID,
            title = context.resources.getQuantityString(
                R.plurals.notification_recurring_tasks_created_title,
                tasks.size,
                tasks.size
            ),
            summaryText = summaryText,
            items = lines,
            icon = R.drawable.ic_recurring_task,
            deepLink = "kino://app/kanban"
        )
    }

    private fun sectionDisplayName(section: TaskSection): String = when (section) {
        TaskSection.PERSONAL -> context.getString(R.string.section_personal)
        TaskSection.WORK -> context.getString(R.string.section_work)
        TaskSection.FAMILY -> context.getString(R.string.section_family)
    }

    private fun columnDisplayName(column: TaskColumn): String = when (column) {
        TaskColumn.BACKLOG -> context.getString(R.string.column_backlog)
        TaskColumn.TODO_THIS_WEEK -> context.getString(R.string.column_todo_this_week)
        TaskColumn.IN_PROGRESS -> context.getString(R.string.column_in_progress)
        TaskColumn.PENDING -> context.getString(R.string.column_pending)
        TaskColumn.DONE -> context.getString(R.string.column_done)
    }

    companion object {
        private const val CHANNEL_ID = "recurring_tasks"
        private const val MAX_GROUPED_LINES = 5
    }
}
