package com.letsgotoperfection.kino.core.designsystem.preview

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.component.*
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.model.*
import com.letsgotoperfection.kino.core.designsystem.preview.ThemePreviews
import com.letsgotoperfection.kino.core.designsystem.preview.FontScalePreviews
import com.letsgotoperfection.kino.core.designsystem.preview.ComponentPreviews

/**
 * COMPREHENSIVE PREVIEW EXAMPLES
 * 
 * This file demonstrates how to use the preview system effectively
 * for different types of components and screens.
 */

// ============================================================================
// 1. BASIC COMPONENT PREVIEWS
// ============================================================================

/**
 * Example: Simple component with theme variations
 */
@ThemePreviews
@Composable
private fun BasicComponentPreview() {
    KinoTheme {
        Surface {
            Card(
                modifier = Modifier.padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Basic Component",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "This is a simple component preview",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = {}) {
                        Text("Action")
                    }
                }
            }
        }
    }
}

/**
 * Example: Parameterized preview with different data
 */
@Preview(
    name = "Parameterized Component",
    showBackground = true
)
@Composable
private fun ParameterizedComponentPreview(
    @PreviewParameter(TaskPreviewProvider::class) task: Task
) {
    KinoTheme {
        Surface {
            TaskCard(
                task = task,
                onTaskClick = {}
            )
        }
    }
}

/**
 * Example: Long text handling
 */
@Preview(
    name = "Long Text",
    showBackground = true
)
@Composable
private fun LongTextPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {}
            )
        }
    }
}

/**
 * Example: Empty state component
 */
@Preview(
    name = "Empty State",
    showBackground = true
)
@Composable
private fun EmptyStatePreview() {
    KinoTheme {
        Surface {
            EmptyState(
                title = "No notes",
                description = "Start taking notes",
                icon = Icons.AutoMirrored.Filled.Note,
                primaryAction = EmptyStateAction(
                    text = "Add Note",
                    onClick = {}
                )
            )
        }
    }
}

/**
 * Example: Font scale variations
 */
@FontScalePreviews
@Composable
private fun FontScalePreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {}
            )
        }
    }
}

/**
 * Example: RTL support
 */
@Preview(
    name = "RTL Support",
    locale = "ar",
    showBackground = true
)
@Composable
private fun RTLPreview() {
    KinoTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                Text("RTL Text")
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
            }
        }
    }
}

/**
 * Example: Different device sizes
 */
@Preview(
    name = "Phone Layout",
    device = "spec:width=411dp,height=891dp,dpi=420",
    showBackground = true
)
@Composable
private fun PhoneLayoutPreview() {
    KinoTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Phone Layout", style = MaterialTheme.typography.headlineSmall)
                Text("Optimized for phone screens", style = MaterialTheme.typography.bodyMedium)
                TaskCard(
                    task = PreviewData.sampleTaskHigh,
                    onTaskClick = {}
                )
            }
        }
    }
}

@Preview(
    name = "Tablet Layout",
    device = "spec:width=600dp,height=1024dp,dpi=240",
    showBackground = true
)
@Composable
private fun TabletLayoutPreview() {
    KinoTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Left Panel", style = MaterialTheme.typography.headlineSmall)
                    TaskCard(
                        task = PreviewData.sampleTaskHigh,
                        onTaskClick = {}
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Right Panel", style = MaterialTheme.typography.headlineSmall)
                    TaskCard(
                        task = PreviewData.sampleTaskMedium,
                        onTaskClick = {}
                    )
                }
            }
        }
    }
}

/**
 * Example: Interactive preview
 */
@Preview(
    name = "Interactive Preview",
    showBackground = true
)
@Composable
private fun InteractivePreview() {
    var count by remember { mutableStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }
    
    KinoTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Interactive Preview",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text("Count: $count")
                Button(onClick = { count++ }) {
                    Text("Increment")
                }
                Button(onClick = { isExpanded = !isExpanded }) {
                    Text(if (isExpanded) "Collapse" else "Expand")
                }
                if (isExpanded) {
                    TaskCard(
                        task = PreviewData.sampleTaskHigh,
                        onTaskClick = {}
                    )
                }
            }
        }
    }
}

