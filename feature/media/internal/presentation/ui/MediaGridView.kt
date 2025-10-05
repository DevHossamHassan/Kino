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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType.Companion.fromMimeType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Grid view for displaying media items
 * 
 * Shows media in a responsive grid with thumbnails, metadata, and actions
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MediaGridView(
    media: List<Media>,
    onMediaClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onSourceClick: (Media) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
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
            // Media thumbnail
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(media.thumbnailUri ?: media.uri)
                    .crossfade(true)
                    .build(),
                contentDescription = media.filename,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Video duration overlay
            if (fromMimeType(media.mimeType) == MediaType.VIDEO) {
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
            
            // Media type indicator
            MediaTypeIndicator(
                mediaType = fromMimeType(media.mimeType),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            )
            
            // Menu button
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.media_more_options),
                    tint = Color.White
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

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}
