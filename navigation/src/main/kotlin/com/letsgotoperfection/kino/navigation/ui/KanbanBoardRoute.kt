package com.letsgotoperfection.kino.navigation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.kino.core.designsystem.component.TaskCard
import com.letsgotoperfection.kino.core.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanBoardRoute(
    onTaskClick: (String) -> Unit,
    onCreateTask: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KanbanBoardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val board by viewModel.board.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var newSectionName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.kanban_board_title), style = MaterialTheme.typography.titleLarge) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.kanban_add_task))
            }
        }
    ) { padding ->
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.section_add)) },
                text = {
                    androidx.compose.material3.OutlinedTextField(
                        value = newSectionName,
                        onValueChange = { newSectionName = it },
                        label = { Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.section_name)) }
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            val trimmed = newSectionName.trim()
                            if (trimmed.isNotEmpty()) viewModel.addSection(trimmed)
                            showAddDialog = false
                            newSectionName = ""
                        }
                    ) { Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.create)) }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showAddDialog = false }) {
                        Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.cancel))
                    }
                }
            )
        }
        LazyRow(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(board, key = { it.section.id }) { sectionWithTasks ->
                SectionColumn(
                    title = sectionWithTasks.section.name,
                    tasks = sectionWithTasks.tasks,
                    onTaskClick = onTaskClick,
                    section = sectionWithTasks.section,
                    onRename = { id, newName -> viewModel.renameSection(id, newName) },
                    onDelete = { viewModel.deleteSection(sectionWithTasks.section) }
                )
            }
            item {
                AddSectionCard(onClick = { showAddDialog = true })
            }
        }
    }
}

@Composable
private fun SectionColumn(
    title: String,
    tasks: List<com.letsgotoperfection.kino.core.model.Task>,
    onTaskClick: (String) -> Unit,
    section: com.letsgotoperfection.kino.core.database.entity.SectionEntity,
    onRename: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .padding(end = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val taskCount = tasks.size
            androidx.compose.material3.Text(text = "$title ($taskCount)", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            var editing by remember { mutableStateOf(false) }
            var name by remember { mutableStateOf(title) }
            var menuExpanded by remember { mutableStateOf(false) }
            androidx.compose.material3.IconButton(onClick = { menuExpanded = true }) {
                androidx.compose.material3.Icon(Icons.Filled.MoreVert, contentDescription = null)
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(
                    text = { androidx.compose.material3.Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.edit)) },
                    onClick = { menuExpanded = false; editing = true }
                )
                var showConfirm by remember { mutableStateOf(false) }
                DropdownMenuItem(
                    text = { androidx.compose.material3.Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.delete)) },
                    onClick = { menuExpanded = false; showConfirm = true }
                )
                if (showConfirm) {
                    AlertDialog(
                        onDismissRequest = { showConfirm = false },
                        title = { androidx.compose.material3.Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.confirm)) },
                        text = { androidx.compose.material3.Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.delete)) },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = {
                                onDelete()
                                showConfirm = false
                            }) { androidx.compose.material3.Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.delete)) }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(onClick = { showConfirm = false }) { androidx.compose.material3.Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.cancel)) }
                        }
                    )
                }
            }
            if (editing) {
                androidx.compose.material3.OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true
                )
                androidx.compose.material3.TextButton(onClick = {
                    val trimmed = name.trim()
                    if (trimmed.isNotEmpty()) onRename(section.id, trimmed)
                    editing = false
                }) { androidx.compose.material3.Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.save)) }
                androidx.compose.material3.TextButton(onClick = { editing = false }) { androidx.compose.material3.Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.cancel)) }
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            items(tasks, key = { it.id }, contentType = { "task" }) { task ->
                com.letsgotoperfection.kino.core.designsystem.component.TaskCard(
                    task = task,
                    onTaskClick = { onTaskClick(task.id) }
                )
            }
        }
    }
}

@Composable
private fun AddSectionCard(onClick: () -> Unit) {
    androidx.compose.material3.OutlinedCard(
        onClick = onClick
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(text = androidx.compose.ui.res.stringResource(com.letsgotoperfection.kino.core.resources.R.string.section_add))
            }
        }
    }
}



