package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.preview.ComponentPreviews
import com.letsgotoperfection.kino.core.designsystem.preview.PreviewData
import com.letsgotoperfection.kino.core.designsystem.preview.PriorityPreviewProvider
import com.letsgotoperfection.kino.core.designsystem.preview.ThemePreviews
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.model.Priority

// Priority data class is defined in TaskCard.kt

@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(android.graphics.Color.parseColor(priority.colorHex)).copy(alpha = 0.2f),
        modifier = modifier
    ) {
        Text(
            text = priority.displayName.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color(android.graphics.Color.parseColor(priority.colorHex)),
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Preview with different priority levels
 */
@ThemePreviews
@Composable
private fun PriorityBadgePreview(
    @PreviewParameter(PriorityPreviewProvider::class) priority: Priority
) {
    KinoTheme {
        Surface {
            PriorityBadge(
                priority = priority,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Preview showing all priority levels
 */
@Preview(name = "All Priorities", showBackground = true)
@Composable
private fun AllPriorityBadgesPreview() {
    KinoTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.values().forEach { priority ->
                    PriorityBadge(priority = priority)
                }
            }
        }
    }
}
