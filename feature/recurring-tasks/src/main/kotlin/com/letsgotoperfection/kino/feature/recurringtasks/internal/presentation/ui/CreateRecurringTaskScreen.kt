package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.letsgotoperfection.kino.core.designsystem.component.MarkdownPreview
import com.letsgotoperfection.kino.core.designsystem.component.RichTextEditorDialog
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.CreateRecurringTaskUiState
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurrencePreview
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskEvent
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel.CreateRecurringTaskViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

/**
 * Screen for creating a new recurring task
 */
@Composable
fun CreateRecurringTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateRecurringTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recurrencePreview by viewModel.recurrencePreview.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is RecurringTaskEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                    onNavigateBack()
                }
                is RecurringTaskEvent.ShowError -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                }
            }
        }
    }
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 16.dp)
            ) 
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }

                    // Title
                    Text(
                        text = stringResource(R.string.create_recurring_task_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Create button
                    TextButton(
                        onClick = { viewModel.createRecurringTask() },
                        enabled = uiState.isValid && !uiState.isLoading
                    ) {
                        Text(stringResource(R.string.create))
                    }
                }
            }

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
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
                    recurrencePreview = recurrencePreview,
                    onDefaultColumnChange = viewModel::updateDefaultColumn,
                    onDueDateOffsetChange = viewModel::updateDueDateOffsetDays,
                    onAddChecklistItem = viewModel::addChecklistItem,
                    onRemoveChecklistItem = viewModel::removeChecklistItem
                )
            }
        }
    }
}

