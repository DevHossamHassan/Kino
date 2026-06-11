package com.letsgotoperfection.kino.feature.media.internal.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Grid view for displaying media items.
 */
@Composable
internal fun MediaGridView(
    media: List<Media>,
    onMediaClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onSourceClick: (Media) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 110.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        items(media, key = { it.id }) { item ->
            MediaGridItem(
                media = item,
                onClick = { onMediaClick(item.id) },
                onDeleteClick = { onDeleteClick(item.id) },
                onSourceClick = { onSourceClick(item) }
            )
        }
    }
}

/**
 * List view for displaying media items with detailed metadata.
 */
@Composable
internal fun MediaListView(
    media: List<Media>,
    onMediaClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onSourceClick: (Media) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(media, key = { it.id }) { item ->
            MediaListItem(
                media = item,
                onClick = { onMediaClick(item.id) },
                onDeleteClick = { onDeleteClick(item.id) },
                onSourceClick = { onSourceClick(item) }
            )
        }
    }
}

@Composable
private fun MediaGridItem(
    media: Media,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSourceClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(media.thumbnailUri ?: media.uri)
                    .crossfade(true)
                    .build(),
                contentDescription = media.filename,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (MediaType.fromMimeType(media.mimeType) == MediaType.VIDEO) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = formatDuration(media.duration ?: 0),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            MediaTypeIndicator(
                mediaType = MediaType.fromMimeType(media.mimeType),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            )

            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.media_more_options),
                    tint = Color.White
                )
                MediaItemMenu(
                    expanded = showMenu,
                    onDismiss = { showMenu = false },
                    onSourceClick = onSourceClick,
                    onDeleteClick = onDeleteClick
                )
            }
        }
    }
}

@Composable
private fun MediaListItem(
    media: Media,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSourceClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(media.thumbnailUri ?: media.uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = media.filename,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                MediaTypeIndicator(
                    mediaType = MediaType.fromMimeType(media.mimeType),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = media.filename,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatFileSize(media.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatDate(media.dateAdded),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatSource(media.sourceType, media.sourceId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.media_more_options)
                )
                MediaItemMenu(
                    expanded = showMenu,
                    onDismiss = { showMenu = false },
                    onSourceClick = onSourceClick,
                    onDeleteClick = onDeleteClick
                )
            }
        }
    }
}

@Composable
private fun MediaItemMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSourceClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.media_go_to_source)) },
            onClick = {
                onSourceClick()
                onDismiss()
            },
            leadingIcon = {
                Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null)
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.media_delete)) },
            onClick = {
                onDeleteClick()
                onDismiss()
            },
            leadingIcon = {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        )
    }
}

@Composable
private fun MediaTypeIndicator(
    mediaType: MediaType,
    modifier: Modifier = Modifier
) {
    val labelRes = when (mediaType) {
        MediaType.IMAGE -> R.string.media_type_image
        MediaType.VIDEO -> R.string.media_type_video
        MediaType.DOCUMENT -> R.string.media_type_document
        MediaType.AUDIO -> R.string.media_type_audio
        MediaType.OTHER -> R.string.media_type_other
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = stringResource(labelRes),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun formatSource(sourceType: MediaSourceType, sourceId: String): String {
    return when (sourceType) {
        MediaSourceType.TASK -> stringResource(R.string.media_source_task, sourceId)
        MediaSourceType.NOTE -> stringResource(R.string.media_source_note, sourceId)
    }
}

internal fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

internal fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    val locale = Locale.getDefault()
    return when {
        gb >= 1 -> String.format(locale, "%.1f GB", gb)
        mb >= 1 -> String.format(locale, "%.1f MB", mb)
        kb >= 1 -> String.format(locale, "%.1f KB", kb)
        else -> String.format(locale, "%d B", bytes)
    }
}

internal fun formatDate(dateTime: LocalDateTime): String {
    return dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}

internal fun formatDateTime(dateTime: LocalDateTime): String {
    return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))
}
