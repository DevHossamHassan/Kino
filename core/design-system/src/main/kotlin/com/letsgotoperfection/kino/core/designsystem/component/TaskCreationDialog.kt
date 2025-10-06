package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.model.*
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
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
    var descriptionTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var selectedSection by remember { mutableStateOf(TaskSection.PERSONAL) }
    var selectedColumn by remember { mutableStateOf(TaskColumn.TODO_THIS_WEEK) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var selectedLabels by remember { mutableStateOf<List<Label>>(emptyList()) }
    var newLabelText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRichTextEditor by remember { mutableStateOf(false) }

    // Form validation - use derivedStateOf for performance
    val isTitleValid by remember { derivedStateOf { title.trim().isNotBlank() } }
    val isFormValid by remember { derivedStateOf { isTitleValid } }

    // Reset form when dialog is dismissed - handled by navigation, no manual reset needed

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
        modifier = modifier.heightIn(max = 700.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Create New Task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("Enter task title") },
                    singleLine = true,
                    isError = title.isNotBlank() && !isTitleValid,
                    supportingText = if (title.isBlank()) {
                        { Text("Title is required", color = MaterialTheme.colorScheme.error) }
                    } else if (!isTitleValid) {
                        { Text("Please enter a valid title", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = context.getString(R.string.cd_task_title_input) }
                )

                // Description field - click to open rich text editor with preview
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    showRichTextEditor = true
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                if (description.isNotBlank()) {
                                    // Show formatted preview
                                    MarkdownPreview(
                                        markdown = description,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = "Tap to add rich text description (optional)",
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
                                    contentDescription = "Edit description with rich text",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

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

                // Due Date and Time selection
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date selection
                    OutlinedTextField(
                        value = dueDate?.let { 
                            it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Due Date") },
                        placeholder = { Text("Select due date") },
                        trailingIcon = {
                            Row {
                                if (dueDate != null) {
                                    IconButton(onClick = { dueDate = null }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = context.getString(R.string.cd_clear_date),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = context.getString(R.string.cd_select_date)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = context.getString(R.string.cd_task_due_date_picker) }
                    )
                    
                    // Time selection (only shown when date is selected)
                    if (dueDate != null) {
                        OutlinedTextField(
                            value = dueDate?.let {
                                it.format(DateTimeFormatter.ofPattern("HH:mm"))
                            } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Time") },
                            placeholder = { Text("Select time") },
                            trailingIcon = {
                                Row {
                                    IconButton(onClick = { 
                                        // Reset time to start of day
                                        dueDate = dueDate?.toLocalDate()?.atStartOfDay()
                                    }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = context.getString(R.string.cd_clear_time),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { showTimePicker = true }) {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = context.getString(R.string.cd_select_time)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = context.getString(R.string.cd_select_time) }
                        )
                    }
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
                    if (isFormValid) {
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
                        onDismiss() // Dismiss immediately after creating
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.semantics { contentDescription = context.getString(R.string.cd_create_task_button) }
            ) {
                Text("Create Task")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss, // Just dismiss, no manual clearing needed
                modifier = Modifier.semantics { contentDescription = context.getString(R.string.cd_cancel_task_creation) }
            ) {
                Text("Cancel")
            }
        }
    )

    // Rich Text Editor Dialog
    if (showRichTextEditor) {
        RichTextEditorDialog(
            initialValue = descriptionTextFieldValue,
            onDismiss = { showRichTextEditor = false },
            onSave = { textFieldValue ->
                descriptionTextFieldValue = textFieldValue
                description = textFieldValue.text
                showRichTextEditor = false
            }
        )
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = dueDate?.toLocalDate() ?: LocalDate.now(),
            onDateSelected = { date ->
                // Preserve existing time if set, otherwise start of day
                val time = dueDate?.toLocalTime() ?: LocalTime.of(0, 0)
                dueDate = LocalDateTime.of(date, time)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
    
    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            initialTime = dueDate?.toLocalTime() ?: LocalTime.of(9, 0),
            onTimeSelected = { time ->
                // Update time while preserving the date
                dueDate = dueDate?.let { LocalDateTime.of(it.toLocalDate(), time) }
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

/**
 * Rich Text Editor Dialog for task description
 * Public component that can be reused across the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RichTextEditorDialog(
    initialValue: TextFieldValue,
    onDismiss: () -> Unit,
    onSave: (TextFieldValue) -> Unit
) {
    var editorValue by remember { mutableStateOf(initialValue) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Description",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                MarkdownTextEditor(
                    value = editorValue,
                    onValueChange = { editorValue = it },
                    placeholder = "Add a detailed description with rich text formatting...",
                    minLines = 15,
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(editorValue) }) {
                Text("Save")
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
 * Material3 Date Picker Dialog Component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * Material3 Time Picker Dialog Component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedTime = LocalTime.of(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    onTimeSelected(selectedTime)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

/**
 * Renders markdown text with basic formatting support
 * Public component for reuse across the app
 */
@Composable
fun MarkdownPreview(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val lines = remember(markdown) { markdown.split('\n') }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        lines.forEach { line ->
            when {
                // Checklist item
                line.trim().startsWith("- [ ]") || line.trim().startsWith("- [x]") -> {
                    val isChecked = line.contains("[x]")
                    val text = line.removePrefix("- [ ]").removePrefix("- [x]").trim()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = if (isChecked) "Checked" else "Unchecked",
                            modifier = Modifier.size(20.dp),
                            tint = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatInlineMarkdown(text),
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (isChecked) TextDecoration.LineThrough else null
                        )
                    }
                }
                // Bullet list
                line.trim().startsWith("- ") -> {
                    val text = line.removePrefix("- ").trim()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("•", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = formatInlineMarkdown(text),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                // Numbered list
                line.trim().matches(Regex("^\\d+\\.\\s.*")) -> {
                    val parts = line.trim().split(". ", limit = 2)
                    if (parts.size == 2) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("${parts[0]}.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = formatInlineMarkdown(parts[1]),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                // Quote
                line.trim().startsWith("> ") -> {
                    val text = line.removePrefix("> ").trim()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(20.dp)
                                .padding(vertical = 2.dp)
                        )
                        Text(
                            text = formatInlineMarkdown(text),
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Code block
                line.trim().startsWith("```") -> {
                    // Skip code block markers
                }
                // Regular text
                line.isNotBlank() -> {
                    Text(
                        text = formatInlineMarkdown(line),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Formats inline markdown (bold, italic, strikethrough, code)
 * Public composable for formatting inline markdown text
 */
@Composable
fun formatInlineMarkdown(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        val processedText = text
        
        // Simple regex-based formatting
        val boldRegex = Regex("""\*\*(.*?)\*\*""")
        val italicRegex = Regex("""_(.*?)_""")
        val strikethroughRegex = Regex("""~~(.*?)~~""")
        val codeRegex = Regex("""`(.*?)`""")
        
        var workingText = processedText
        val segments = mutableListOf<Pair<String, SpanStyle?>>()
        
        // Process all formatting
        while (workingText.isNotEmpty()) {
            val boldMatch = boldRegex.find(workingText)
            val italicMatch = italicRegex.find(workingText)
            val strikeMatch = strikethroughRegex.find(workingText)
            val codeMatch = codeRegex.find(workingText)
            
            // Find the earliest match
            val matches = listOfNotNull(
                boldMatch?.let { it to "bold" },
                italicMatch?.let { it to "italic" },
                strikeMatch?.let { it to "strike" },
                codeMatch?.let { it to "code" }
            ).sortedBy { it.first.range.first }
            
            if (matches.isEmpty()) {
                // No more formatting, add remaining text
                segments.add(workingText to null)
                break
            }
            
            val (match, type) = matches.first()
            
            // Add text before match
            if (match.range.first > 0) {
                segments.add(workingText.substring(0, match.range.first) to null)
            }
            
            // Add formatted text
            val style = when (type) {
                "bold" -> SpanStyle(fontWeight = FontWeight.Bold)
                "italic" -> SpanStyle(fontStyle = FontStyle.Italic)
                "strike" -> SpanStyle(textDecoration = TextDecoration.LineThrough)
                "code" -> SpanStyle(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    background = MaterialTheme.colorScheme.surfaceVariant,
                    fontSize = 14.sp
                )
                else -> null
            }
            segments.add(match.groupValues[1] to style)
            
            // Continue with remaining text
            workingText = workingText.substring(match.range.last + 1)
        }
        
        // Build the annotated string
        segments.forEach { (text, style) ->
            if (style != null) {
                withStyle(style) {
                    append(text)
                }
            } else {
                append(text)
            }
        }
    }
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