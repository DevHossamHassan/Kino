package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import com.letsgotoperfection.kino.core.resources.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.model.*
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Dialog for creating a new task with all necessary fields
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onTaskCreated: (TaskCreationRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf(TaskSection.PERSONAL) }
    var selectedColumn by remember { mutableStateOf(TaskColumn.TODO_THIS_WEEK) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var selectedLabels by remember { mutableStateOf<List<Label>>(emptyList()) }
    var newLabelText by remember { mutableStateOf("") }
    var showAddLabel by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Track if form was just submitted to reset on next open
    var wasSubmitted by remember { mutableStateOf(false) }
    
    // Reset form when dialog is dismissed after successful submission
    LaunchedEffect(isVisible) {
        if (!isVisible && wasSubmitted) {
            title = ""
            description = ""
            selectedSection = TaskSection.PERSONAL
            selectedColumn = TaskColumn.TODO_THIS_WEEK
            selectedPriority = Priority.MEDIUM
            dueDate = null
            selectedLabels = emptyList()
            newLabelText = ""
            showAddLabel = false
            wasSubmitted = false
        }
    }

    // Available labels for selection with accessible colors
    val availableLabels = remember {
        listOf(
            Label("work", "Work", "#7B1FA2"),      // Purple - better contrast
            Label("personal", "Personal", "#388E3C"), // Green - better contrast
            Label("urgent", "Urgent", "#D32F2F"),     // Red - better contrast
            Label("shopping", "Shopping", "#F57C00"), // Orange - better contrast
            Label("travel", "Travel", "#1976D2"),     // Blue - better contrast
            Label("planning", "Planning", "#00796B")  // Teal - better contrast
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.heightIn(max = 600.dp),
        title = {
            Text(
                text = "Create New Task",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("Enter task title") },
                    singleLine = true,
                    isError = title.isBlank(),
                    supportingText = if (title.isBlank()) {
                        { Text("Title is required", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = context.getString(R.string.cd_task_title_input) }
                )

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Enter task description (optional)") },
                    minLines = 2,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = context.getString(R.string.cd_task_description_input) }
                )

                // Section and Column selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Section dropdown
                    var expandedSection by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedSection,
                        onExpandedChange = { expandedSection = !expandedSection },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedSection.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Section") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSection) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .semantics { contentDescription = context.getString(R.string.cd_task_section_dropdown) }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSection,
                            onDismissRequest = { expandedSection = false }
                        ) {
                            TaskSection.values().forEach { section ->
                                DropdownMenuItem(
                                    text = { Text(section.displayName) },
                                    onClick = {
                                        selectedSection = section
                                        expandedSection = false
                                    }
                                )
                            }
                        }
                    }

                    // Column dropdown
                    var expandedColumn by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedColumn,
                        onExpandedChange = { expandedColumn = !expandedColumn },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedColumn.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Column") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedColumn) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .semantics { contentDescription = context.getString(R.string.cd_task_column_dropdown) }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedColumn,
                            onDismissRequest = { expandedColumn = false }
                        ) {
                            TaskColumn.values().forEach { column ->
                                DropdownMenuItem(
                                    text = { Text(column.displayName) },
                                    onClick = {
                                        selectedColumn = column
                                        expandedColumn = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Priority selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var expandedPriority by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedPriority,
                        onExpandedChange = { expandedPriority = !expandedPriority },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedPriority.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Priority") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .semantics { contentDescription = context.getString(R.string.cd_task_priority_dropdown) }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPriority,
                            onDismissRequest = { expandedPriority = false }
                        ) {
                            Priority.values().forEach { priority ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            PriorityBadge(priority = priority)
                                            Text(priority.displayName)
                                        }
                                    },
                                    onClick = {
                                        selectedPriority = priority
                                        expandedPriority = false
                                    },
                                    modifier = Modifier.semantics { 
                                        contentDescription = context.getString(R.string.cd_select_priority, priority.displayName) 
                                    }
                                )
                            }
                        }
                    }
                }

                // Due Date selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = dueDate?.let { 
                            "${it.dayOfMonth}/${it.monthValue}/${it.year}" 
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Due Date") },
                        placeholder = { Text("Select due date") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = context.getString(R.string.cd_select_date))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = context.getString(R.string.cd_task_due_date_picker) }
                    )
                }

                // Labels selection
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Labels",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Selected labels
                    if (selectedLabels.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(selectedLabels) { label ->
                                AssistChip(
                                    onClick = { 
                                        selectedLabels = selectedLabels.filter { it.id != label.id }
                                    },
                                    label = { Text(label.name) },
                                    trailingIcon = {
                                        Icon(Icons.Default.Close, contentDescription = context.getString(R.string.cd_remove_label))
                                    }
                                )
                            }
                        }
                    }
                    
                    // Add new label
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newLabelText,
                            onValueChange = { newLabelText = it },
                            label = { Text("Add Label") },
                            placeholder = { Text("Enter label name") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (newLabelText.isNotBlank()) {
                                    val newLabel = Label(
                                        id = newLabelText.lowercase().replace(" ", "_"),
                                        name = newLabelText,
                                        color = "#6366F1" // Default color
                                    )
                                    selectedLabels = selectedLabels + newLabel
                                    newLabelText = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = context.getString(R.string.cd_add_label))
                        }
                    }
                    
                    // Available labels
                    if (availableLabels.isNotEmpty()) {
                        Text(
                            text = "Available Labels:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableLabels.filter { availableLabel ->
                                !selectedLabels.any { it.id == availableLabel.id }
                            }) { label ->
                                AssistChip(
                                    onClick = { 
                                        selectedLabels = selectedLabels + label
                                    },
                                    label = { Text(label.name) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Add, contentDescription = context.getString(R.string.cd_add_label))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val taskRequest = TaskCreationRequest(
                            title = title.trim(),
                            description = description.trim(),
                            section = selectedSection,
                            column = selectedColumn,
                            priority = selectedPriority,
                            dueDate = dueDate,
                            labels = selectedLabels
                        )
                        onTaskCreated(taskRequest)
                        wasSubmitted = true
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.semantics { contentDescription = context.getString(R.string.cd_create_task_button) }
            ) {
                Text("Create Task")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.semantics { contentDescription = context.getString(R.string.cd_cancel_task_creation) }
            ) {
                Text("Cancel")
            }
        }
    )

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = dueDate?.toLocalDate() ?: LocalDate.now(),
            onDateSelected = { date ->
                showDatePicker = false
                dueDate = date.atStartOfDay()
            },
            onDismiss = { showDatePicker = false }
        )
    }
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
        title = { Text("Select Due Date") },
        text = {
            Column {
                Text("Choose the due date for this task:")
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Selected: ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            Button(onClick = { onDateSelected(selectedDate) }) {
                Text("Select")
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
 * Preview for the task creation dialog
 */
@Preview(showBackground = true)
@Composable
private fun TaskCreationDialogPreview() {
    KinoTheme {
        Surface {
            TaskCreationDialog(
                isVisible = true,
                onDismiss = {},
                onTaskCreated = { _ -> }
            )
        }
    }
}