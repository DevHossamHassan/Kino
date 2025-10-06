package com.letsgotoperfection.kino.feature.recurringtasks.api

import androidx.compose.runtime.Composable
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui.CreateRecurringTaskScreen
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui.EditRecurringTaskScreen
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui.RecurringTasksListScreen
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui.RecurringTaskInstancesScreen

/**
 * Public API composables for Recurring Tasks feature.
 * 
 * These composables expose the internal UI screens to the navigation module
 * while maintaining proper encapsulation.
 */

/**
 * Recurring Tasks List Screen - Public API
 */
@Composable
fun RecurringTasksListScreenApi(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToInstances: (String) -> Unit
) {
    RecurringTasksListScreen(
        onNavigateBack = onNavigateBack,
        onNavigateToCreate = onNavigateToCreate,
        onNavigateToEdit = onNavigateToEdit,
        onNavigateToInstances = onNavigateToInstances
    )
}

/**
 * Create Recurring Task Screen - Public API
 */
@Composable
fun CreateRecurringTaskScreenApi(
    onNavigateBack: () -> Unit
) {
    CreateRecurringTaskScreen(
        onNavigateBack = onNavigateBack
    )
}

/**
 * Edit Recurring Task Screen - Public API
 */
@Composable
fun EditRecurringTaskScreenApi(
    recurringTaskId: String,
    onNavigateBack: () -> Unit
) {
    EditRecurringTaskScreen(
        recurringTaskId = recurringTaskId,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Recurring Task Instances Screen - Public API
 */
@Composable
fun RecurringTaskInstancesScreenApi(
    recurringTaskId: String,
    onNavigateBack: () -> Unit
) {
    RecurringTaskInstancesScreen(
        recurringTaskId = recurringTaskId,
        onNavigateBack = onNavigateBack
    )
}