@Composable
private fun CreateRecurringTaskContent(
    uiState: CreateRecurringTaskUiState,
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
    recurrencePreview: RecurrencePreview,
    onDefaultColumnChange: (TaskColumn) -> Unit,
    onDueDateOffsetChange: (Int) -> Unit,
    onAddChecklistItem: (String) -> Unit,
    onRemoveChecklistItem: (Int) -> Unit
) {
    var showRichTextEditor by remember { mutableStateOf(false) }
    var descriptionTextFieldValue by remember(uiState.description) { 
        mutableStateOf(TextFieldValue(uiState.description)) 
    }
    var showAddChecklistDialog by remember { mutableStateOf(false) }
    var newChecklistText by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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
        
        // Rich Text Description Field
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRichTextEditor = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (uiState.description.isNotBlank()) {
                                MarkdownPreview(
                                    markdown = uiState.description,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.tap_to_add_description),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { showRichTextEditor = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.cd_edit_description),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
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
        
        // Default Column Selector
        item {
            ColumnSelector(
                column = uiState.defaultColumn,
                onColumnChange = onDefaultColumnChange
            )
        }
        
        // Due Date Offset
        item {
            DueDateOffsetSelector(
                offsetDays = uiState.dueDateOffsetDays,
                onOffsetChange = onDueDateOffsetChange
            )
        }
        
        // Labels Section (optional)
        if (uiState.labels.isNotEmpty()) {
            item {
                LabelsDisplay(labels = uiState.labels)
            }
        }
        
        // Checklist Template Section
        item {
            Text(
                text = stringResource(R.string.task_template),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            ChecklistTemplateSection(
                checklistItems = uiState.checklistTemplate,
                onAddItem = { showAddChecklistDialog = true },
                onRemoveItem = onRemoveChecklistItem
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
            RecurrencePreviewCard(preview = recurrencePreview)
        }
    }
    
    // Rich Text Editor Dialog
    if (showRichTextEditor) {
        RichTextEditorDialog(
            initialValue = descriptionTextFieldValue,
            onDismiss = { showRichTextEditor = false },
            onSave = { textFieldValue ->
                descriptionTextFieldValue = textFieldValue
                onDescriptionChange(textFieldValue.text)
                showRichTextEditor = false
            }
        )
    }
    
    // Add Checklist Item Dialog
    if (showAddChecklistDialog) {
        AlertDialog(
            onDismissRequest = { showAddChecklistDialog = false },
            title = { Text(stringResource(R.string.add_checklist_item_title)) },
            text = {
                OutlinedTextField(
                    value = newChecklistText,
                    onValueChange = { newChecklistText = it },
                    label = { Text(stringResource(R.string.checklist_item_text)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newChecklistText.isNotBlank()) {
                            onAddChecklistItem(newChecklistText)
                            newChecklistText = ""
                            showAddChecklistDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    newChecklistText = ""
                    showAddChecklistDialog = false 
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
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
            TaskSection.entries.forEach { taskSection ->
                FilterChip(
                    selected = section == taskSection,
                    onClick = { onSectionChange(taskSection) },
                    label = { Text(taskSection.localizedName()) }
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
            Priority.entries.forEach { prio ->
                FilterChip(
                    selected = priority == prio,
                    onClick = { onPriorityChange(prio) },
                    label = { Text(prio.localizedName()) }
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
            RecurrenceFrequency.entries.forEach { freq ->
                FilterChip(
                    selected = frequency == freq,
                    onClick = { onFrequencyChange(freq) },
                    label = { Text(freq.localizedName()) }
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
                    stringResource(
                        when (frequency) {
                            RecurrenceFrequency.DAILY -> R.string.every_x_days
                            RecurrenceFrequency.WEEKLY -> R.string.every_x_weeks
                            RecurrenceFrequency.MONTHLY -> R.string.every_x_months
                            RecurrenceFrequency.YEARLY -> R.string.every_x_years
                        }
                    )
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
            DayOfWeek.entries.forEach { day ->
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
                        Text(day.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
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
            label = { Text(stringResource(R.string.day_of_month_hint)) },
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
            java.time.Month.entries.forEach { monthEnum ->
                FilterChip(
                    selected = month == monthEnum.value,
                    onClick = { onMonthChange(monthEnum.value) },
                    label = { 
                        Text(monthEnum.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeOfDaySelector(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    
    // Check if device uses 24-hour format
    // Recompute when configuration changes (e.g., user changes time format in settings)
    val is24Hour = remember(configuration) {
        android.text.format.DateFormat.is24HourFormat(context)
    }
    
    // Format time according to device settings
    val timeFormatter = remember(is24Hour) {
        if (is24Hour) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("h:mm a")
        }
    }

    Column {
        Text(
            text = stringResource(R.string.time_of_day),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = time.format(timeFormatter),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.time_of_day)) },
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(Icons.Default.Schedule, contentDescription = stringResource(R.string.cd_select_time))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
            is24Hour = is24Hour // Use device preference
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
private fun DateSelector(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = date.format(dateFormatter),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.cd_select_date))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
    
    // Date Picker Dialog
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
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateChange(selectedDate)
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
private fun OptionalDateSelector(
    label: String,
    date: LocalDate?,
    onDateChange: (LocalDate?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = date?.format(dateFormatter) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_optional, label)) },
            placeholder = { Text(stringResource(R.string.no_end_date)) },
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
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
    
    // Date Picker Dialog
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
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateChange(selectedDate)
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

@Composable
private fun ColumnSelector(
    column: TaskColumn,
    onColumnChange: (TaskColumn) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.default_column),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.default_column_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            TaskColumn.entries.forEach { col ->
                FilterChip(
                    selected = column == col,
                    onClick = { onColumnChange(col) },
                    label = { Text(col.localizedName()) }
                )
            }
        }
    }
}

@Composable
private fun DueDateOffsetSelector(
    offsetDays: Int,
    onOffsetChange: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.due_date),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.due_date_offset_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            listOf(0, 1, 3, 7, 14, 30).forEach { days ->
                FilterChip(
                    selected = offsetDays == days,
                    onClick = { onOffsetChange(days) },
                    label = { 
                        Text(
                            if (days == 0) {
                                stringResource(R.string.offset_same_day)
                            } else {
                                pluralStringResource(R.plurals.offset_days, days, days)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LabelsDisplay(
    labels: List<com.letsgotoperfection.kino.core.model.Label>
) {
    Column {
        Text(
            text = stringResource(R.string.labels),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            labels.forEach { label ->
                AssistChip(
                    onClick = { },
                    label = { Text(label.name) }
                )
            }
        }
    }
}

@Composable
private fun ChecklistTemplateSection(
    checklistItems: List<String>,
    onAddItem: () -> Unit,
    onRemoveItem: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
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
                    text = stringResource(R.string.checklist_template_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onAddItem) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_checklist_item))
                }
            }
            
            if (checklistItems.isEmpty()) {
                Text(
                    text = stringResource(R.string.checklist_template_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                checklistItems.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        IconButton(onClick = { onRemoveItem(index) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.cd_remove_checklist_item),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecurrencePreviewCard(
    preview: RecurrencePreview
) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

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

            when (preview) {
                is RecurrencePreview.Prompt -> {
                    Text(
                        text = stringResource(preview.messageRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                is RecurrencePreview.Ready -> {
                    Text(
                        text = preview.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (preview.nextOccurrences.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.next_occurrences),
                            style = MaterialTheme.typography.labelMedium
                        )

                        preview.nextOccurrences.forEach { date ->
                            Text(
                                text = stringResource(R.string.occurrence_bullet, date.format(dateFormatter)),
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
    }
}
