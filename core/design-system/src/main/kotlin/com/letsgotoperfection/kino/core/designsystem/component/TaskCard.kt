package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.designsystem.accessibility.AccessibilityUtils
import com.letsgotoperfection.kino.core.designsystem.accessibility.AccessibilityUtils.accessibility
import com.letsgotoperfection.kino.core.designsystem.preview.ComponentPreviews
import com.letsgotoperfection.kino.core.designsystem.preview.FontScalePreviews
import com.letsgotoperfection.kino.core.designsystem.preview.PreviewData
import com.letsgotoperfection.kino.core.designsystem.preview.TaskPreviewProvider
import com.letsgotoperfection.kino.core.designsystem.preview.ThemePreviews
import com.letsgotoperfection.kino.core.model.Task
import java.time.LocalDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressPercent = task.progress.coerceIn(0, 100)
    
    // Check if task is overdue
    val isOverdue = remember(task.dueDate) {
        task.dueDate?.let { it.isBefore(LocalDateTime.now()) } ?: false
    }
    
    // Determine if checklist is complete
    val checklistProgress = remember(task.checklist) {
        if (task.checklistTotal > 0) {
            task.checklistCompleted.toFloat() / task.checklistTotal.toFloat()
        } else null
    }
    
    // Build comprehensive accessibility content description using utility
    val accessibilityDescription = AccessibilityUtils.createTaskCardDescription(task)
    
    // Priority color for accent
    val priorityColor = remember(task.priority) {
        Color(android.graphics.Color.parseColor(task.priority.colorHex))
    }

    Card(
        modifier = modifier
            .widthIn(min = 240.dp, max = 320.dp)
            .clickable(onClick = onTaskClick)
            .semantics {
                role = Role.Button
                contentDescription = accessibilityDescription
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Priority accent bar at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(priorityColor)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section badge and priority
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Section indicator
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.semantics {
                            contentDescription = "Section: ${task.section.displayName}"
                        }
                    ) {
                        Text(
                            text = task.section.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    
                    // Compact priority badge
                    Surface(
                        shape = CircleShape,
                        color = priorityColor.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(8.dp)
                            .semantics {
                                contentDescription = "Priority: ${task.priority.displayName}"
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(priorityColor, CircleShape)
                        )
                    }
                }
                
                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Description (if available)
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Labels with FlowRow for better wrapping
                if (task.labels.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        task.labels.take(3).forEach { label ->
                            val labelColor = remember(label.color) {
                                try {
                                    Color(android.graphics.Color.parseColor(label.color))
                                } catch (e: Exception) {
                                    Color.Gray
                                }
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = labelColor.copy(alpha = 0.15f),
                                modifier = Modifier.border(
                                    width = 1.dp,
                                    color = labelColor.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            ) {
                                Text(
                                    text = label.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = labelColor.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        
                        // Show more indicator
                        if (task.labels.size > 3) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "+${task.labels.size - 3}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
                
                // Checklist progress (if available)
                checklistProgress?.let { progress ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (progress == 1f) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .semantics {
                                    contentDescription = "Checklist: ${task.checklistCompleted} of ${task.checklistTotal} completed"
                                },
                            color = if (progress == 1f) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.tertiary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        
                        Text(
                            text = "${task.checklistCompleted}/${task.checklistTotal}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Footer: Due date and metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Due date
                    task.dueDate?.let { dueDate ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isOverdue) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Text(
                                text = formatDate(dueDate),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOverdue) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = if (isOverdue) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    } ?: Spacer(modifier = Modifier.width(1.dp))
                    
                    // Metadata: Attachments
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Attachments count
                        if (task.attachmentCount > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Attachment,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = task.attachmentCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(date: java.time.LocalDateTime): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd")
    return date.format(formatter)
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
