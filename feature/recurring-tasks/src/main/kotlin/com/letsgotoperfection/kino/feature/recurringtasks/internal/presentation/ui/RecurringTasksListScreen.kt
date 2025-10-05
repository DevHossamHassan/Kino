package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskAction
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskEvent
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel.RecurringTasksViewModel
import java.time.format.DateTimeFormatter

/**
 * Screen displaying the list of recurring tasks
 */
@Composable
fun RecurringTasksListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToInstances: (String) -> Unit,
    viewModel: RecurringTasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is RecurringTaskEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is RecurringTaskEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recurring_tasks_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_recurring_task))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorScreen(
                    message = uiState.error!!,
                    onRetry = { viewModel.onAction(RecurringTaskAction.RefreshRecurringTasks) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.recurringTasks.isEmpty() -> {
                EmptyRecurringTasksState(
                    onCreateClick = onNavigateToCreate,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                RecurringTasksList(
                    tasks = uiState.recurringTasks,
                    onTaskClick = onNavigateToEdit,
                    onViewInstances = onNavigateToInstances,
                    onToggleActive = { id, isActive ->
                        viewModel.onAction(RecurringTaskAction.ToggleRecurringTaskActive(id, isActive))
                    },
                    onDelete = { id ->
                        viewModel.onAction(RecurringTaskAction.DeleteRecurringTask(id))
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun RecurringTasksList(
    tasks: List<RecurringTask>,
    onTaskClick: (String) -> Unit,
    onViewInstances: (String) -> Unit,
    onToggleActive: (String, Boolean) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            RecurringTaskCard(
                task = task,
                onClick = { onTaskClick(task.id) },
                onViewInstances = { onViewInstances(task.id) },
                onToggleActive = { onToggleActive(task.id, !task.isActive) },
                onDelete = { onDelete(task.id) }
            )
        }
    }
}

@Composable
private fun RecurringTaskCard(
    task: RecurringTask,
    onClick: () -> Unit,
    onViewInstances: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row {
                    IconButton(onClick = onViewInstances) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = stringResource(R.string.cd_view_instances)
                        )
                    }
                    
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                }
            }
            
            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Recurrence description
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Loop,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = getRecurrenceDescription(task),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Status and end date
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (task.isActive) stringResource(R.string.active)
                            else stringResource(R.string.paused)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (task.isActive) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
                
                if (task.endDate != null) {
                    Text(
                        text = stringResource(
                            R.string.ends_on,
                            task.endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = stringResource(R.string.never_ends),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        if (task.isActive) stringResource(R.string.pause)
                        else stringResource(R.string.resume)
                    )
                },
                onClick = {
                    onToggleActive()
                    showMenu = false
                },
                leadingIcon = {
                    Icon(
                        if (task.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                }
            )
            
            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete)) },
                onClick = {
                    onDelete()
                    showMenu = false
                },
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            )
        }
    }
}

@Composable
private fun EmptyRecurringTasksState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Loop,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.no_recurring_tasks),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.no_recurring_tasks_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(24.dp))
        
        Button(onClick = onCreateClick) {
            Text(stringResource(R.string.create_recurring_task_title))
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(Modifier.height(16.dp))
        
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun getRecurrenceDescription(task: RecurringTask): String {
    val rule = task.recurrenceRule
    return when (rule.frequency) {
        com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency.DAILY -> {
            if (rule.interval == 1) "Every day at ${rule.timeOfDay}"
            else "Every ${rule.interval} days at ${rule.timeOfDay}"
        }
        com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency.WEEKLY -> {
            val daysStr = rule.daysOfWeek
                .sorted()
                .joinToString(", ") { it.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()) }
            
            if (rule.interval == 1) "Every week on $daysStr at ${rule.timeOfDay}"
            else "Every ${rule.interval} weeks on $daysStr at ${rule.timeOfDay}"
        }
        com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency.MONTHLY -> {
            val day = rule.dayOfMonth ?: 1
            if (rule.interval == 1) "Monthly on day $day at ${rule.timeOfDay}"
            else "Every ${rule.interval} months on day $day at ${rule.timeOfDay}"
        }
        com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency.YEARLY -> {
            val month = java.time.Month.of(rule.monthOfYear ?: 1)
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())
            val day = rule.dayOfMonth ?: 1
            
            if (rule.interval == 1) "Yearly on $month $day at ${rule.timeOfDay}"
            else "Every ${rule.interval} years on $month $day at ${rule.timeOfDay}"
        }
    }
}