/**
 * Example: Screen previews
 */
@Preview(
    name = "Screen - Loading",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ScreenLoadingPreview() {
    KinoTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading...")
                }
            }
        }
    }
}

@Preview(
    name = "Screen - Error",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ScreenErrorPreview() {
    KinoTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Error",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Please try again later",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {}) {
                    Text("Retry")
                }
            }
        }
    }
}

@Preview(
    name = "Screen - Success",
    showBackground = true,
    showSystemUi = true
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenSuccessPreview() {
    KinoTheme {
        Surface {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Success Screen") },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {}) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Success Screen",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        Text(
                            text = "This screen shows a successful state with data loaded",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Sample Data",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("• Work: 12 tasks")
                                Text("• Personal: 5 tasks")
                                Text("• Family: 3 tasks")
                            }
                        }
                    }
                    items(3) { index ->
                        TaskCard(
                            task = when (index) {
                                0 -> PreviewData.sampleTaskHigh
                                1 -> PreviewData.sampleTaskMedium
                                else -> PreviewData.sampleTaskLow
                            },
                            onTaskClick = {}
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 8. PREVIEW GROUPS
// ============================================================================

/**
 * Example: Grouped previews for different states
 */
@Preview(name = "State - Loading", group = "States")
@Composable
private fun StateLoadingPreview() {
    KinoTheme {
        Surface {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(name = "State - Success", group = "States")
@Composable
private fun StateSuccessPreview() {
    KinoTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Success!", style = MaterialTheme.typography.titleMedium)
                Text("Data loaded successfully", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(name = "State - Error", group = "States")
@Composable
private fun StateErrorPreview() {
    KinoTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Error", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                Text("Something went wrong", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ============================================================================
// 9. CUSTOM PREVIEWS
// ============================================================================

/**
 * Example: Custom previews with specific configurations
 */
@Preview(
    name = "Custom - High Priority",
    showBackground = true,
    backgroundColor = 0xFFE8F5E8
)
@Preview(
    name = "Custom - Medium Priority",
    showBackground = true,
    backgroundColor = 0xFFFFF8E1
)
@Preview(
    name = "Custom - Low Priority",
    showBackground = true,
    backgroundColor = 0xFFF3E5F5
)
@Composable
private fun PriorityCustomPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {}
            )
        }
    }
}

/**
 * Example: Performance testing with large lists
 */
@Preview(
    name = "Performance - Large List",
    showBackground = true
)
@Composable
private fun PerformanceLargeListPreview() {
    KinoTheme {
        Surface {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(20) { index ->
                    TaskCard(
                        task = when (index % 3) {
                            0 -> PreviewData.sampleTaskHigh
                            1 -> PreviewData.sampleTaskMedium
                            else -> PreviewData.sampleTaskLow
                        },
                        onTaskClick = {}
                    )
                }
            }
        }
    }
}

// ============================================================================
// 10. COMPONENT-SPECIFIC PREVIEWS
// ============================================================================

/**
 * Example: Priority badge variations
 */
@Preview(name = "Priority Badge - High", group = "Priority")
@Composable
private fun PriorityBadgeHighPreview() {
    KinoTheme {
        Surface {
            PriorityBadge(priority = Priority.HIGH)
        }
    }
}

@Preview(name = "Priority Badge - Medium", group = "Priority")
@Composable
private fun PriorityBadgeMediumPreview() {
    KinoTheme {
        Surface {
            PriorityBadge(priority = Priority.MEDIUM)
        }
    }
}

@Preview(name = "Priority Badge - Low", group = "Priority")
@Composable
private fun PriorityBadgeLowPreview() {
    KinoTheme {
        Surface {
            PriorityBadge(priority = Priority.LOW)
        }
    }
}

/**
 * Example: Label chip variations
 */
@Preview(name = "Label Chip - Single", group = "Labels")
@Composable
private fun LabelChipSinglePreview() {
    KinoTheme {
        Surface {
            LabelChip(
                label = Label("1", "Design", "#FF6B6B"),
                onRemove = {}
            )
        }
    }
}

@Preview(name = "Label Chip - Multiple", group = "Labels")
@Composable
private fun LabelChipMultiplePreview() {
    KinoTheme {
        Surface {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LabelChip(
                    label = Label("1", "Design", "#FF6B6B"),
                    onRemove = {}
                )
                LabelChip(
                    label = Label("2", "Urgent", "#FFD93D"),
                    onRemove = {}
                )
                LabelChip(
                    label = Label("3", "Marketing", "#4ECDC4"),
                    onRemove = {}
                )
            }
        }
    }
}

/**
 * Example: Section header variations
 */
@Preview(name = "Section Header - Expanded", group = "Headers")
@Composable
private fun SectionHeaderExpandedPreview() {
    KinoTheme {
        Surface {
            SectionHeader(
                title = "Work Tasks",
                count = 12,
                isExpanded = true,
                onToggle = {}
            )
        }
    }
}

@Preview(name = "Section Header - Collapsed", group = "Headers")
@Composable
private fun SectionHeaderCollapsedPreview() {
    KinoTheme {
        Surface {
            SectionHeader(
                title = "Personal Tasks",
                count = 5,
                isExpanded = false,
                onToggle = {}
            )
        }
    }
}

// ============================================================================
// 11. EDGE CASE PREVIEWS
// ============================================================================

/**
 * Example: Edge cases and error states
 */
@Preview(name = "Edge Case - High Priority", group = "Edge Cases")
@Composable
private fun EdgeCaseHighPriorityPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {}
            )
        }
    }
}

