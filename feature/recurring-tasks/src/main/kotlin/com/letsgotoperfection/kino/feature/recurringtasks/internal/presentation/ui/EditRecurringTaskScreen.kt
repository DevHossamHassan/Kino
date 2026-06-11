package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.EditRecurringTaskUiState
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskAction
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskEvent
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel.EditRecurringTaskViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Screen for editing an existing recurring task
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecurringTaskScreen(
    recurringTaskId: String,
    onNavigateBack: () -> Unit,
    viewModel: EditRecurringTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(recurringTaskId) {
        viewModel.loadRecurringTask(recurringTaskId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is RecurringTaskEvent.ShowError -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                }
                is RecurringTaskEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_recurring_task_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.onAction(
                                RecurringTaskAction.UpdateRecurringTask(
                                    id = uiState.recurringTask?.id ?: return@TextButton,
                                    title = uiState.title,
                                    description = uiState.description,
                                    section = uiState.section,
                                    priority = uiState.priority,
                                    labels = uiState.labels,
                                    frequency = uiState.frequency,
                                    interval = uiState.interval,
                                    daysOfWeek = uiState.daysOfWeek,
                                    dayOfMonth = uiState.dayOfMonth,
                                    monthOfYear = uiState.monthOfYear,
                                    timeOfDay = uiState.timeOfDay,
                                    startDate = uiState.startDate,
                                    endDate = uiState.endDate,
                                    isActive = uiState.isActive
                                )
                            )
                        },
                        enabled = uiState.isValid && !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text(stringResource(R.string.save))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorRes != null -> {
                    EditErrorState(
                        messageRes = uiState.errorRes!!,
                        onRetry = { viewModel.loadRecurringTask(recurringTaskId) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    EditRecurringTaskContent(
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
                        onActiveChange = viewModel::updateActiveStatus
                    )
                }
            }
        }
    }
}

@Composable
private fun EditErrorState(
    @StringRes messageRes: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(messageRes),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun EditRecurringTaskContent(
    uiState: EditRecurringTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSectionChange: (TaskSection) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onFrequencyChange: (RecurrenceFrequency) -> Unit,
    onIntervalChange: (Int) -> Unit,
    onDaysOfWeekChange: (Set<java.time.DayOfWeek>) -> Unit,
    onDayOfMonthChange: (Int) -> Unit,
    onMonthOfYearChange: (Int) -> Unit,
    onTimeOfDayChange: (LocalTime) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    onActiveChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.title.isBlank()
        )

        // Description
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            isError = uiState.description.isBlank()
        )

        // Section and Priority
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                EnumDropdownField(
                    label = stringResource(R.string.section),
                    value = uiState.section.localizedName(),
                    options = TaskSection.entries,
                    optionLabel = { it.localizedName() },
                    onSelected = onSectionChange
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                EnumDropdownField(
                    label = stringResource(R.string.priority),
                    value = uiState.priority.localizedName(),
                    options = Priority.entries,
                    optionLabel = { it.localizedName() },
                    onSelected = onPriorityChange
                )
            }
        }

        // Recurrence Settings
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.recurring_task_pattern),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                EnumDropdownField(
                    label = stringResource(R.string.frequency),
                    value = uiState.frequency.localizedName(),
                    options = RecurrenceFrequency.entries,
                    optionLabel = { it.localizedName() },
                    onSelected = onFrequencyChange
                )

                OutlinedTextField(
                    value = uiState.interval.toString(),
                    onValueChange = { text ->
                        text.toIntOrNull()?.let { interval ->
                            if (interval > 0) onIntervalChange(interval)
                        }
                    },
                    label = { Text(stringResource(R.string.interval)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.interval <= 0
                )

                // Frequency-specific fields
                when (uiState.frequency) {
                    RecurrenceFrequency.WEEKLY -> {
                        EditDaysOfWeekSelector(
                            selectedDays = uiState.daysOfWeek,
                            onDaysChange = onDaysOfWeekChange
                        )
                    }
                    RecurrenceFrequency.MONTHLY -> {
                        EditDayOfMonthField(
                            dayOfMonth = uiState.dayOfMonth,
                            onDayChange = onDayOfMonthChange
                        )
                    }
                    RecurrenceFrequency.YEARLY -> {
                        EditMonthSelector(
                            month = uiState.monthOfYear,
                            onMonthChange = onMonthOfYearChange
                        )
                        EditDayOfMonthField(
                            dayOfMonth = uiState.dayOfMonth,
                            onDayChange = onDayOfMonthChange
                        )
                    }
                    RecurrenceFrequency.DAILY -> Unit
                }

                EditTimeField(
                    time = uiState.timeOfDay,
                    onTimeChange = onTimeOfDayChange
                )

                EditDateField(
                    label = stringResource(R.string.start_date),
                    date = uiState.startDate,
                    onDateChange = onStartDateChange
                )

                EditOptionalDateField(
                    label = stringResource(R.string.end_date),
                    date = uiState.endDate,
                    onDateChange = onEndDateChange
                )
            }
        }

        // Active Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.active),
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = uiState.isActive,
                onCheckedChange = onActiveChange
            )
        }
    }
}

