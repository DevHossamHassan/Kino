package com.letsgotoperfection.kino.feature.kanban

import android.R.id.message
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import com.letsgotoperfection.kino.core.designsystem.CustomIcons
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.letsgotoperfection.kino.core.designsystem.component.TaskCard
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.core.resources.R
import kotlinx.coroutines.launch

/**
 * State representing an active drag operation with visual feedback.
 * 
 * @param task The task being dragged (for visual rendering)
 * @param taskId The ID of the task being dragged
 * @param fromColumn The column the task started from
 * @param position Current drag position for visual feedback
 * @param dropIndex Target insertion index for same-column reordering (null if dragging between columns)
 */
private data class DragState(
    val task: Task,
    val taskId: String,
    val fromColumn: TaskColumn,
    val position: Offset,
    val dropIndex: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanBoardScreen(
    onTaskClick: (String) -> Unit,
    onCreateTask: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: KanbanBoardViewModel = hiltViewModel()
) {
    val board by viewModel.boardState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var dragState by remember { mutableStateOf<DragState?>(null) }
    val columnBounds = remember { mutableStateMapOf<TaskColumn, Rect>() }

    val activeDropColumn = dragState?.let { state ->
        columnBounds.entries
            .firstOrNull { (_, rect) -> rect.contains(state.position) }
            ?.key
    }

    // State for search and filter
    var isFilterSheetOpen by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filters by viewModel.filterCriteria.collectAsStateWithLifecycle()
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // Disable default insets
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTask,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_task)
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar with edge-to-edge support
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface) // Background extends to screen edge
                    .statusBarsPadding() // Padding for content only
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TopAppBar(
                        title = { 
                            Column {
                                Text(
                                    text = stringResource(R.string.kanban_board_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                // Task count indicator
                                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                                if (uiState is KanbanUiState.Success) {
                                    val state = uiState as KanbanUiState.Success
                                    Text(
                                        text = if (state.filters.isActive) {
                                            "${state.filteredTasks} of ${state.totalTasks} tasks"
                                        } else {
                                            "${state.totalTasks} tasks"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_back)
                                )
                            }
                        },
                        actions = {
                            // Filter button with badge
                            BadgedBox(
                                badge = {
                                    if (filters.isActive) {
                                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                                            Text("${filters.activeFilterCount}")
                                        }
                                    }
                                }
                            ) {
                                IconButton(onClick = { isFilterSheetOpen = true }) {
                                    Icon(
                                        CustomIcons.FilterList,
                                        contentDescription = "Filter tasks",
                                        tint = if (filters.isActive) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = stringResource(R.string.cd_settings)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent, // Transparent to show Box background
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    
                    // Search Bar
                    SearchBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    
                    // Active Filters Chips
                    AnimatedVisibility(
                        visible = filters.isActive || searchQuery.isNotBlank(),
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        ActiveFiltersChips(
                            filters = filters.copy(searchQuery = searchQuery),
                            onClearFilters = viewModel::clearFilters,
                            onRemovePriority = viewModel::togglePriorityFilter,
                            onRemoveSection = viewModel::toggleSectionFilter,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            
            // Board Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            
            when (uiState) {
                is KanbanUiState.Loading -> {
                    LoadingState(modifier = Modifier.fillMaxSize())
                }
                is KanbanUiState.Error -> {
                    ErrorState(
                        message = (uiState as KanbanUiState.Error).message,
                        onRetry = { /* ViewModel will automatically retry */ },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is KanbanUiState.Success -> {
                    val context = LocalContext.current

                    val boardData = (uiState as KanbanUiState.Success).board
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(TaskColumn.values(), key = { it.name }) { column ->
                            val tasks = boardData[column].orEmpty()
                            KanbanColumn(
                                column = column,
                                tasks = tasks,
                                isDropTarget = activeDropColumn == column,
                                isDragging = dragState != null,
                                onBoundsChanged = { rect -> columnBounds[column] = rect },
                                columnBounds = columnBounds,  // Pass column bounds for drop indicator
                                onTaskClick = onTaskClick,
                                onDragStart = { task, taskId, startPosition ->
                                    dragState = DragState(task, taskId, column, startPosition)
                                },
                                onDrag = { position, _ ->
                                    // Find which column we're currently hovering over
                                    val currentColumn = columnBounds.entries
                                        .firstOrNull { (_, rect) -> rect.contains(position) }
                                        ?.key
                                    
                                    // Calculate drop index for the column we're hovering over (not just current column)
                                    val dropIndex = if (currentColumn != null) {
                                        val targetColumnBounds = columnBounds[currentColumn]
                                        val targetTasks = boardData[currentColumn].orEmpty()
                                        
                                        if (targetColumnBounds != null) {
                                            // Calculate relative Y position within the target column
                                            val columnTop = targetColumnBounds.top
                                            val relativeY = position.y - columnTop - 100f // Offset for header
                                            val estimatedIndex = ((relativeY / 120f).toInt()).coerceIn(0, targetTasks.size)
                                            estimatedIndex
                                        } else null
                                    } else null
                                    
                                    dragState = dragState?.copy(position = position, dropIndex = dropIndex)
                                },
                                dragState = dragState,
                                onDragEnd = {
                                    val state = dragState ?: return@KanbanColumn
                                    val target = columnBounds.entries
                                        .firstOrNull { (_, rect) -> rect.contains(state.position) }
                                        ?.key
                                    
                                    dragState = null
                                    
                                    if (target != null) {
                                        // Haptic feedback for successful drop
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        
                                        val taskId = state.taskId
                                        val dropIndex = state.dropIndex
                                        
                                        if (target == state.fromColumn && dropIndex != null) {
                                            // Same column reordering
                                            viewModel.reorderTask(taskId, dropIndex)
                                        } else if (target != state.fromColumn) {
                                            // Cross-column move with position
                                            val originalColumn = state.fromColumn
                                            
                                            // Move task to target column at specific position
                                            if (dropIndex != null) {
                                                viewModel.moveTaskToPosition(taskId, target, dropIndex)
                                            } else {
                                                viewModel.moveTask(taskId, target)
                                            }

                                            val message = context.getString(R.string.drag_task_dropped, target.displayName)
                                            val actionLabel = context.getString(R.string.cd_undo)
                                            // Show snackbar with undo action
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = message,
                                                    actionLabel = actionLabel,
                                                    duration = SnackbarDuration.Short
                                                )
                                                
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    // Undo the move (back to original column at end)
                                                    viewModel.moveTask(taskId, originalColumn)
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                }
                                            }
                                        }
                                    } else {
                                        // Haptic feedback for invalid drop
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                }
                            )
                        }
                    }
                    
                    // Floating drag preview
                    dragState?.let { state ->
                        Box(
                            modifier = Modifier
                                .offset { androidx.compose.ui.unit.IntOffset(state.position.x.toInt(), state.position.y.toInt()) }
                        ) {
                            TaskCard(
                                task = state.task,
                                onTaskClick = {},
                                modifier = Modifier
                                    .widthIn(min = 280.dp, max = 320.dp)
                                    .shadow(16.dp, MaterialTheme.shapes.medium)
                                    .alpha(0.9f)
                            )
                        }
                    }
                }
            }
            }
        }
        }
        
        // Filter Bottom Sheet
        if (isFilterSheetOpen) {
            FilterBottomSheet(
                filters = filters,
                onDismiss = { isFilterSheetOpen = false },
                onTogglePriority = viewModel::togglePriorityFilter,
                onToggleSection = viewModel::toggleSectionFilter,
                onToggleOverdue = viewModel::toggleOverdueFilter,
                onToggleCompleted = viewModel::toggleCompletedTasksFilter,
                onClearAll = {
                    viewModel.clearFilters()
                    isFilterSheetOpen = false
                }
            )
        }
    }
}

/**
 * Loading state with centered progress indicator.
 * 
 * UX: Clear feedback that content is loading
 */
@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Error state with actionable retry button.
 * 
 * UX: Clear error message with action to fix it
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(R.string.error_generic),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun KanbanColumn(
    column: TaskColumn,
    tasks: List<Task>,
    isDropTarget: Boolean,
    isDragging: Boolean,
    onBoundsChanged: (Rect) -> Unit,
    columnBounds: Map<TaskColumn, Rect>,  // Pass column bounds for drop indicator
    onTaskClick: (String) -> Unit,
    onDragStart: (Task, String, Offset) -> Unit,
    onDrag: (Offset, Int?) -> Unit,  // Now includes dropIndex for same-column reordering
    onDragEnd: () -> Unit,
    dragState: DragState? = null  // Pass drag state for drop indicator
) {
    val backgroundColor by animateColorAsState(
        if (isDropTarget) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        }, label = "columnHighlight"
    )

    val borderColor by animateColorAsState(
        if (isDropTarget) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        }, label = "columnBorder"
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(min = 280.dp, max = 320.dp)
            .clip(MaterialTheme.shapes.large)
            .background(backgroundColor, shape = MaterialTheme.shapes.large)
            .padding(16.dp)
            .onGloballyPositioned { coords ->
                onBoundsChanged(coords.boundsInRoot())
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Column header with task count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = column.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${tasks.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Drop zone indicator
        if (isDropTarget) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.cd_drop_zone, column.displayName),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            if (tasks.isEmpty() && !isDropTarget) {
                item {
                    EmptyColumnContent(column = column)
                }
            }
            items(
                items = tasks,
                key = { it.id }
            ) { task ->
                Column {
                    // Drop indicator above task (for both same-column and cross-column reordering)
                    val taskIndex = tasks.indexOf(task)
                    
                    // Check if we're hovering over this column
                    val isHoveringThisColumn = dragState?.let { state ->
                        columnBounds[column]?.contains(state.position) == true
                    } ?: false
                    
                    val showDropIndicator = dragState != null && 
                        isHoveringThisColumn &&  // Only show in column being hovered
                        dragState.dropIndex == taskIndex &&
                        // Don't show indicator for the task being dragged itself
                        dragState.taskId != task.id
                    
                    if (showDropIndicator) {
                        DropIndicator()
                    }
                    
                    KanbanTaskCard(
                        task = task,
                        column = column,
                        onTaskClick = onTaskClick,
                        onDragStart = onDragStart,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        modifier = Modifier.animateItemPlacement()  // Smooth reorder animation
                    )
                    
                    // Drop indicator at end of list (for both same-column and cross-column)
                    if (dragState != null && 
                        isHoveringThisColumn &&  // Only show in column being hovered
                        dragState.dropIndex == tasks.size &&
                        task == tasks.lastOrNull()) {
                        DropIndicator()
                    }
                }
            }
        }
    }
}

/**
 * Visual indicator showing where a task will be dropped during reordering.
 */
@Composable
private fun DropIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .padding(horizontal = 8.dp)
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(2.dp)
            )
    )
}

