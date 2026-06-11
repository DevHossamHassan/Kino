package com.letsgotoperfection.kino.feature.recurringtasks.internal.fake

import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.internal.notification.RecurringTaskNotificationService
import com.letsgotoperfection.kino.feature.recurringtasks.internal.notification.RecurringTaskNotificationService.RecurringTaskInfo

/**
 * Recording fake of [RecurringTaskNotificationService] for unit tests.
 */
internal class FakeRecurringTaskNotificationService : RecurringTaskNotificationService {

    val singleNotifications = mutableListOf<RecurringTaskInfo>()
    val groupedNotifications = mutableListOf<List<RecurringTaskInfo>>()

    override suspend fun sendTaskCreated(
        taskTitle: String,
        column: TaskColumn,
        section: TaskSection,
        taskId: String
    ) {
        singleNotifications += RecurringTaskInfo(
            taskTitle = taskTitle,
            column = column,
            section = section,
            taskId = taskId
        )
    }

    override suspend fun sendTasksGrouped(tasks: List<RecurringTaskInfo>) {
        groupedNotifications += tasks
    }
}
