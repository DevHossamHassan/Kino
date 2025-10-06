package com.letsgotoperfection.kino.feature.kanban

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Public API for Kanban Screen
 * This provides a clean interface for other modules to use the Kanban screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanScreen(
    onTaskClick: (String) -> Unit,
    onCreateTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kanban Board") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTask,
                modifier = Modifier.padding(bottom = 80.dp) // Account for bottom navigation bar
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Create new task"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Kanban Board",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "This is a placeholder for the Kanban Board screen. The full implementation will be connected soon.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { onTaskClick("sample-task-id") }) {
                    Text("View Sample Task")
                }
            }
        }
    }
}

