package com.letsgotoperfection.kino.feature.taskdetail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.kino.core.designsystem.component.ErrorState
import com.letsgotoperfection.kino.core.designsystem.component.LoadingState
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailAction
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailEvent
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.viewmodel.TaskDetailViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Comprehensive TaskDetailScreen with rich content and full functionality.
 * Features: Task editing, checklist management, media attachments, labels, and more.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMedia: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddChecklistDialog by remember { mutableStateOf(false) }
    var newChecklistText by remember { mutableStateOf("") }
    val context = LocalContext.current

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.onAction(TaskDetailAction.AttachMedia(uris))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(TaskDetailAction.LoadTask)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TaskDetailEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                TaskDetailEvent.TaskSaved -> {
                    snackbarHostState.showSnackbar("Task updated")
                }
                TaskDetailEvent.TaskDeleted -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val toolbarTitle = when {
                        uiState.editMode -> uiState.editForm?.title?.takeIf { it.isNotBlank() }
                            ?: uiState.taskDetail?.title
                            ?: "Task Details"
                        else -> uiState.taskDetail?.title ?: "Task Details"
                    }
                    Text(text = toolbarTitle, maxLines = 1)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.taskDetail != null) {
                        val editForm = uiState.editForm
                        if (uiState.editMode) {
                            IconButton(
                                onClick = {
                                    editForm?.let {
                                        viewModel.onAction(
                                            TaskDetailAction.UpdateTask(
                                                title = it.title,
                                                description = it.description,
                                                priority = it.priority,
                                                dueDate = it.dueDate,
                                                section = it.section,
                                                column = it.column,
                                                dueDateExplicit = true
                                            )
                                        )
                                    }
                                },
                                enabled = editForm != null
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                            IconButton(onClick = { viewModel.onAction(TaskDetailAction.ToggleEditMode) }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel editing")
                            }
                        } else {
                            IconButton(onClick = { viewModel.onAction(TaskDetailAction.ToggleEditMode) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                        IconButton(
                            onClick = { viewModel.onAction(TaskDetailAction.ShowDeleteDialog) },
                            enabled = !uiState.editMode
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            if (uiState.taskDetail != null) {
                FloatingActionButton(
                    onClick = { showAddChecklistDialog = true },
                    modifier = Modifier.padding(bottom = 16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add checklist item")
                }
            }
        },
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 16.dp)
            ) 
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                LoadingState(
                    message = "Loading task details...",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.error != null -> {
                ErrorState(
                    title = "Failed to load task",
                    message = uiState.error ?: "An unexpected error occurred",
                    onRetry = { viewModel.onAction(TaskDetailAction.LoadTask) },
                    onDismiss = onNavigateBack,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.taskDetail != null -> {
                val task = checkNotNull(uiState.taskDetail)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val editForm = uiState.editForm
                    val currentTitle = if (uiState.editMode) {
                        editForm?.title ?: task.title
                    } else {
                        task.title
                    }
                    val currentDescription = if (uiState.editMode) {
                        editForm?.description ?: task.description
                    } else {
                        task.description
                    }
                    val currentPriority = if (uiState.editMode) {
                        editForm?.priority ?: task.priority
                    } else {
                        task.priority
                    }
                    val currentSection = if (uiState.editMode) {
                        editForm?.section ?: task.section
                    } else {
                        task.section
                    }
                    val currentColumn = if (uiState.editMode) {
                        editForm?.column ?: task.column
                    } else {
                        task.column
                    }
                    val currentDueDate = if (uiState.editMode) {
                        editForm?.dueDate ?: task.dueDate
                    } else {
                        task.dueDate
                    }

                    // Task Header Card
                    item {
                        TaskHeaderCard(
                            editMode = uiState.editMode,
                            title = currentTitle,
                            description = currentDescription,
                            onTitleChange = { newTitle ->
                                viewModel.onAction(TaskDetailAction.EditTitle(newTitle))
                            },
                            onDescriptionChange = { newDescription ->
                                viewModel.onAction(TaskDetailAction.EditDescription(newDescription))
                            }
                        )
                    }

                    // Priority and Status Row
                    item {
                        TaskMetadataSection(
                            editMode = uiState.editMode,
                            priority = currentPriority,
                            section = currentSection,
                            column = currentColumn,
                            onPriorityChange = { viewModel.onAction(TaskDetailAction.EditPriority(it)) },
                            onSectionChange = { viewModel.onAction(TaskDetailAction.EditSection(it)) },
                            onColumnChange = { viewModel.onAction(TaskDetailAction.EditColumn(it)) }
                        )
                    }

                    // Due Date Card
                    if (uiState.editMode) {
                        item {
                            DueDateEditor(
                                dueDate = currentDueDate,
                                onRequestChange = { newDueDate ->
                                    viewModel.onAction(TaskDetailAction.EditDueDate(newDueDate))
                                },
                                onClearDueDate = {
                                    viewModel.onAction(TaskDetailAction.EditDueDate(null))
                                }
                            )
                        }
                    } else {
                        currentDueDate?.let { dueDate ->
                            item { DueDateCard(dueDate = dueDate) }
                        }
                    }

                    // Labels
                    if (task.labels.isNotEmpty()) {
                        item {
                            LabelsSection(labels = task.labels)
                        }
                    }

                    // Progress Card
                    item {
                        ProgressCard(
                            progress = task.progress,
                            totalItems = task.checklist.size,
                            completedItems = task.checklist.count { it.isCompleted }
                        )
                    }

                    // Checklist Section
                    if (task.checklist.isNotEmpty()) {
                        item {
                            Text(
                                text = "Checklist",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(task.checklist) { item ->
                            ChecklistItemCard(
                                item = item,
                                onToggle = { viewModel.onAction(TaskDetailAction.ToggleChecklistItem(item.id)) },
                                onDelete = { viewModel.onAction(TaskDetailAction.DeleteChecklistItem(item.id)) }
                            )
                        }
                    }

                    // Attachments Section
                    if (task.attachments.isNotEmpty()) {
                        item {
                            Text(
                                text = "Attachments",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(task.attachments) { attachment ->
                            AttachmentCard(
                                attachment = attachment,
                                onClick = { onNavigateToMedia(attachment.id) }
                            )
                        }
                    }

                    // Add Media Button
                    item {
                        OutlinedButton(
                            onClick = {
                                mediaPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Attach Media")
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No task found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    // Add Checklist Item Dialog
    if (showAddChecklistDialog) {
        AlertDialog(
            onDismissRequest = { showAddChecklistDialog = false },
            title = { Text("Add Checklist Item") },
            text = {
                OutlinedTextField(
                    value = newChecklistText,
                    onValueChange = { newChecklistText = it },
                    label = { Text("Item text") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newChecklistText.isNotBlank()) {
                            viewModel.onAction(TaskDetailAction.AddChecklistItem(newChecklistText))
                            newChecklistText = ""
                            showAddChecklistDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddChecklistDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(TaskDetailAction.HideDeleteDialog) },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onAction(TaskDetailAction.DeleteTask)
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onAction(TaskDetailAction.HideDeleteDialog) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskHeaderCard(
    title: String,
    description: String,
    editMode: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    var showRichTextEditor by remember { mutableStateOf(false) }
    var descriptionTextFieldValue by remember(description) {
        mutableStateOf(androidx.compose.ui.text.input.TextFieldValue(description))
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (editMode) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Rich text description editor
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
                                if (description.isNotBlank()) {
                                    // Show formatted markdown preview (limited to 4 lines)
                                    Box(
                                        modifier = Modifier.heightIn(max = 100.dp)
                                    ) {
                                        com.letsgotoperfection.kino.core.designsystem.component.MarkdownPreview(
                                            markdown = description,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Tap to add rich text description",
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
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (description.isNotBlank()) {
                    // Show formatted markdown preview in read mode
                    com.letsgotoperfection.kino.core.designsystem.component.MarkdownPreview(
                        markdown = description,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    
    // Rich Text Editor Dialog
    if (showRichTextEditor) {
        com.letsgotoperfection.kino.core.designsystem.component.RichTextEditorDialog(
            initialValue = descriptionTextFieldValue,
            onDismiss = { showRichTextEditor = false },
            onSave = { textFieldValue ->
                descriptionTextFieldValue = textFieldValue
                onDescriptionChange(textFieldValue.text)
                showRichTextEditor = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskMetadataSection(
    editMode: Boolean,
    priority: Priority,
    section: TaskSection,
    column: TaskColumn,
    onPriorityChange: (Priority) -> Unit,
    onSectionChange: (TaskSection) -> Unit,
    onColumnChange: (TaskColumn) -> Unit
) {
    if (editMode) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetadataDropdown(
                    label = "Priority",
                    selectedLabel = priority.displayName,
                    options = Priority.values().toList(),
                    onOptionSelected = onPriorityChange,
                    optionLabel = { it.displayName },
                    modifier = Modifier.weight(1f)
                )
                MetadataDropdown(
                    label = "Section",
                    selectedLabel = section.displayName,
                    options = TaskSection.values().toList(),
                    onOptionSelected = onSectionChange,
                    optionLabel = { it.displayName },
                    modifier = Modifier.weight(1f)
                )
            }
            MetadataDropdown(
                label = "Column",
                selectedLabel = column.displayName,
                options = TaskColumn.values().toList(),
                onOptionSelected = onColumnChange,
                optionLabel = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PriorityChip(priority = priority)
            SectionChip(section = section)
            ColumnChip(column = column)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> MetadataDropdown(
    label: String,
    selectedLabel: String,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        expanded = false
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun PriorityChip(priority: Priority) {
    val (color, icon) = when (priority) {
        Priority.HIGH -> Color.Red to Icons.Default.PriorityHigh
        Priority.MEDIUM -> Color(0xFFFF9800) to Icons.Default.Remove
        Priority.LOW -> Color.Green to Icons.Default.KeyboardArrowDown
    }
    
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = priority.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }
    }
}

@Composable
private fun SectionChip(section: TaskSection) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = section.displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ColumnChip(column: TaskColumn) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = column.displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun DueDateCard(dueDate: java.time.LocalDateTime) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Due: ${dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueDateEditor(
    dueDate: LocalDateTime?,
    onRequestChange: (LocalDateTime?) -> Unit,
    onClearDueDate: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var tempDueDate by remember(dueDate) { mutableStateOf(dueDate) }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Due Date & Time",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        // Date selection
        OutlinedTextField(
            value = tempDueDate?.let {
                it.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Due Date") },
            placeholder = { Text("Select due date") },
            trailingIcon = {
                Row {
                    if (tempDueDate != null) {
                        IconButton(onClick = { 
                            tempDueDate = null
                            onRequestChange(null)
                        }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear date",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Time selection (only shown when date is selected)
        if (tempDueDate != null) {
            OutlinedTextField(
                value = tempDueDate?.let {
                    it.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Time") },
                placeholder = { Text("Select time") },
                trailingIcon = {
                    Row {
                        IconButton(onClick = {
                            // Reset time to start of day
                            tempDueDate = tempDueDate?.toLocalDate()?.atStartOfDay()
                            onRequestChange(tempDueDate)
                        }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear time",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = "Select time"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (tempDueDate?.toLocalDate() ?: java.time.LocalDate.now())
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            val time = tempDueDate?.toLocalTime() ?: java.time.LocalTime.of(0, 0)
                            tempDueDate = LocalDateTime.of(selectedDate, time)
                            onRequestChange(tempDueDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = tempDueDate?.toLocalTime()?.hour ?: 9,
            initialMinute = tempDueDate?.toLocalTime()?.minute ?: 0,
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime = java.time.LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        tempDueDate = tempDueDate?.let { 
                            LocalDateTime.of(it.toLocalDate(), selectedTime) 
                        }
                        onRequestChange(tempDueDate)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
private fun LabelsSection(labels: List<com.letsgotoperfection.kino.core.model.Label>) {
    Column {
        Text(
            text = "Labels",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            labels.forEach { label ->
                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    color = Color(android.graphics.Color.parseColor(label.color)).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = label.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(android.graphics.Color.parseColor(label.color))
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(progress: Int, totalItems: Int, completedItems: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$completedItems/$totalItems",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress / 100f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ChecklistItemCard(
    item: com.letsgotoperfection.kino.core.model.ChecklistItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AttachmentCard(
    attachment: com.letsgotoperfection.kino.core.model.Attachment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AttachFile,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.filename,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Size: ${attachment.size / 1024} KB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
