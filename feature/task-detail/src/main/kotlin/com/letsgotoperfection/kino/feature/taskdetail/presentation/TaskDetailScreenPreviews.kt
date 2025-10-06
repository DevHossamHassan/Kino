package com.letsgotoperfection.kino.feature.taskdetail.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
 * Preview with full task data
 */
@Preview(
    name = "Task Detail - Light",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Preview(
    name = "Task Detail - Dark",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun TaskDetailScreenPreview() {
    KinoTheme {
        Surface {
            TaskDetailScreen(
                task = PreviewData.sampleTaskHigh,
                checklist = PreviewData.sampleChecklist,
                attachments = PreviewData.sampleTaskHigh.attachments,
                onNavigateBack = {},
                onNavigateToMedia = {}
            )
        }
    }
}

/**
 * Preview with minimal task data
 */
@Preview(
    name = "Task Detail - Minimal",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun TaskDetailScreenMinimalPreview() {
    KinoTheme {
        Surface {
            TaskDetailScreen(
                task = PreviewData.sampleTaskLow,
                checklist = emptyList(),
                attachments = emptyList(),
                onNavigateBack = {},
                onNavigateToMedia = {}
            )
        }
    }
}

/**
 * Preview with completed task
 */
@Preview(
    name = "Task Detail - Completed",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun TaskDetailScreenCompletedPreview() {
    KinoTheme {
        Surface {
            TaskDetailScreen(
                task = PreviewData.sampleTaskCompleted,
                checklist = PreviewData.sampleChecklist,
                attachments = PreviewData.sampleTaskCompleted.attachments,
                onNavigateBack = {},
                onNavigateToMedia = {}
            )
        }
    }
}

/**
 * Preview with overdue task
 */
@Preview(
    name = "Task Detail - Overdue",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun TaskDetailScreenOverduePreview() {
    KinoTheme {
        Surface {
            TaskDetailScreen(
                task = PreviewData.sampleTaskOverdue,
                checklist = PreviewData.sampleChecklist.take(5),
                attachments = PreviewData.sampleTaskOverdue.attachments,
                onNavigateBack = {},
                onNavigateToMedia = {}
            )
        }
    }
}

/**
 * Preview on tablet
 */
@Preview(
    name = "Task Detail - Tablet",
    device = "spec:width=600dp,height=1024dp,dpi=240",
    showSystemUi = true
)
@Composable
private fun TaskDetailScreenTabletPreview() {
    KinoTheme {
        Surface {
            TaskDetailScreen(
                task = PreviewData.sampleTaskHigh,
                checklist = PreviewData.sampleChecklist,
                attachments = PreviewData.sampleTaskHigh.attachments,
                onNavigateBack = {},
                onNavigateToMedia = {}
            )
        }
    }
}

/**
 * Font scale accessibility preview
 */
@Preview(
    name = "Task Detail - Font Scale Large",
    fontScale = 1.5f,
    showBackground = true
)
@Composable
private fun TaskDetailScreenFontScalePreview() {
    KinoTheme {
        Surface {
            TaskDetailScreen(
                task = PreviewData.sampleTaskMedium,
                checklist = PreviewData.sampleChecklist.take(3),
                attachments = emptyList(),
                onNavigateBack = {},
                onNavigateToMedia = {}
            )
        }
    }
}

/**
 * RTL preview for Arabic support
 */
@Preview(
    name = "Task Detail - RTL",
    locale = "ar",
    showBackground = true
)
@Composable
private fun TaskDetailScreenRTLPreview() {
    KinoTheme {
        Surface {
            TaskDetailScreen(
                task = PreviewData.sampleTaskHigh,
                checklist = PreviewData.sampleChecklist.take(2),
                attachments = PreviewData.sampleTaskHigh.attachments,
                onNavigateBack = {},
                onNavigateToMedia = {}
            )
        }
    }
}

/**
 * Mock Task Detail Screen for previews
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailScreen(
    task: Task,
    checklist: List<ChecklistItem>,
    attachments: List<Attachment>,
    onNavigateBack: () -> Unit,
    onNavigateToMedia: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Header
            item {
                TaskDetailHeader(task = task)
            }
            
            // Progress Section
            if (task.progress > 0) {
                item {
                    TaskProgressSection(
                        progress = task.progress,
                        checklistCompleted = task.checklistCompleted,
                        checklistTotal = task.checklistTotal
                    )
                }
            }
            
            // Labels Section
            if (task.labels.isNotEmpty()) {
                item {
                    TaskLabelsSection(labels = task.labels)
                }
            }
            
            // Checklist Section
            if (checklist.isNotEmpty()) {
                item {
                    TaskChecklistSection(
                        checklist = checklist,
                        onItemToggle = {}
                    )
                }
            }
            
            // Attachments Section
            if (attachments.isNotEmpty()) {
                item {
                    TaskAttachmentsSection(
                        attachments = attachments,
                        onNavigateToMedia = onNavigateToMedia
                    )
                }
            }
            
            // Due Date Section
            task.dueDate?.let { dueDate ->
                item {
                    TaskDueDateSection(dueDate = dueDate)
                }
            }
        }
    }
}

/**
 * Mock Task Detail Header
 */
@Composable
private fun TaskDetailHeader(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityBadge(priority = task.priority)
                Text(
                    text = task.section.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Mock Task Progress Section
 */
@Composable
private fun TaskProgressSection(
    progress: Int,
    checklistCompleted: Int,
    checklistTotal: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$progress% Complete",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$checklistCompleted/$checklistTotal checklist items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Mock Task Labels Section
 */
@Composable
private fun TaskLabelsSection(labels: List<Label>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Labels",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(labels) { label ->
                    LabelChip(label = label)
                }
            }
        }
    }
}

/**
 * Mock Task Checklist Section
 */
@Composable
private fun TaskChecklistSection(
    checklist: List<ChecklistItem>,
    onItemToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Checklist",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            checklist.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = { onItemToggle(item.id) }
                    )
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Mock Task Attachments Section
 */
@Composable
private fun TaskAttachmentsSection(
    attachments: List<Attachment>,
    onNavigateToMedia: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Attachments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToMedia) {
                    Text("View All")
                }
            }
            
            attachments.take(3).forEach { attachment ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = when {
                            attachment.mimeType.startsWith("image/") -> Icons.Default.Image
                            attachment.mimeType.startsWith("video/") -> Icons.Default.VideoFile
                            attachment.mimeType == "application/pdf" -> Icons.Default.PictureAsPdf
                            else -> Icons.Default.AttachFile
                        },
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = attachment.filename,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatFileSize(attachment.size),
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
    }
}

/**
 * Mock Task Due Date Section
 */
@Composable
private fun TaskDueDateSection(dueDate: java.time.LocalDateTime) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "Due Date",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dueDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
