package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskEvent
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel.CreateRecurringTaskViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Screen for creating a new recurring task
 */
@Composable
fun CreateRecurringTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateRecurringTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is RecurringTaskEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(event.message)
                    onNavigateBack()
                }
                is RecurringTaskEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_recurring_task_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.createRecurringTask() },
                        enabled = uiState.isValid && !uiState.isLoading
                    ) {
                        Text(stringResource(R.string.create))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        CreateRecurringTaskContent(
            uiState = uiState,
            onTitleChange = viewModel::updateTitle,
            onDescriptionChange = viewModel::updateDescription,
            onSectionChange = viewModel::updateSection,
            onPriorityChange = viewModel::updatePriority,
            onFrequencyChange = viewModel::updateFrequency,
            onIntervalChange = viewModel::updateInterval,
            onDaysOfWeekChange = viewModel::updateDaysOfWeek,
            onDayOfMonthChange = viewModel::updateDayOfMonth,
            onMonthOfYearChange = viewModel::updateMonthOfYear,
            onTimeOfDayChange = viewModel::updateTimeOfDay,
            onStartDateChange = viewModel::updateStartDate,
            onEndDateChange = viewModel::updateEndDate,
            getRecurrenceDescription = viewModel::getRecurrenceDescription,
            getNextOccurrences = viewModel::getNextOccurrences,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun CreateRecurringTaskContent(
    uiState: com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.CreateRecurringTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSectionChange: (TaskSection) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onFrequencyChange: (RecurrenceFrequency) -> Unit,
    onIntervalChange: (Int) -> Unit,
    onDaysOfWeekChange: (Set<DayOfWeek>) -> Unit,
    onDayOfMonthChange: (Int) -> Unit,
    onMonthOfYearChange: (Int) -> Unit,
    onTimeOfDayChange: (LocalTime) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    getRecurrenceDescription: () -> String,
    getNextOccurrences: (Int) -> List<LocalDate>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Basic Info Section
        item {
            Text(
                text = stringResource(R.string.recurring_task_basic_info),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.title.isBlank()
            )
        }
        
        item {
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
        
        item {
            SectionSelector(
                section = uiState.section,
                onSectionChange = onSectionChange
            )
        }
        
        item {
            PrioritySelector(
                priority = uiState.priority,
                onPriorityChange = onPriorityChange
            )
        }
        
        // Recurrence Pattern Section
        item {
            Text(
                text = stringResource(R.string.recurring_task_pattern),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            FrequencySelector(
                frequency = uiState.frequency,
                onFrequencyChange = onFrequencyChange
            )
        }
        
        item {
            IntervalSelector(
                frequency = uiState.frequency,
                interval = uiState.interval,
                onIntervalChange = onIntervalChange
            )
        }
        
        // Frequency-specific options
        when (uiState.frequency) {
            RecurrenceFrequency.WEEKLY -> {
                item {
                    DayOfWeekSelector(
                        selectedDays = uiState.daysOfWeek,
                        onDaysChange = onDaysOfWeekChange
                    )
                }
            }
            RecurrenceFrequency.MONTHLY -> {
                item {
                    DayOfMonthSelector(
                        dayOfMonth = uiState.dayOfMonth ?: 1,
                        onDayChange = onDayOfMonthChange
                    )
                }
            }
            RecurrenceFrequency.YEARLY -> {
                item {
                    MonthSelector(
                        month = uiState.monthOfYear ?: 1,
                        onMonthChange = onMonthOfYearChange
                    )
                }
                item {
                    DayOfMonthSelector(
                        dayOfMonth = uiState.dayOfMonth ?: 1,
                        onDayChange = onDayOfMonthChange
                    )
                }
            }
            else -> {}
        }
        
        item {
            TimeOfDaySelector(
                time = uiState.timeOfDay,
                onTimeChange = onTimeOfDayChange
            )
        }
        
        // Schedule Section
        item {
            Text(
                text = stringResource(R.string.recurring_task_schedule),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            DateSelector(
                label = stringResource(R.string.start_date),
                date = uiState.startDate,
                onDateChange = onStartDateChange
            )
        }
        
        item {
            OptionalDateSelector(
                label = stringResource(R.string.end_date),
                date = uiState.endDate,
                onDateChange = onEndDateChange
            )
        }
        
        // Preview
        item {
            RecurrencePreview(
                description = getRecurrenceDescription(),
                nextOccurrences = getNextOccurrences(5)
            )
        }
    }
}

@Composable
private fun SectionSelector(
    section: TaskSection,
    onSectionChange: (TaskSection) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.section),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            TaskSection.values().forEach { taskSection ->
                FilterChip(
                    selected = section == taskSection,
                    onClick = { onSectionChange(taskSection) },
                    label = { Text(taskSection.displayName) }
                )
            }
        }
    }
}

@Composable
private fun PrioritySelector(
    priority: Priority,
    onPriorityChange: (Priority) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.priority),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Priority.values().forEach { prio ->
                FilterChip(
                    selected = priority == prio,
                    onClick = { onPriorityChange(prio) },
                    label = { Text(prio.displayName) }
                )
            }
        }
    }
}

