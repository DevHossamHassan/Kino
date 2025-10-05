package com.letsgotoperfection.kino.feature.media.internal.presentation.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.component.*
import com.letsgotoperfection.kino.core.designsystem.preview.*
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.model.*

/**
 * Preview with grid view
 */
@Preview(
    name = "Media Manager - Grid View",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Preview(
    name = "Media Manager - Grid View Dark",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MediaManagerGridViewPreview() {
    KinoTheme {
        Surface {
            MediaManagerScreen(
                viewMode = ViewMode.GRID,
                mediaList = listOf(
                    PreviewData.sampleMediaImage,
                    PreviewData.sampleMediaPdf,
                    PreviewData.sampleMediaVideo
                ),
                onNavigateBack = {},
                onNavigateToSource = { _, _ -> }
            )
        }
    }
}

/**
 * Preview with list view
 */
@Preview(
    name = "Media Manager - List View",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun MediaManagerListViewPreview() {
    KinoTheme {
        Surface {
            MediaManagerScreen(
                viewMode = ViewMode.LIST,
                mediaList = listOf(
                    PreviewData.sampleMediaImage,
                    PreviewData.sampleMediaPdf,
                    PreviewData.sampleMediaVideo
                ),
                onNavigateBack = {},
                onNavigateToSource = { _, _ -> }
            )
        }
    }
}

/**
 * Preview with empty state
 */
@Preview(
    name = "Media Manager - Empty",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun MediaManagerEmptyPreview() {
    KinoTheme {
        Surface {
            EmptyState(
                icon = Icons.Default.AttachFile,
                title = "No media files",
                message = "Upload or attach files to your tasks and notes",
                actionLabel = "Upload Files",
                onActionClick = {}
            )
        }
    }
}

/**
 * Preview with different media types
 */
@Preview(
    name = "Media Manager - Different Types",
    showBackground = true
)
@Composable
private fun MediaManagerTypesPreview(
    @PreviewParameter(MediaPreviewProvider::class) media: MediaFile
) {
    KinoTheme {
        Surface {
            MediaManagerScreen(
                viewMode = ViewMode.GRID,
                mediaList = listOf(media),
                onNavigateBack = {},
                onNavigateToSource = { _, _ -> }
            )
        }
    }
}

/**
 * Preview on tablet
 */
@Preview(
    name = "Media Manager - Tablet",
    device = "spec:width=600dp,height=1024dp,dpi=240",
    showSystemUi = true
)
@Composable
private fun MediaManagerTabletPreview() {
    KinoTheme {
        Surface {
            MediaManagerScreen(
                viewMode = ViewMode.GRID,
                mediaList = listOf(
                    PreviewData.sampleMediaImage,
                    PreviewData.sampleMediaPdf,
                    PreviewData.sampleMediaVideo
                ),
                onNavigateBack = {},
                onNavigateToSource = { _, _ -> }
            )
        }
    }
}

/**
 * Font scale accessibility preview
 */
@Preview(
    name = "Media Manager - Font Scale Large",
    fontScale = 1.5f,
    showBackground = true
)
@Composable
private fun MediaManagerFontScalePreview() {
    KinoTheme {
        Surface {
            MediaManagerScreen(
                viewMode = ViewMode.LIST,
                mediaList = listOf(PreviewData.sampleMediaImage),
                onNavigateBack = {},
                onNavigateToSource = { _, _ -> }
            )
        }
    }
}

/**
 * RTL preview for Arabic support
 */
@Preview(
    name = "Media Manager - RTL",
    locale = "ar",
    showBackground = true
)
@Composable
private fun MediaManagerRTLPreview() {
    KinoTheme {
        Surface {
            MediaManagerScreen(
                viewMode = ViewMode.GRID,
                mediaList = listOf(PreviewData.sampleMediaImage),
                onNavigateBack = {},
                onNavigateToSource = { _, _ -> }
            )
        }
    }
}

/**
 * Mock ViewMode enum
 */
enum class ViewMode {
    GRID, LIST
}

/**
 * Mock Media Manager Screen for previews
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaManagerScreen(
    viewMode: ViewMode,
    mediaList: List<MediaFile>,
    onNavigateBack: () -> Unit,
    onNavigateToSource: (String, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Media Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            if (viewMode == ViewMode.GRID) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = "Toggle View"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (mediaList.isEmpty()) {
            EmptyState(
                icon = Icons.Default.AttachFile,
                title = "No media files",
                message = "Upload or attach files to your tasks and notes",
                actionLabel = "Upload Files",
                onActionClick = {}
            )
        } else {
            when (viewMode) {
                ViewMode.GRID -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(mediaList) { media ->
                            MediaGridItem(
                                media = media,
                                onClick = { onNavigateToSource(media.targetId ?: "", media.targetType ?: "") }
                            )
                        }
                    }
                }
                ViewMode.LIST -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(mediaList) { media ->
                            MediaListItem(
                                media = media,
                                onClick = { onNavigateToSource(media.targetId ?: "", media.targetType ?: "") }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Mock Media Grid Item for previews
 */
@Composable
private fun MediaGridItem(
    media: MediaFile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = when {
                    media.mimeType.startsWith("image/") -> Icons.Default.Image
                    media.mimeType.startsWith("video/") -> Icons.Default.VideoFile
                    media.mimeType == "application/pdf" -> Icons.Default.PictureAsPdf
                    else -> Icons.Default.AttachFile
                },
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = media.filename,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatFileSize(media.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Mock Media List Item for previews
 */
@Composable
private fun MediaListItem(
    media: MediaFile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = when {
                    media.mimeType.startsWith("image/") -> Icons.Default.Image
                    media.mimeType.startsWith("video/") -> Icons.Default.VideoFile
                    media.mimeType == "application/pdf" -> Icons.Default.PictureAsPdf
                    else -> Icons.Default.AttachFile
                },
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = media.filename,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${formatFileSize(media.size)} • ${media.mimeType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Open",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Helper function to format file size
 */
private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> "%.1f GB".format(gb)
        mb >= 1 -> "%.1f MB".format(mb)
        kb >= 1 -> "%.1f KB".format(kb)
        else -> "$bytes B"
    }
}
