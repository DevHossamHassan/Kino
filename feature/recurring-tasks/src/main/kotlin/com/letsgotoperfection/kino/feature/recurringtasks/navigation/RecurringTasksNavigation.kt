package com.letsgotoperfection.kino.feature.recurringtasks.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letsgotoperfection.kino.feature.recurringtasks.api.CreateRecurringTaskScreenApi
import com.letsgotoperfection.kino.feature.recurringtasks.api.EditRecurringTaskScreenApi
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksListScreenApi
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskInstancesScreenApi
import kotlinx.serialization.Serializable

/**
 * Recurring Tasks feature routes - Type-safe navigation
 */
@Serializable
object RecurringTasksListRoute

@Serializable
object CreateRecurringTaskRoute

@Serializable
data class EditRecurringTaskRoute(
    val recurringTaskId: String
)

@Serializable
data class RecurringTaskInstancesRoute(
    val recurringTaskId: String
)

/**
 * Recurring Tasks navigation graph
 * Exposes composable screens for app module to wire
 */
fun NavGraphBuilder.recurringTasksGraph(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToInstances: (String) -> Unit
) {
    composable<RecurringTasksListRoute> {
        RecurringTasksListScreenApi(
            onNavigateBack = onNavigateBack,
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToEdit = onNavigateToEdit,
            onNavigateToInstances = onNavigateToInstances
        )
    }
    
    composable<CreateRecurringTaskRoute> {
        CreateRecurringTaskScreenApi(
            onNavigateBack = onNavigateBack
        )
    }
    
    composable<EditRecurringTaskRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<EditRecurringTaskRoute>()
        EditRecurringTaskScreenApi(
            recurringTaskId = route.recurringTaskId,
            onNavigateBack = onNavigateBack
        )
    }

    composable<RecurringTaskInstancesRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<RecurringTaskInstancesRoute>()
        RecurringTaskInstancesScreenApi(
            recurringTaskId = route.recurringTaskId,
            onNavigateBack = onNavigateBack
        )
    }
}

/**
 * Deep link patterns for recurring tasks feature
 */
object RecurringTasksDeepLinks {
    const val RECURRING_TASKS_LIST = "kino://app/recurring_tasks"
    const val CREATE_RECURRING_TASK = "kino://app/recurring_tasks/create"
    const val EDIT_RECURRING_TASK = "kino://app/recurring_tasks/edit/{recurringTaskId}"
    
    fun createEditRecurringTaskDeepLink(recurringTaskId: String) = 
        "kino://app/recurring_tasks/edit/$recurringTaskId"
}
