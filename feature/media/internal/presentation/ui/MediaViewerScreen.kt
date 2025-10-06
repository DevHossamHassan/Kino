package com.letsgotoperfection.kino.feature.media.internal.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaViewerEvent
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaViewerUiState
import com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel.MediaViewerViewModel
import java.text.NumberFormat
import java.time.format.DateTimeFormatter

/**
 * Media Viewer Screen - View and interact with media files
 * Supports images, videos, and documents with zoom, pan, and navigation features
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaViewerScreen(
    mediaId: String,
    onNavigateBack: () -> Unit,
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    viewModel: MediaViewerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvent.collectAsStateWithLifecycle()

    // Handle events
    LaunchedEffect(uiEvent) {
        when (uiEvent) {
            is MediaViewerEvent.NavigateToTask -> onNavigateToTask(uiEvent.taskId)
            is MediaViewerEvent.NavigateToNote -> onNavigateToNote(uiEvent.noteId)
            is MediaViewerEvent.MediaDeleted -> onNavigateBack()
            is MediaViewerEvent.ShowError -> {
                // Error handling could be improved with snackbar
            }
            null -> {}
        }
        viewModel.clearEvent()
    }

    // Load media on first composition
    LaunchedEffect(mediaId) {
        viewModel.loadMedia(mediaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.media?.filename ?: "Media Viewer",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.semantics {
                            role = Role.Button
                            contentDescription = "Navigate back"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.media != null) {
                        IconButton(
                            onClick = { viewModel.navigateToSource() },
                            modifier = Modifier.semantics {
                                role = Role.Button
                                contentDescription = "Go to source"
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Go to source"
                            )
                        }
                        IconButton(
                            onClick = { viewModel.deleteMedia() },
                            modifier = Modifier.semantics {
                                role = Role.Button
                                contentDescription = "Delete media"
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete media"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = { viewModel.loadMedia(mediaId) }
                    )
                }
                uiState.media != null -> {
                    MediaContent(
                        media = uiState.media,
                        mediaType = uiState.mediaType
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "Loading media"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading media...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
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
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Failed to load media",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun MediaContent(
    media: Media,
    mediaType: MediaType?
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Media display area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (mediaType) {
                MediaType.IMAGE -> ImageViewer(media = media)
                MediaType.VIDEO -> VideoViewer(media = media)
                MediaType.DOCUMENT -> DocumentViewer(media = media)
                MediaType.AUDIO -> AudioViewer(media = media)
                else -> UnsupportedMediaViewer(media = media)
            }
        }
        
        // Media info panel
        MediaInfoPanel(media = media)
    }
}

@Composable
private fun ImageViewer(
    media: Media
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3f)
        offsetX = (offsetX + panChange.x).coerceIn(-500f, 500f)
        offsetY = (offsetY + panChange.y).coerceIn(-500f, 500f)
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
private fun VideoViewer(
    media: Media
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // TODO: Implement video player with ExoPlayer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play video",
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
            Text(
                text = "Video Player\n\nTODO: Implement ExoPlayer integration",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Duration: ${media.duration?.let { "${it / 1000}s" } ?: "Unknown"}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DocumentViewer(
    media: Media
) {
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
                imageVector = Icons.Default.Description,
                contentDescription = "Document",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Document Viewer\n\nTODO: Implement PDF viewer",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "File: ${media.filename}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AudioViewer(
    media: Media
) {
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
                imageVector = Icons.Default.AudioFile,
                contentDescription = "Audio",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Audio Player\n\nTODO: Implement audio player",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Duration: ${media.duration?.let { "${it / 1000}s" } ?: "Unknown"}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UnsupportedMediaViewer(
    media: Media
) {
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
                imageVector = Icons.Default.FilePresent,
                contentDescription = "File",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Unsupported Media Type",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Type: ${media.mimeType}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MediaInfoPanel(
    media: Media
) {
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
                text = "Media Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            InfoRow("Filename", media.filename)
            InfoRow("Type", media.mimeType)
            InfoRow("Size", formatFileSize(media.size))
            
            media.width?.let { width ->
                media.height?.let { height ->
                    InfoRow("Dimensions", "${width}x${height}")
                }
            }
            
            media.duration?.let { duration ->
                InfoRow("Duration", "${duration / 1000}s")
            }
            
            InfoRow("Added", media.dateAdded.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")))
            InfoRow("Source", "${media.sourceType.name} - ${media.sourceId}")
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
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    val formatter = NumberFormat.getInstance()
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${formatter.format(bytes / 1024)} KB"
        bytes < 1024 * 1024 * 1024 -> "${formatter.format(bytes / (1024 * 1024))} MB"
        else -> "${formatter.format(bytes / (1024 * 1024 * 1024))} GB"
    }
}