@Preview(name = "Edge Case - Medium Priority", group = "Edge Cases")
@Composable
private fun EdgeCaseMediumPriorityPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskMedium,
                onTaskClick = {}
            )
        }
    }
}

@Preview(name = "Edge Case - Low Priority", group = "Edge Cases")
@Composable
private fun EdgeCaseLowPriorityPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskLow,
                onTaskClick = {}
            )
        }
    }
}

// ============================================================================
// 12. ACCESSIBILITY PREVIEWS
// ============================================================================

/**
 * Example: Accessibility testing
 */
@Preview(name = "Accessibility - High Contrast", group = "Accessibility")
@Composable
private fun AccessibilityHighContrastPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {}
            )
        }
    }
}

@Preview(name = "Accessibility - Large Text", group = "Accessibility")
@Composable
private fun AccessibilityLargeTextPreview() {
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {}
            )
        }
    }
}

// ============================================================================
// 13. THEME VARIATIONS
// ============================================================================

/**
 * Example: Different theme configurations
 */
@Preview(name = "Theme - Light", group = "Themes")
@Composable
private fun ThemeLightPreview() {
    KinoTheme(darkTheme = false) {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {}
            )
        }
    }
}

@Preview(name = "Theme - Dark", group = "Themes")
@Composable
private fun ThemeDarkPreview() {
    KinoTheme(darkTheme = true) {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = {}
            )
        }
    }
}

// ============================================================================
// 14. INTERACTIVE PREVIEWS
// ============================================================================

/**
 * Example: Interactive components
 */
@Preview(
    name = "Interactive - Task Card",
    showBackground = true
)
@Composable
private fun InteractiveTaskCardPreview() {
    var isSelected by remember { mutableStateOf(false) }
    
    KinoTheme {
        Surface {
            TaskCard(
                task = PreviewData.sampleTaskHigh,
                onTaskClick = { isSelected = !isSelected }
            )
        }
    }
}

@Preview(
    name = "Interactive - Section Header",
    showBackground = true
)
@Composable
private fun InteractiveSectionHeaderPreview() {
    var isExpanded by remember { mutableStateOf(true) }
    
    KinoTheme {
        Surface {
            SectionHeader(
                title = "Interactive Section",
                count = 8,
                isExpanded = isExpanded,
                onToggle = { isExpanded = !isExpanded }
            )
        }
    }
}