package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.model.*
import java.time.LocalDateTime

/**
 * Dialog for creating a new task with all necessary fields
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onTaskCreated: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf(TaskSection.PERSONAL) }
    var selectedColumn by remember { mutableStateOf(TaskColumn.TODO_THIS_WEEK) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var selectedLabels by remember { mutableStateOf<List<Label>>(emptyList()) }
    var newLabelText by remember { mutableStateOf("") }
    var showAddLabel by remember { mutableStateOf(false) }

    // Available labels for selection
    val availableLabels = remember {
        listOf(
            Label("work", "Work", "#9C27B0"),
            Label("personal", "Personal", "#4CAF50"),
            Label("urgent", "Urgent", "#F44336"),
            Label("shopping", "Shopping", "#FF9800"),
            Label("travel", "Travel", "#2196F3"),
            Label("planning", "Planning", "#00BCD4")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create New Task",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    placeholder = { Text("Enter task title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Enter task description (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
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
                            modifier = Modifier.fillMaxWidth()
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
                            modifier = Modifier.fillMaxWidth()
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
                var expandedPriority by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedPriority,
                    onExpandedChange = { expandedPriority = !expandedPriority },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPriority.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                        modifier = Modifier.fillMaxWidth()
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
                                }
                            )
                        }
                    }
                }

                // Labels section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Labels",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(
                            onClick = { showAddLabel = !showAddLabel }
                        ) {
                            Icon(
                                imageVector = if (showAddLabel) Icons.Default.Remove else Icons.Default.Add,
                                contentDescription = if (showAddLabel) "Remove" else "Add",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (showAddLabel) "Hide" else "Add Label")
                        }
                    }

                    // Add new label input
                    if (showAddLabel) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newLabelText,
                                onValueChange = { newLabelText = it },
                                label = { Text("New Label") },
                                placeholder = { Text("Enter label name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (newLabelText.isNotBlank()) {
                                        val newLabel = Label(
                                            id = newLabelText.lowercase().replace(" ", "_"),
                                            name = newLabelText,
                                            color = "#2196F3" // Default blue color
                                        )
                                        selectedLabels = selectedLabels + newLabel
                                        newLabelText = ""
                                    }
                                },
                                enabled = newLabelText.isNotBlank()
                            ) {
                                Text("Add")
                            }
                        }
                    }

                    // Selected labels
                    if (selectedLabels.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedLabels) { label ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    LabelChip(label = label)
                                    IconButton(
                                        onClick = { 
                                            selectedLabels = selectedLabels.filter { it.id != label.id }
                                        },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove label",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Available labels to select
                    if (availableLabels.isNotEmpty()) {
                        Text(
                            text = "Available Labels:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableLabels.filter { available -> 
                                !selectedLabels.any { selected -> selected.id == available.id }
                            }) { label ->
                                FilterChip(
                                    onClick = { 
                                        selectedLabels = selectedLabels + label
                                    },
                                    label = { Text(label.name) },
                                    selected = false
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
                        val newTask = Task(
                            id = "task_${System.currentTimeMillis()}",
                            title = title.trim(),
                            description = description.trim(),
                            section = selectedSection,
                            column = selectedColumn,
                            priority = selectedPriority,
                            progress = 0,
                            labels = selectedLabels,
                            attachments = emptyList(),
                            checklist = emptyList(),
                            dueDate = dueDate,
                            createdAt = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now()
                        )
                        onTaskCreated(newTask)
                        
                        // Reset form
                        title = ""
                        description = ""
                        selectedSection = TaskSection.PERSONAL
                        selectedColumn = TaskColumn.TODO_THIS_WEEK
                        selectedPriority = Priority.MEDIUM
                        dueDate = null
                        selectedLabels = emptyList()
                        newLabelText = ""
                        showAddLabel = false
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Create Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
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
                onTaskCreated = {}
            )
        }
    }
}
