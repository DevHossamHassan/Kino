package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.preview.ComponentPreviews
import com.letsgotoperfection.kino.core.designsystem.preview.FontScalePreviews
import com.letsgotoperfection.kino.core.designsystem.preview.PreviewData
import com.letsgotoperfection.kino.core.designsystem.preview.TaskPreviewProvider
import com.letsgotoperfection.kino.core.designsystem.preview.ThemePreviews
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.model.Task

// Using Task from core model

@Composable
fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .height(180.dp)
            .clickable(onClick = onTaskClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(android.graphics.Color.parseColor(task.priorityColor)),
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = 20f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Priority badge
            PriorityBadge(priority = task.priority)
            
            // Title (2 lines max)
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Description (3 lines max)
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Progress bar
            if (task.progress > 0) {
                LinearProgressIndicator(
                    progress = { task.progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Labels
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(task.labels.take(2)) { label ->
                    LabelChip(label = label)
                }
                if (task.labels.size > 2) {
                    item {
                        Text(
                            text = "+${task.labels.size - 2}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            
            // Meta info
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.attachmentCount > 0) {
                    MetaInfo(
                        icon = Icons.Default.AttachFile,
                        text = task.attachmentCount.toString()
                    )
                }
                if (task.checklistTotal > 0) {
                    MetaInfo(
                        icon = Icons.Default.CheckBox,
                        text = "${task.checklistCompleted}/${task.checklistTotal}"
                    )
                }
                if (task.dueDate != null) {
                    MetaInfo(
                        icon = Icons.Default.CalendarToday,
                        text = task.dueDateFormatted ?: ""
                    )
                }
            }
        }
    }
}

/**
 * Preview with different task states
 */
@ThemePreviews
@Composable
private fun TaskCardPreview(
    @PreviewParameter(TaskPreviewProvider::class) task: Task
) {
    KinoTheme {
        Surface {
            TaskCard(
                task = task,
                onTaskClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Preview with high priority task
 */
@Preview(name = "High Priority", group = "Priority")
@Preview(name = "High Priority - Dark", group = "Priority", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TaskCardHighPriorityPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Preview with long text (edge case)
 */
@ThemePreviews
@Composable
private fun TaskCardLongTextPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh.copy(
                    title = "This is a very long task title that should be truncated with ellipsis after two lines to ensure proper layout",
                    description = "This is a very long description that goes on and on and should be truncated after three lines to ensure the card maintains its consistent height across all tasks in the kanban board view"
                ),
                onTaskClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Preview with minimal task (no metadata)
 */
@ThemePreviews
@Composable
private fun TaskCardMinimalPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = Task(
                    id = "min-1",
                    title = "Simple task",
                    description = "No extras",
                    section = com.letsgotoperfection.kino.core.model.TaskSection.PERSONAL,
                    column = com.letsgotoperfection.kino.core.model.TaskColumn.TODO_THIS_WEEK,
                    priority = com.letsgotoperfection.kino.core.model.Priority.LOW,
                    progress = 0,
                    labels = emptyList(),
                    attachments = emptyList(),
                    checklist = emptyList(),
                    dueDate = null,
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                ),
                onTaskClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Preview with all features (maximal task)
 */
@ThemePreviews
@Composable
private fun TaskCardMaximalPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Font scale accessibility preview
 */
@FontScalePreviews
@Composable
private fun TaskCardFontScalePreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskMedium,
                onTaskClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Interactive preview for testing interactions
 */
@Preview(
    name = "Task Card - Interactive",
    showBackground = true
)
@Composable
private fun TaskCardInteractivePreview() {
    KinoTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TaskCard(
                    task = PreviewData.sampleTaskHigh,
                    onTaskClick = {
                        // Preview only - no interaction
                    }
                )
            }
        }
    }
}

@Composable
private fun MetaInfo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