@Composable
private fun EmptyColumnContent(column: TaskColumn) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.cd_empty_column, column.displayName),
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Text(
            text = stringResource(R.string.cd_empty_column, column.displayName),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun KanbanTaskCard(
    task: Task,
    column: TaskColumn,
    onTaskClick: (String) -> Unit,
    onDragStart: (Task, String, Offset) -> Unit,
    onDrag: (Offset, Int?) -> Unit,  // Now includes dropIndex
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cardCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    // Animate elevation during drag
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 16.dp else 2.dp,
        label = "cardElevation"
    )
    
    // Animate alpha to smoothly hide original card during drag
    val alpha by animateFloatAsState(
        targetValue = if (isDragging) 0f else 1f,
        label = "cardAlpha"
    )

    TaskCard(
        task = task,
        onTaskClick = { onTaskClick(task.id) },
        modifier = modifier
            .shadow(elevation, MaterialTheme.shapes.medium)
            .alpha(alpha) // Smoothly hide original completely during drag
            .onGloballyPositioned { coords -> cardCoordinates = coords }
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                        // Haptic feedback for drag start
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val start = cardCoordinates?.boundsInRoot()?.center ?: Offset.Zero
                        onDragStart(task, task.id, start)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val position = cardCoordinates?.localToRoot(change.position)
                            ?: cardCoordinates?.boundsInRoot()?.center
                            ?: Offset.Zero
                        onDrag(position, null)  // dropIndex calculated in parent
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragEnd()
                    },
                    onDragCancel = {
                        isDragging = false
                        onDragEnd()
                    }
                )
            }
    )
}

