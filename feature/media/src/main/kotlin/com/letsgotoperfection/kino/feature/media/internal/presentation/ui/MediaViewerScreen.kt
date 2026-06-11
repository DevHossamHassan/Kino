package com.letsgotoperfection.kino.feature.media.internal.presentation.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaViewerEvent
import com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel.MediaViewerViewModel

private const val MIN_ZOOM = 0.5f
private const val MAX_ZOOM = 3f
private const val MAX_PAN = 500f

/**
 * Media viewer screen.
 *
 * Shows an image with zoom/pan or metadata plus an external-open action
 * for other media types.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaViewerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    viewModel: MediaViewerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MediaViewerEvent.NavigateToTask -> onNavigateToTask(event.taskId)
                is MediaViewerEvent.NavigateToNote -> onNavigateToNote(event.noteId)
                is MediaViewerEvent.MediaDeleted -> onNavigateBack()
                is MediaViewerEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.media?.filename
                            ?: stringResource(R.string.media_viewer_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    if (uiState.media != null) {
                        IconButton(onClick = viewModel::navigateToSource) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = stringResource(R.string.media_open_source)
                            )
                        }
                        IconButton(onClick = viewModel::deleteMedia) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.media_delete)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> LoadingContent()

                uiState.errorRes != null -> ErrorContent(
                    messageRes = requireNotNull(uiState.errorRes),
                    onRetry = viewModel::loadMedia
                )

                uiState.media != null -> MediaViewerContent(
                    media = requireNotNull(uiState.media),
                    mediaType = uiState.mediaType,
                    snackbarHostState = snackbarHostState
                )
            }
        }
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
                style = MaterialTheme.typography.bodyLarge
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
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(messageRes),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun MediaViewerContent(
    media: Media,
    mediaType: MediaType?,
    snackbarHostState: SnackbarHostState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (mediaType) {
                MediaType.IMAGE -> ImageViewer(media = media)
                else -> NonImagePreview(
                    media = media,
                    mediaType = mediaType,
                    snackbarHostState = snackbarHostState
                )
            }
        }

        MediaInfoPanel(media = media)
    }
}

@Composable
private fun ImageViewer(media: Media) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(MIN_ZOOM, MAX_ZOOM)
        offsetX = (offsetX + panChange.x).coerceIn(-MAX_PAN, MAX_PAN)
        offsetY = (offsetY + panChange.y).coerceIn(-MAX_PAN, MAX_PAN)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .transformable(state = transformableState)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(media.uri)
                .crossfade(true)
                .build(),
            contentDescription = media.filename,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun NonImagePreview(
    media: Media,
    mediaType: MediaType?,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val openFailedMessage = stringResource(R.string.media_open_external_error)
    var openFailed by remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(openFailed) {
        if (openFailed) {
            snackbarHostState.showSnackbar(openFailedMessage)
            openFailed = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = mediaType.icon(),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = media.filename,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Text(
                text = stringResource(R.string.media_preview_unavailable),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(media.uri, media.mimeType)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        openFailed = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Launch,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.media_open_external),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun MediaInfoPanel(media: Media) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.media_metadata_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            InfoRow(stringResource(R.string.media_metadata_type), media.mimeType)
            InfoRow(stringResource(R.string.media_metadata_size), formatFileSize(media.size))

            media.width?.let { width ->
                media.height?.let { height ->
                    InfoRow(
                        stringResource(R.string.media_metadata_dimensions),
                        "$width × $height"
                    )
                }
            }

            media.duration?.let { duration ->
                InfoRow(
                    stringResource(R.string.media_metadata_duration),
                    formatDuration(duration)
                )
            }

            InfoRow(stringResource(R.string.media_metadata_added), formatDateTime(media.dateAdded))

            val sourceLabel = when (media.sourceType) {
                MediaSourceType.TASK -> stringResource(R.string.media_source_task, media.sourceId)
                MediaSourceType.NOTE -> stringResource(R.string.media_source_note, media.sourceId)
            }
            InfoRow(stringResource(R.string.media_go_to_source), sourceLabel)
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

private fun MediaType?.icon(): ImageVector = when (this) {
    MediaType.VIDEO -> Icons.Default.VideoFile
    MediaType.AUDIO -> Icons.Default.AudioFile
    MediaType.DOCUMENT -> Icons.Default.Description
    else -> Icons.AutoMirrored.Filled.InsertDriveFile
}