@Composable
private fun FrequencySelector(
    frequency: RecurrenceFrequency,
    onFrequencyChange: (RecurrenceFrequency) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.frequency),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            RecurrenceFrequency.values().forEach { freq ->
                FilterChip(
                    selected = frequency == freq,
                    onClick = { onFrequencyChange(freq) },
                    label = { Text(freq.getDisplayName()) }
                )
            }
        }
    }
}

@Composable
private fun IntervalSelector(
    frequency: RecurrenceFrequency,
    interval: Int,
    onIntervalChange: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.interval),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = interval.toString(),
            onValueChange = { 
                it.toIntOrNull()?.let { value ->
                    if (value > 0) onIntervalChange(value)
                }
            },
            label = { 
                Text(
                    when (frequency) {
                        RecurrenceFrequency.DAILY -> "Every X days"
                        RecurrenceFrequency.WEEKLY -> "Every X weeks"
                        RecurrenceFrequency.MONTHLY -> "Every X months"
                        RecurrenceFrequency.YEARLY -> "Every X years"
                    }
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DayOfWeekSelector(
    selectedDays: Set<DayOfWeek>,
    onDaysChange: (Set<DayOfWeek>) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.days_of_week),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DayOfWeek.values().forEach { day ->
                FilterChip(
                    selected = selectedDays.contains(day),
                    onClick = {
                        val newDays = if (selectedDays.contains(day)) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                        onDaysChange(newDays)
                    },
                    label = {
                        Text(day.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()))
                    }
                )
            }
        }
    }
}

@Composable
private fun DayOfMonthSelector(
    dayOfMonth: Int,
    onDayChange: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.day_of_month),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = dayOfMonth.toString(),
            onValueChange = { 
                it.toIntOrNull()?.let { value ->
                    if (value in 1..31) onDayChange(value)
                }
            },
            label = { Text("Day (1-31)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MonthSelector(
    month: Int,
    onMonthChange: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.month_of_year),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            java.time.Month.values().forEach { monthEnum ->
                FilterChip(
                    selected = month == monthEnum.value,
                    onClick = { onMonthChange(monthEnum.value) },
                    label = { 
                        Text(monthEnum.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()))
                    }
                )
            }
        }
    }
}

@Composable
private fun TimeOfDaySelector(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.time_of_day),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = time.toString(),
            onValueChange = { 
                try {
                    LocalTime.parse(it)?.let { parsedTime ->
                        onTimeChange(parsedTime)
                    }
                } catch (e: Exception) {
                    // Invalid time format, ignore
                }
            },
            label = { Text("Time (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DateSelector(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            onValueChange = { 
                try {
                    LocalDate.parse(it)?.let { parsedDate ->
                        onDateChange(parsedDate)
                    }
                } catch (e: Exception) {
                    // Invalid date format, ignore
                }
            },
            label = { Text("Date (yyyy-MM-dd)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun OptionalDateSelector(
    label: String,
    date: LocalDate?,
    onDateChange: (LocalDate?) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = date?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "",
            onValueChange = { 
                if (it.isBlank()) {
                    onDateChange(null)
                } else {
                    try {
                        LocalDate.parse(it)?.let { parsedDate ->
                            onDateChange(parsedDate)
                        }
                    } catch (e: Exception) {
                        // Invalid date format, ignore
                    }
                }
            },
            label = { Text("Date (yyyy-MM-dd) or leave empty") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RecurrencePreview(
    description: String,
    nextOccurrences: List<LocalDate>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.recurrence_preview),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (nextOccurrences.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.next_occurrences),
                    style = MaterialTheme.typography.labelMedium
                )
                
                nextOccurrences.forEach { date ->
                    Text(
                        text = "• ${date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.no_occurrences),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