private val Rect.center: Offset get() = Offset((left + right) / 2f, (top + bottom) / 2f)

/**
 * Search bar for filtering tasks
 */
@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier,
        placeholder = {
            Text(
                "Search tasks...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                CustomIcons.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        CustomIcons.Close,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    )
}

/**
 * Active filters chips row
 */
@Composable
private fun ActiveFiltersChips(
    filters: TaskFilterCriteria,
    onClearFilters: () -> Unit,
    onRemovePriority: (Priority) -> Unit,
    onRemoveSection: (TaskSection) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Clear all chip
        item {
            FilterChip(
                selected = false,
                onClick = onClearFilters,
                label = { Text("Clear all") },
                leadingIcon = {
                    Icon(
                        CustomIcons.ClearAll,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
        
        // Priority filters
        items(filters.selectedPriorities.toList()) { priority ->
            FilterChip(
                selected = true,
                onClick = { onRemovePriority(priority) },
                label = { Text(priority.displayName) },
                trailingIcon = {
                    Icon(
                        CustomIcons.Close,
                        contentDescription = "Remove filter",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
        
        // Section filters
        items(filters.selectedSections.toList()) { section ->
            FilterChip(
                selected = true,
                onClick = { onRemoveSection(section) },
                label = { Text(section.displayName) },
                trailingIcon = {
                    Icon(
                        CustomIcons.Close,
                        contentDescription = "Remove filter",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

/**
 * Filter bottom sheet
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    filters: TaskFilterCriteria,
    onDismiss: () -> Unit,
    onTogglePriority: (Priority) -> Unit,
    onToggleSection: (TaskSection) -> Unit,
    onToggleOverdue: () -> Unit,
    onToggleCompleted: () -> Unit,
    onClearAll: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Filter Tasks",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onClearAll) {
                    Text("Clear All")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Priority Section
            Text(
                "Priority",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Priority.values()) { priority ->
                    FilterChip(
                        selected = filters.selectedPriorities.contains(priority),
                        onClick = { onTogglePriority(priority) },
                        label = { Text(priority.displayName) },
                        leadingIcon = {
                            Icon(
                                CustomIcons.PriorityHigh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Section
            Text(
                "Section",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TaskSection.values()) { section ->
                    val icon = when (section) {
                        TaskSection.WORK -> CustomIcons.Work
                        TaskSection.PERSONAL -> CustomIcons.Person
                        TaskSection.FAMILY -> CustomIcons.Family
                    }
                    FilterChip(
                        selected = filters.selectedSections.contains(section),
                        onClick = { onToggleSection(section) },
                        label = { Text(section.displayName) },
                        leadingIcon = {
                            Icon(
                                icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quick Filters
            Text(
                "Quick Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filters.showOverdueOnly,
                    onClick = onToggleOverdue,
                    label = { Text("Overdue") },
                    leadingIcon = {
                        Icon(
                            CustomIcons.Today,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                FilterChip(
                    selected = !filters.showCompletedTasks,
                    onClick = onToggleCompleted,
                    label = { Text("Hide Completed") },
                    leadingIcon = {
                        Icon(
                            CustomIcons.VisibilityOff,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

