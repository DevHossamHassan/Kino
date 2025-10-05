package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.preview.ComponentPreviews
import com.letsgotoperfection.kino.core.designsystem.preview.ThemePreviews
import com.letsgotoperfection.kino.core.designsystem.KinoTheme

@Composable
fun SectionHeader(
    title: String,
    count: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isExpanded) 
                        Icons.Default.ExpandMore 
                    else 
                        Icons.Default.ChevronRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "($count)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Preview with expanded state
 */
@ThemePreviews
@Composable
private fun SectionHeaderExpandedPreview() {
    KinoTheme {
        Surface {
            SectionHeader(
                title = "Work",
                count = 12,
                isExpanded = true,
                onToggle = {}
            )
        }
    }
}

/**
 * Preview with collapsed state
 */
@ThemePreviews
@Composable
private fun SectionHeaderCollapsedPreview() {
    KinoTheme {
        Surface {
            SectionHeader(
                title = "Personal",
                count = 5,
                isExpanded = false,
                onToggle = {}
            )
        }
    }
}

/**
 * Preview showing all section header states
 */
@Preview(name = "Section Headers - All States", showBackground = true)
@Composable
private fun SectionHeadersPreview() {
    KinoTheme {
        Surface {
            Column {
                SectionHeader(
                    title = "Work",
                    count = 12,
                    isExpanded = true,
                    onToggle = {}
                )
                SectionHeader(
                    title = "Personal",
                    count = 5,
                    isExpanded = false,
                    onToggle = {}
                )
                SectionHeader(
                    title = "Family",
                    count = 0,
                    isExpanded = true,
                    onToggle = {}
                )
            }
        }
    }
}
