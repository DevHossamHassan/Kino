package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressPercent = task.progress.coerceIn(0, 100)
    val labelTexts = buildList {
        task.labels
            .take(2)
            .mapNotNull { label -> label.name.takeIf { it.isNotBlank() } }
            .forEach { add(it) }
        if (task.labels.size > 2) {
            add("+${task.labels.size - 2}")
        }
        add(task.priority.displayName)
    }.distinct()

    // Build comprehensive accessibility content description using utility
    val accessibilityDescription = AccessibilityUtils.createTaskCardDescription(task)

    Card(
        modifier = modifier
            .width(280.dp)
            .clickable(onClick = onTaskClick)
            .semantics {
                role = Role.Button
                contentDescription = accessibilityDescription
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (progressPercent > 0) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .accessibility(
                                contentDescription = "Progress: $progressPercent percent"
                            ),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                    Text(
                        text = "$progressPercent%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.accessibility(
                            contentDescription = "Progress: $progressPercent percent"
                        )
                    )
                }
            }

            if (labelTexts.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    labelTexts.forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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
