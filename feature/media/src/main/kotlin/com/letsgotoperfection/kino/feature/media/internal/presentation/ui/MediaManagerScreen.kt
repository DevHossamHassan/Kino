package com.letsgotoperfection.kino.feature.media.internal.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaManagerEvent
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaManagerUiState
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.ViewMode
import com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel.MediaManagerViewModel

/**
 * Media manager screen.
 *
 * Displays all attached media in grid or list view with type filtering.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaManagerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    onNavigateToViewer: (String) -> Unit,
    viewModel: MediaManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MediaManagerEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.media_manager_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    ViewModeToggle(
                        viewMode = uiState.viewMode,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MediaTypeFilterRow(
                selectedType = uiState.typeFilter,
                onTypeSelected = viewModel::setTypeFilter
            )

            when {
                uiState.isLoading -> LoadingContent()
                uiState.errorRes != null -> ErrorContent(
                    messageRes = requireNotNull(uiState.errorRes),
                    onRetry = { viewModel.setTypeFilter(uiState.typeFilter) }
                )

                uiState.media.isEmpty() -> EmptyMediaState()

                else -> MediaContent(
                    uiState = uiState,
                    onNavigateToViewer = onNavigateToViewer,
                    onDeleteMedia = viewModel::deleteMedia,
                    onSourceClick = { media ->
                        viewModel.navigateToSource(
                            media = media,
                            onNavigateToTask = onNavigateToTask,
                            onNavigateToNote = onNavigateToNote
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun MediaContent(
    uiState: MediaManagerUiState,
    onNavigateToViewer: (String) -> Unit,
    onDeleteMedia: (String) -> Unit,
    onSourceClick: (com.letsgotoperfection.kino.feature.media.internal.domain.model.Media) -> Unit
) {
    when (uiState.viewMode) {
        ViewMode.GRID -> MediaGridView(
            media = uiState.media,
            onMediaClick = onNavigateToViewer,
            onDeleteClick = onDeleteMedia,
            onSourceClick = onSourceClick,
            modifier = Modifier.fillMaxSize()
        )

        ViewMode.LIST -> MediaListView(
            media = uiState.media,
            onMediaClick = onNavigateToViewer,
            onDeleteClick = onDeleteMedia,
            onSourceClick = onSourceClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun MediaTypeFilterRow(
    selectedType: MediaType?,
    onTypeSelected: (MediaType?) -> Unit
) {
    val filters = listOf(
        null to R.string.media_filter_all,
        MediaType.IMAGE to R.string.media_filter_images,
        MediaType.VIDEO to R.string.media_filter_videos,
        MediaType.DOCUMENT to R.string.media_filter_documents
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters.size) { index ->
            val (type, labelRes) = filters[index]
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(stringResource(labelRes)) }
            )
        }
    }
}

@Composable
private fun ViewModeToggle(
    viewMode: ViewMode,
    onToggleViewMode: () -> Unit
) {
    IconButton(onClick = onToggleViewMode) {
        Icon(
            imageVector = if (viewMode == ViewMode.GRID) {
                Icons.AutoMirrored.Filled.List
            } else {
                Icons.Default.GridView
            },
            contentDescription = if (viewMode == ViewMode.GRID) {
                stringResource(R.string.media_list_view)
            } else {
                stringResource(R.string.media_grid_view)
            }
        )
    }
}

@Composable
private fun LoadingContent() {
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
private fun ErrorContent(
    messageRes: Int,
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
                text = stringResource(messageRes),
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
            modifier = Modifier.padding(32.dp),
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
