package com.letsgotoperfection.kino.feature.media.internal.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Launch
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
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType.Companion.fromMimeType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * List view for displaying media items
 * 
 * Shows media in a vertical list with detailed information and thumbnails
 */
@OptIn(ExperimentalFoundationApi::class)
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
            // Media thumbnail
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
                
                // Media type indicator
                MediaTypeIndicator(
                    mediaType = fromMimeType(media.mimeType),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Media information
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Menu button
            IconButton(
                onClick = { showMenu = true }
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.media_more_options)
                )
            }
            
            // Dropdown menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.media_go_to_source)) },
                    onClick = {
                        onSourceClick()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Launch, contentDescription = null)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.media_delete)) },
                    onClick = {
                        onDeleteClick()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Composable
private fun MediaTypeIndicator(
    mediaType: MediaType,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (mediaType) {
        MediaType.IMAGE -> "IMG" to Color.Blue
        MediaType.VIDEO -> "VID" to Color.Red
        MediaType.DOCUMENT -> "DOC" to Color.Green
        MediaType.AUDIO -> "AUD" to Color.Orange
        MediaType.OTHER -> "FILE" to Color.Gray
    }
    
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.8f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> String.format("%.1f GB", gb)
        mb >= 1 -> String.format("%.1f MB", mb)
        kb >= 1 -> String.format("%.1f KB", kb)
        else -> "$bytes B"
    }
}

private fun formatDate(date: java.time.LocalDateTime): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val instant = date.atZone(java.time.ZoneId.systemDefault()).toInstant()
    return formatter.format(Date.from(instant))
}

private fun formatSource(sourceType: MediaSourceType, sourceId: String): String {
    return when (sourceType) {
        MediaSourceType.TASK -> "Task: $sourceId"
        MediaSourceType.NOTE -> "Note: $sourceId"
    }
}
