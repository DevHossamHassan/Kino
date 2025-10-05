package com.letsgotoperfection.kino.feature.media.internal.presentation.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.letsgotoperfection.kino.feature.media.R
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType.Companion.fromMimeType
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaViewerUiState
import com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel.MediaViewerViewModel
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@Composable
fun MediaViewerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    viewModel: MediaViewerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MediaViewerTopBar(onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingContent(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorContent(
                    message = uiState.error,
                    onRetry = viewModel::loadMedia,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.media != null -> {
                MediaViewerContent(
                    media = uiState.media,
                    onNavigateToTask = onNavigateToTask,
                    onNavigateToNote = onNavigateToNote,
                    onOpenSource = viewModel::openSource,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaViewerTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.media_viewer_title)) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cancel)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.media_loading))
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun MediaViewerContent(
    media: Media,
    onNavigateToTask: (String) -> Unit,
    onNavigateToNote: (String) -> Unit,
    onOpenSource: ( (String) -> Unit, (String) -> Unit ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mediaType = fromMimeType(media.mimeType)
    var showErrorDialog by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (mediaType) {
            MediaType.IMAGE -> ImageViewerContent(media)
            MediaType.VIDEO -> VideoViewerContent(media.uri)
            MediaType.AUDIO -> AudioViewerContent(media.uri)
            else -> FilePreviewContent(mediaType)
        }

        MediaMetaDataSection(media = media)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { onOpenSource(onNavigateToTask, onNavigateToNote) }) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.media_open_source))
            }

            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(media.uri, media.mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    showErrorDialog = stringResource(R.string.media_open_external_error)
                }
            }) {
                Icon(Icons.Default.Launch, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.media_open_external))
            }
        }
    }

    showErrorDialog?.let { message ->
        AlertDialog(
            onDismissRequest = { showErrorDialog = null },
            confirmButton = {
        Button(onClick = { showErrorDialog = null }) {
            Text(stringResource(R.string.cancel))
        }
            },
            text = { Text(message) }
        )
    }
}

@Composable
private fun ImageViewerContent(media: Media) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(media.uri)
            .crossfade(true)
            .build(),
        contentDescription = media.filename,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Composable
private fun VideoViewerContent(uri: Uri) {
    var videoView: VideoView? by remember { mutableStateOf(null) }
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            VideoView(context).apply {
                val controller = MediaController(context)
                controller.setAnchorView(this)
                setVideoURI(uri)
                setMediaController(controller)
                start()
                videoView = this
            }
        },
        update = { view ->
            view.setVideoURI(uri)
            view.start()
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            videoView?.stopPlayback()
        }
    }
}

@Composable
private fun AudioViewerContent(uri: Uri) {
    val context = LocalContext.current
    var mediaPlayer by remember(uri) { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(uri) {
        mediaPlayer?.release()
        mediaPlayer = runCatching {
            MediaPlayer().apply {
                setDataSource(context, uri)
                prepare()
                setOnCompletionListener {
                    isPlaying = false
                }
            }
        }.getOrNull()
    }

    DisposableEffect(uri) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Default.AudioFile, contentDescription = null, modifier = Modifier.size(40.dp))
        Button(onClick = {
            mediaPlayer?.let { player ->
                if (isPlaying) {
                    player.pause()
                } else {
                    player.start()
                }
                isPlaying = !isPlaying
            }
        }) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isPlaying) stringResource(R.string.media_pause_audio) else stringResource(R.string.media_play_audio)
            )
        }
    }
}

@Composable
private fun FilePreviewContent(mediaType: MediaType) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = when (mediaType) {
                MediaType.DOCUMENT -> Icons.Default.Info
                MediaType.OTHER -> Icons.Default.Info
                else -> Icons.Default.Image
            },
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = stringResource(R.string.media_open_external),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun MediaMetaDataSection(media: Media) {
    val mediaType = fromMimeType(media.mimeType)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.media_metadata_header),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        MetadataRow(stringResource(R.string.media_metadata_type), mediaType.name.lowercase().replaceFirstChar { it.uppercase() })
        MetadataRow(stringResource(R.string.media_metadata_size), formatFileSize(media.size))
        media.width?.let { width ->
            val height = media.height ?: 0
            MetadataRow(
                stringResource(R.string.media_metadata_dimensions),
                if (height > 0) "${width}x$height" else "$width px"
            )
        }
        media.duration?.let {
            MetadataRow(
                stringResource(R.string.media_metadata_duration),
                formatDuration(it)
            )
        }
        MetadataRow(
            stringResource(R.string.media_metadata_added),
            formatDate(media.dateAdded)
        )
        MetadataRow(
            stringResource(R.string.media_metadata_modified),
            formatDate(media.dateModified)
        )
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
    Spacer(modifier = Modifier.height(6.dp))
}

private fun formatFileSize(sizeBytes: Long): String {
    val kb = sizeBytes / 1024.0
    val mb = kb / 1024.0
    return when {
        mb >= 1 -> String.format(Locale.getDefault(), "%.1f MB", mb)
        kb >= 1 -> String.format(Locale.getDefault(), "%.1f KB", kb)
        else -> String.format(Locale.getDefault(), "%d B", sizeBytes)
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = (durationMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

private fun formatDate(date: java.time.LocalDateTime): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val instant = date.atZone(ZoneId.systemDefault()).toInstant()
    return formatter.format(Date.from(instant))
}
