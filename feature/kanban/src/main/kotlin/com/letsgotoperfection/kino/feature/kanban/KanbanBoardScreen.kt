package com.letsgotoperfection.kino.feature.kanban

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.letsgotoperfection.kino.core.designsystem.component.TaskCard
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskColumn
import kotlinx.coroutines.launch

private data class DragState(
    val taskId: String,
    val fromColumn: TaskColumn,
    val position: Offset
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

    var dragState by remember { mutableStateOf<DragState?>(null) }
    val columnBounds = remember { mutableStateMapOf<TaskColumn, Rect>() }

    val activeDropColumn = dragState?.let { state ->
        columnBounds.entries
            .firstOrNull { (_, rect) -> rect.contains(state.position) }
            ?.key
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Tasks") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTask,
                modifier = Modifier.padding(bottom = 80.dp) // Account for bottom navigation bar
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(TaskColumn.values(), key = { it.name }) { column ->
                val tasks = board[column].orEmpty()
                KanbanColumn(
                    column = column,
                    tasks = tasks,
                    isDropTarget = activeDropColumn == column,
                    onBoundsChanged = { rect -> columnBounds[column] = rect },
                    onTaskClick = onTaskClick,
                    onDragStart = { taskId, startPosition ->
                        dragState = DragState(taskId, column, startPosition)
                    },
                    onDrag = { position ->
                        dragState = dragState?.copy(position = position)
                    },
                    onDragEnd = {
                        val state = dragState ?: return@KanbanColumn
                        val target = columnBounds.entries
                            .firstOrNull { (_, rect) -> rect.contains(state.position) }
                            ?.key
                        dragState = null
                        if (target != null && target != state.fromColumn) {
                            viewModel.moveTask(state.taskId, target)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Moved to ${target.displayName}"
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun KanbanColumn(
    column: TaskColumn,
    tasks: List<Task>,
    isDropTarget: Boolean,
    onBoundsChanged: (Rect) -> Unit,
    onTaskClick: (String) -> Unit,
    onDragStart: (String, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isDropTarget) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        }, label = "columnHighlight"
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(12.dp)
            .onGloballyPositioned { coords ->
                onBoundsChanged(coords.boundsInRoot())
            },
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = column.displayName,
            style = MaterialTheme.typography.titleMedium
        )
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            if (tasks.isEmpty()) {
                item {
                    Text(
                        text = "No tasks yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(tasks, key = { it.id }) { task ->
                KanbanTaskCard(
                    task = task,
                    column = column,
                    onTaskClick = onTaskClick,
                    onDragStart = onDragStart,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd
                )
            }
        }
    }
}

@Composable
private fun KanbanTaskCard(
    task: Task,
    column: TaskColumn,
    onTaskClick: (String) -> Unit,
    onDragStart: (String, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    var cardCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    TaskCard(
        task = task,
        onTaskClick = { onTaskClick(task.id) },
        modifier = Modifier
            .onGloballyPositioned { coords -> cardCoordinates = coords }
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        val start = cardCoordinates?.boundsInRoot()?.center ?: Offset.Zero
                        onDragStart(task.id, start)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val position = cardCoordinates?.localToRoot(change.position)
                            ?: cardCoordinates?.boundsInRoot()?.center
                            ?: Offset.Zero
                        onDrag(position)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragEnd
                )
            }
    )
}

private val Rect.center: Offset get() = Offset((left + right) / 2f, (top + bottom) / 2f)
