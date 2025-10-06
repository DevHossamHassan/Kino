package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.EditRecurringTaskUiState
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskAction
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.state.RecurringTaskEvent
import com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.viewmodel.EditRecurringTaskViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Screen for editing an existing recurring task
 */
@Composable
fun EditRecurringTaskScreen(
    recurringTaskId: String,
    onNavigateBack: () -> Unit,
    viewModel: EditRecurringTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.loadRecurringTask(recurringTaskId)
    }
    
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
                title = { Text("Edit Recurring Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isValid && !uiState.isSaving) {
                        TextButton(
                            onClick = { 
                                // TODO: Create UpdateRecurringTask action with current state
                                viewModel.onAction(RecurringTaskAction.UpdateRecurringTask(
                                    id = uiState.recurringTask?.id ?: "",
                                    title = uiState.title,
                                    description = uiState.description,
                                    section = uiState.section,
                                    priority = uiState.priority,
                                    labels = emptyList(), // TODO: Get from state
                                    frequency = uiState.frequency,
                                    interval = uiState.interval,
                                    daysOfWeek = uiState.daysOfWeek,
                                    dayOfMonth = uiState.dayOfMonth,
                                    monthOfYear = uiState.monthOfYear,
                                    timeOfDay = uiState.timeOfDay,
                                    startDate = uiState.startDate,
                                    endDate = uiState.endDate,
                                    isActive = uiState.isActive
                                ))
                            },
                            enabled = !uiState.isSaving
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Text("Save")
                            }
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
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.error ?: "Unknown error",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadRecurringTask(recurringTaskId) }) {
                        Text("Retry")
                    }
                }
            } else {
                EditRecurringTaskContent(
                    uiState = uiState,
                    onAction = viewModel::onAction,
                    viewModel = viewModel
                )
            }
        }
    }
    
    SnackbarHost(hostState = snackbarHostState)
}

@Composable
private fun EditRecurringTaskContent(
    uiState: EditRecurringTaskUiState,
    onAction: (RecurringTaskAction) -> Unit,
    viewModel: EditRecurringTaskViewModel
) {
    // Dialog state management
    var showTimePicker by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
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
            onValueChange = { viewModel.updateTitle(it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.title.isBlank()
        )
        
        // Description
        OutlinedTextField(
            value = uiState.description,
            onValueChange = { viewModel.updateDescription(it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            isError = uiState.description.isBlank()
        )
        
        // Section and Priority Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Section
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }
                
                OutlinedTextField(
                    value = uiState.section.displayName,
                    onValueChange = { },
                    label = { Text("Section") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Section")
                        }
                    }
                )
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TaskSection.values().forEach { section ->
                        DropdownMenuItem(
                            text = { Text(section.displayName) },
                            onClick = {
                                expanded = false
                                viewModel.updateSection(section)
                            }
                        )
                    }
                }
            }
            
            // Priority
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }
                
                OutlinedTextField(
                    value = uiState.priority.displayName,
                    onValueChange = { },
                    label = { Text("Priority") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Priority")
                        }
                    }
                )
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Priority.values().forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority.displayName) },
                            onClick = {
                                expanded = false
                                viewModel.updatePriority(priority)
                            }
                        )
                    }
                }
            }
        }
        
        // Recurrence Settings
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Recurrence Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Frequency
                Box(modifier = Modifier.fillMaxWidth()) {
                    var expanded by remember { mutableStateOf(false) }
                    
                    OutlinedTextField(
                        value = uiState.frequency.name,
                        onValueChange = { },
                        label = { Text("Frequency") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Frequency")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        RecurrenceFrequency.values().forEach { frequency ->
                            DropdownMenuItem(
                                text = { Text(frequency.name) },
                                onClick = {
                                    expanded = false
                                    viewModel.updateFrequency(frequency)
                                }
                            )
                        }
                    }
                }
                
                // Interval
                OutlinedTextField(
                    value = uiState.interval.toString(),
                    onValueChange = { 
                        it.toIntOrNull()?.let { interval ->
                            viewModel.updateInterval(interval)
                        }
                    },
                    label = { Text("Interval") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.interval <= 0
                )
                
                // Time of Day
                OutlinedTextField(
                    value = uiState.timeOfDay.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onValueChange = { },
                    label = { Text("Time of Day") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.Schedule, contentDescription = "Select Time")
                        }
                    }
                )
                
                // Time Picker Dialog
                if (showTimePicker) {
                    TimePickerDialog(
                        initialTime = uiState.timeOfDay,
                        onTimeSelected = { time ->
                            showTimePicker = false
                            viewModel.updateTimeOfDay(time)
                        },
                        onDismiss = { showTimePicker = false }
                    )
                }
                
                // Start Date
                OutlinedTextField(
                    value = uiState.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    onValueChange = { },
                    label = { Text("Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Start Date")
                        }
                    }
                )
                
                // Start Date Picker Dialog
                if (showStartDatePicker) {
                    DatePickerDialog(
                        initialDate = uiState.startDate,
                        onDateSelected = { date ->
                            showStartDatePicker = false
                            viewModel.updateStartDate(date)
                        },
                        onDismiss = { showStartDatePicker = false }
                    )
                }
                
                // End Date (optional)
                OutlinedTextField(
                    value = uiState.endDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "",
                    onValueChange = { },
                    label = { Text("End Date (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select End Date")
                        }
                    }
                )
                
                // End Date Picker Dialog
                if (showEndDatePicker) {
                    DatePickerDialog(
                        initialDate = uiState.endDate ?: LocalDate.now(),
                        onDateSelected = { date ->
                            showEndDatePicker = false
                            viewModel.updateEndDate(date)
                        },
                        onDismiss = { showEndDatePicker = false }
                    )
                }
            }
        }
        
        // Active Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Active",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = uiState.isActive,
                onCheckedChange = { viewModel.updateActiveStatus(it) }
            )
        }
    }
}

/**
 * Time Picker Dialog Component
 */
@Composable
private fun TimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            Column {
                Text("Choose the time for this recurring task:")
                Spacer(modifier = Modifier.height(16.dp))
                
                // Time input with proper validation
                OutlinedTextField(
                    value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onValueChange = { timeString ->
                        try {
                            val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
                            selectedTime = time
                        } catch (e: Exception) {
                            // Invalid time format, keep current value
                        }
                    },
                    label = { Text("Time (HH:mm)") },
                    placeholder = { Text("09:30") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = false
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter time in 24-hour format (e.g., 09:30, 14:45)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onTimeSelected(selectedTime) }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Date Picker Dialog Component
 */
@Composable
private fun DatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = {
            Column {
                Text("Choose the date:")
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date input with proper validation
                OutlinedTextField(
                    value = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    onValueChange = { dateString ->
                        try {
                            val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            selectedDate = date
                        } catch (e: Exception) {
                            // Invalid date format, keep current value
                        }
                    },
                    label = { Text("Date (yyyy-MM-dd)") },
                    placeholder = { Text("2024-01-15") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = false
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter date in YYYY-MM-DD format (e.g., 2024-01-15)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDateSelected(selectedDate) }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

