package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency

/**
 * Localized display names for domain enums used across the recurring tasks UI.
 */

@Composable
internal fun TaskSection.localizedName(): String = stringResource(
    when (this) {
        TaskSection.PERSONAL -> R.string.section_personal
        TaskSection.WORK -> R.string.section_work
        TaskSection.FAMILY -> R.string.section_family
    }
)

@Composable
internal fun TaskColumn.localizedName(): String = stringResource(
    when (this) {
        TaskColumn.BACKLOG -> R.string.column_backlog
        TaskColumn.TODO_THIS_WEEK -> R.string.column_todo_this_week
        TaskColumn.IN_PROGRESS -> R.string.column_in_progress
        TaskColumn.PENDING -> R.string.column_pending
        TaskColumn.DONE -> R.string.column_done
    }
)

@Composable
internal fun Priority.localizedName(): String = stringResource(
    when (this) {
        Priority.HIGH -> R.string.priority_high
        Priority.MEDIUM -> R.string.priority_medium
        Priority.LOW -> R.string.priority_low
    }
)

@Composable
internal fun RecurrenceFrequency.localizedName(): String = stringResource(
    when (this) {
        RecurrenceFrequency.DAILY -> R.string.daily
        RecurrenceFrequency.WEEKLY -> R.string.weekly
        RecurrenceFrequency.MONTHLY -> R.string.monthly
        RecurrenceFrequency.YEARLY -> R.string.yearly
    }
)
