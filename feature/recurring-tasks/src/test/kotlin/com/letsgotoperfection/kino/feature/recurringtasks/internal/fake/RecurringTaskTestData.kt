package com.letsgotoperfection.kino.feature.recurringtasks.internal.fake

import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Factory for recurring task test data.
 */
internal object RecurringTaskTestData {

    fun dailyTask(
        id: String = "task-1",
        title: String = "Daily standup",
        isActive: Boolean = true,
        startDate: LocalDate = LocalDate.of(2026, 1, 1),
        endDate: LocalDate? = null,
        defaultColumn: TaskColumn = TaskColumn.TODO_THIS_WEEK,
        checklistTemplate: List<String> = emptyList(),
        dueDateOffsetDays: Int = 0
    ) = RecurringTask(
        id = id,
        title = title,
        description = "Sync with the team",
        section = TaskSection.WORK,
        priority = Priority.MEDIUM,
        labels = emptyList(),
        recurrenceRule = RecurrenceRule(
            frequency = RecurrenceFrequency.DAILY,
            interval = 1,
            timeOfDay = LocalTime.of(9, 0)
        ),
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = LocalDateTime.of(2026, 1, 1, 8, 0),
        updatedAt = LocalDateTime.of(2026, 1, 1, 8, 0),
        lastGeneratedDate = null,
        defaultColumn = defaultColumn,
        checklistTemplate = checklistTemplate,
        dueDateOffsetDays = dueDateOffsetDays
    )
}