@Composable
private fun <T> EnumDropdownField(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = label)
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun EditDaysOfWeekSelector(
    selectedDays: Set<java.time.DayOfWeek>,
    onDaysChange: (Set<java.time.DayOfWeek>) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.days_of_week),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            java.time.DayOfWeek.entries.forEach { day ->
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
                        Text(
                            day.getDisplayName(
                                java.time.format.TextStyle.SHORT,
                                java.util.Locale.getDefault()
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EditDayOfMonthField(
    dayOfMonth: Int?,
    onDayChange: (Int) -> Unit
) {
    OutlinedTextField(
        value = dayOfMonth?.toString() ?: "",
        onValueChange = { text ->
            text.toIntOrNull()?.let { value ->
                if (value in 1..31) onDayChange(value)
            }
        },
        label = { Text(stringResource(R.string.day_of_month_hint)) },
        modifier = Modifier.fillMaxWidth(),
        isError = dayOfMonth == null || dayOfMonth !in 1..31
    )
}

@Composable
private fun EditMonthSelector(
    month: Int?,
    onMonthChange: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.month_of_year),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            java.time.Month.entries.forEach { monthEnum ->
                FilterChip(
                    selected = month == monthEnum.value,
                    onClick = { onMonthChange(monthEnum.value) },
                    label = {
                        Text(
                            monthEnum.getDisplayName(
                                java.time.format.TextStyle.SHORT,
                                java.util.Locale.getDefault()
                            )
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTimeField(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val is24Hour = remember(configuration) {
        android.text.format.DateFormat.is24HourFormat(context)
    }
    val timeFormatter = remember(is24Hour) {
        if (is24Hour) DateTimeFormatter.ofPattern("HH:mm") else DateTimeFormatter.ofPattern("h:mm a")
    }

    OutlinedTextField(
        value = time.format(timeFormatter),
        onValueChange = { },
        label = { Text(stringResource(R.string.time_of_day)) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showTimePicker = true }) {
                Icon(Icons.Default.Schedule, contentDescription = stringResource(R.string.cd_select_time))
            }
        }
    )

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
            is24Hour = is24Hour
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeChange(LocalTime.of(timePickerState.hour, timePickerState.minute))
                        showTimePicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditDateField(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    OutlinedTextField(
        value = date.format(dateFormatter),
        onValueChange = { },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.cd_select_date))
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateChange(
                                Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            )
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditOptionalDateField(
    label: String,
    date: LocalDate?,
    onDateChange: (LocalDate?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    OutlinedTextField(
        value = date?.format(dateFormatter) ?: "",
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text(stringResource(R.string.no_end_date)) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            Row {
                if (date != null) {
                    IconButton(onClick = { onDateChange(null) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_clear_date),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.cd_select_date))
                }
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (date ?: LocalDate.now())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateChange(
                                Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            )
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
