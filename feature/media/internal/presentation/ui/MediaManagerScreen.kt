package com.letsgotoperfection.kino.feature.media.internal.presentation.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.letsgotoperfection.kino.core.common.UiState
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.presentation.permission.MediaPermissionHandler
import com.letsgotoperfection.kino.feature.media.internal.presentation.permission.rememberMediaPermissionState
import com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel.MediaManagerViewModel
import com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel.MediaUiEvent
import com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel.ViewMode

/**
 * Main screen for Media Manager
 * 
 * Displays all media items in grid or list view with filtering options
 * Handles permissions, loading states, and user interactions
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MediaManagerScreen(
    viewModel: MediaManagerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    onNavigateToViewer: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    
    // Permission handling
    val permissionState = rememberMediaPermissionState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle delete events
    val deletePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // User approved deletion
            viewModel.refresh()
        }
    }
    
    // Handle UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MediaUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is MediaUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is MediaUiEvent.RequiresDeletePermission -> {
                    try {
                        deletePermissionLauncher.launch(
                            IntentSenderRequest.Builder(event.pendingIntent).build()
                        )
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Failed to request delete permission")
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.media_manager_title)) },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cancel)
                        )
                    }
                },
                actions = {
                    MediaManagerTopBarActions(
                        viewMode = viewMode,
                        onToggleViewMode = viewModel::toggleViewMode
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            !permissionState.allPermissionsGranted -> {
                PermissionRequiredContent(
                    permissionState = permissionState,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState is UiState.Loading -> {
                LoadingScreen()
            }
            uiState is UiState.Error -> {
                ErrorScreen(
                    message = (uiState as UiState.Error).message,
                    onRetry = { viewModel.refresh() }
                )
            }
            uiState is UiState.Success -> {
                val state = (uiState as UiState.Success<com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel.MediaScreenState>).data
                
                if (state.media.isEmpty()) {
                    EmptyMediaState(modifier = Modifier.padding(paddingValues))
                } else {
                    when (viewMode) {
                        ViewMode.GRID -> {
                            MediaGridView(
                                media = state.media,
                                onMediaClick = onNavigateToViewer,
                                onDeleteClick = viewModel::deleteMedia,
                                onSourceClick = { media ->
                                    viewModel.navigateToSource(
                                        media = media,
                                        onNavigateToTask = onNavigateToTask,
                                        onNavigateToNote = onNavigateToNote
                                    )
                                },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                        ViewMode.LIST -> {
                            MediaListView(
                                media = state.media,
                                onMediaClick = onNavigateToViewer,
                                onDeleteClick = viewModel::deleteMedia,
                                onSourceClick = { media ->
                                    viewModel.navigateToSource(
                                        media = media,
                                        onNavigateToTask = onNavigateToTask,
                                        onNavigateToNote = onNavigateToNote
                                    )
                                },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaManagerTopBarActions(
    viewMode: ViewMode,
    onToggleViewMode: () -> Unit
) {
    androidx.compose.material3.IconButton(onClick = onToggleViewMode) {
        androidx.compose.material3.Icon(
            imageVector = if (viewMode == ViewMode.GRID) {
                androidx.compose.material.icons.Icons.Default.List
            } else {
                androidx.compose.material.icons.Icons.Default.GridView
            },
            contentDescription = if (viewMode == ViewMode.GRID) {
                stringResource(R.string.media_list_view)
            } else {
                stringResource(R.string.media_grid_view)
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionRequiredContent(
    permissionState: com.google.accompanist.permissions.MultiplePermissionsState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = Icons.Default.PermMedia,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = stringResource(R.string.media_permission_required_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = MediaPermissionHandler.getPermissionRationaleMessage(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
            Text(stringResource(R.string.media_grant_permission))
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.media_loading),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun EmptyMediaState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PermMedia,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = stringResource(R.string.media_empty_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = stringResource(R.string.media_empty_message),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
