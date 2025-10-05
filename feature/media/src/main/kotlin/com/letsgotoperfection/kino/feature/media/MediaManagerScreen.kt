package com.letsgotoperfection.kino.feature.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Public API for Media Manager Screen
 * This provides a clean interface for other modules to use the Media Manager screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaManagerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    onNavigateToViewer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Media Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
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
                    text = "Media Manager",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "This is a placeholder for the Media Manager screen. The full implementation will be connected soon.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { onNavigateToViewer("sample-media-id") }) {
                    Text("View Sample Media")
                }
            }
        }
    }
}
