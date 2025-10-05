package com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.kino.core.common.UiState
import com.letsgotoperfection.kino.core.designsystem.component.LoadingScreen
import com.letsgotoperfection.kino.core.designsystem.component.ErrorScreen
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailAction
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.state.TaskDetailEvent
import com.letsgotoperfection.kino.feature.taskdetail.internal.presentation.viewmodel.TaskDetailViewModel
import com.letsgotoperfection.kino.core.model.ChecklistItem
import com.letsgotoperfection.kino.core.model.Priority

/**
 * Task Detail Screen - Shows detailed information about a specific task.
 */
@Composable
internal fun TaskDetailScreen(
    taskId: String,
    viewModel: TaskDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToMedia: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Photo picker for attachments
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.onAction(TaskDetailAction.AttachMedia(uris))
        }
    }
    
    // Handle UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TaskDetailEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is TaskDetailEvent.NavigateBack -> {
                    onNavigateBack()
                }
                is TaskDetailEvent.NavigateToMedia -> {
                    onNavigateToMedia(event.mediaId)
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TaskDetailTopBar(
                editMode = uiState.editMode,
                onNavigateBack = onNavigateBack,
                onToggleEdit = { viewModel.onAction(TaskDetailAction.ToggleEditMode) },
                onDelete = { viewModel.onAction(TaskDetailAction.ShowDeleteDialog) }
            )
        },
        floatingActionButton = {
            if (uiState.editMode) {
                FloatingActionButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageAndVideo
                            )
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Attach Media"
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            is UiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.onAction(TaskDetailAction.LoadTask) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is UiState.Success -> {
                val task = state.data
                TaskDetailContent(
                    task = task,
                    editMode = uiState.editMode,
                    onToggleChecklistItem = { itemId ->
                        viewModel.onAction(TaskDetailAction.ToggleChecklistItem(itemId))
                    },
                    onAddChecklistItem = { text ->
                        viewModel.onAction(TaskDetailAction.AddChecklistItem(text))
                    },
                    onDeleteChecklistItem = { itemId ->
                        viewModel.onAction(TaskDetailAction.DeleteChecklistItem(itemId))
                    },
                    onAttachMedia = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageAndVideo
                            )
                        )
                    },
                    onMediaClick = onNavigateToMedia,
                    onUpdateTask = { title, description, priority, dueDate ->
                        viewModel.onAction(
                            TaskDetailAction.UpdateTask(
                                title = title,
                                description = description,
                                priority = priority,
                                dueDate = dueDate
                            )
                        )
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailTopBar(
    editMode: Boolean,
    onNavigateBack: () -> Unit,
    onToggleEdit: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        title = { Text("Task Details") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            if (editMode) {
                TextButton(onClick = onToggleEdit) {
                    Text("Save")
                }
            } else {
                IconButton(onClick = onToggleEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit task"
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete task"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun TaskDetailContent(
    task: com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model.TaskDetail,
    editMode: Boolean,
    onToggleChecklistItem: (String) -> Unit,
    onAddChecklistItem: (String) -> Unit,
    onDeleteChecklistItem: (String) -> Unit,
    onAttachMedia: () -> Unit,
    onMediaClick: (String) -> Unit,
    onUpdateTask: (String?, String?, Priority?, java.time.LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedTitle by remember(task.title) { mutableStateOf(task.title) }
    var editedDescription by remember(task.description) { mutableStateOf(task.description) }
    var editedPriority by remember(task.priority) { mutableStateOf(task.priority) }
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header section
        item {
            TaskHeaderSection(
                title = if (editMode) editedTitle else task.title,
                priority = if (editMode) editedPriority else task.priority,
                editMode = editMode,
                onTitleChange = { editedTitle = it },
                onPriorityChange = { editedPriority = it }
            )
        }
        
        // Description section
        item {
            DescriptionSection(
                description = if (editMode) editedDescription else task.description,
                editMode = editMode,
                onDescriptionChange = { editedDescription = it }
            )
        }
        
        // Progress section
        if (task.checklist.isNotEmpty()) {
            item {
                ProgressSection(progress = task.progress)
            }
        }
        
        // Checklist section
        item {
            ChecklistSection(
                checklist = task.checklist,
                editMode = editMode,
                onToggleItem = onToggleChecklistItem,
                onAddItem = onAddChecklistItem,
                onDeleteItem = onDeleteChecklistItem
            )
        }
        
        // Attachments section
        if (task.attachments.isNotEmpty() || editMode) {
            item {
                AttachmentsSection(
                    attachments = task.attachments,
                    editMode = editMode,
                    onAttachMedia = onAttachMedia,
                    onMediaClick = onMediaClick
                )
            }
        }
        
        // Due date section
        item {
            DueDateSection(
                dueDate = task.dueDate,
                editMode = editMode
            )
        }
        
        // Labels section
        item {
            LabelsSection(
                labels = task.labels,
                editMode = editMode
            )
        }
        
        // Save button (in edit mode)
        if (editMode) {
            item {
                Button(
                    onClick = {
                        onUpdateTask(
                            editedTitle.takeIf { it != task.title },
                            editedDescription.takeIf { it != task.description },
                            editedPriority.takeIf { it != task.priority },
                            null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
private fun TaskHeaderSection(
    title: String,
    priority: Priority,
    editMode: Boolean,
    onTitleChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (editMode) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = "Priority: ${priority.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DescriptionSection(
    description: String,
    editMode: Boolean,
    onDescriptionChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier.padding(16.dp)
        ) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            if (editMode) {
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            } else {
                Text(
                    text = description.ifEmpty { "No description provided" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProgressSection(progress: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier.padding(16.dp)
        ) {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            androidx.compose.material3.LinearProgressIndicator(
                progress = progress / 100f,
                modifier = androidx.compose.ui.Modifier.fillMaxWidth()
            )
            
            Text(
                text = "$progress%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChecklistSection(
    checklist: List<ChecklistItem>,
    editMode: Boolean,
    onToggleItem: (String) -> Unit,
    onAddItem: (String) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    var newItemText by remember { mutableStateOf("") }
    var showAddField by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = "Checklist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (editMode || checklist.isEmpty()) {
                    TextButton(
                        onClick = { showAddField = true }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Add Item")
                    }
                }
            }
            
            // Checklist items
            checklist.forEach { item ->
                ChecklistItemRow(
                    item = item,
                    editMode = editMode,
                    onToggle = { onToggleItem(item.id) },
                    onDelete = { onDeleteItem(item.id) }
                )
            }
            
            // Add new item field
            if (showAddField) {
                OutlinedTextField(
                    value = newItemText,
                    onValueChange = { newItemText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Enter checklist item")
                    },
                    trailingIcon = {
                        Row {
                            IconButton(
                                onClick = {
                                    if (newItemText.isNotBlank()) {
                                        onAddItem(newItemText)
                                        newItemText = ""
                                        showAddField = false
                                    }
                                },
                                enabled = newItemText.isNotBlank()
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add item"
                                )
                            }
                        }
                    },
                    singleLine = true
                )
            }
            
            // Empty state
            if (checklist.isEmpty() && !showAddField) {
                Text(
                    text = "No checklist items yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ChecklistItemRow(
    item: ChecklistItem,
    editMode: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = { onToggle() }
        )
        
        Text(
            text = item.text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (item.isCompleted) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            },
            color = if (item.isCompleted) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
        
        if (editMode) {
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
private fun AttachmentsSection(
    attachments: List<com.letsgotoperfection.kino.core.model.Attachment>,
    editMode: Boolean,
    onAttachMedia: () -> Unit,
    onMediaClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier.padding(16.dp)
        ) {
            Text(
                text = "Attachments",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            if (attachments.isEmpty()) {
                Text(
                    text = "No attachments",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                attachments.forEach { attachment ->
                    Text(
                        text = attachment.filename,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            onMediaClick(attachment.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DueDateSection(
    dueDate: java.time.LocalDateTime?,
    editMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier.padding(16.dp)
        ) {
            Text(
                text = "Due Date",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            Text(
                text = dueDate?.let { 
                    it.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                } ?: "No due date set",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LabelsSection(
    labels: List<com.letsgotoperfection.kino.core.model.Label>,
    editMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier.padding(16.dp)
        ) {
            Text(
                text = "Labels",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            if (labels.isEmpty()) {
                Text(
                    text = "No labels",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
    }
}
