package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.preview.ComponentPreviews
import com.letsgotoperfection.kino.core.designsystem.preview.ThemePreviews
import com.letsgotoperfection.kino.core.designsystem.KinoTheme

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onActionClick != null) {
            Button(onClick = onActionClick) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * Preview with action button
 */
@ThemePreviews
@Composable
private fun EmptyStateWithActionPreview() {
    KinoTheme {
        Surface {
            EmptyState(
                icon = Icons.Default.TaskAlt,
                title = "No tasks yet",
                message = "Create your first task to get started with organizing your work",
                actionLabel = "Create Task",
                onActionClick = {}
            )
        }
    }
}

/**
 * Preview without action button
 */
@ThemePreviews
@Composable
private fun EmptyStateWithoutActionPreview() {
    KinoTheme {
        Surface {
            EmptyState(
                icon = Icons.Default.Search,
                title = "No results found",
                message = "Try adjusting your search or filters",
                actionLabel = null,
                onActionClick = null
            )
        }
    }
}

/**
 * Preview with different icons and states
 */
@Preview(name = "Empty States - All Types", showBackground = true)
@Composable
private fun AllEmptyStatesPreview() {
    KinoTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                EmptyState(
                    icon = Icons.Default.TaskAlt,
                    title = "No tasks",
                    message = "Create your first task",
                    actionLabel = "Create Task",
                    onActionClick = {}
                )
                EmptyState(
                    icon = Icons.AutoMirrored.Filled.Note,
                    title = "No notes",
                    message = "Start taking notes",
                    actionLabel = "Add Note",
                    onActionClick = {}
                )
                EmptyState(
                    icon = Icons.Default.Search,
                    title = "No results",
                    message = "Try different search terms",
                    actionLabel = null,
                    onActionClick = null
                )
            }
        }
    }
}
