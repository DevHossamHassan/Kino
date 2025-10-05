package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.core.designsystem.preview.ComponentPreviews
import com.letsgotoperfection.kino.core.designsystem.preview.LabelsPreviewProvider
import com.letsgotoperfection.kino.core.designsystem.preview.PreviewData
import com.letsgotoperfection.kino.core.designsystem.preview.ThemePreviews
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import com.letsgotoperfection.kino.core.model.Label

// Label data class is defined in TaskCard.kt

@Composable
fun LabelChip(
    label: Label,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { onRemove?.invoke() },
        label = { Text(label.name) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(android.graphics.Color.parseColor(label.color)).copy(alpha = 0.2f),
            labelColor = Color(android.graphics.Color.parseColor(label.color))
        ),
        trailingIcon = if (onRemove != null) {
            {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove label",
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null,
        modifier = modifier
    )
}

/**
 * Preview with different label configurations
 */
@ThemePreviews
@Composable
private fun LabelChipPreview() {
    KinoTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LabelChip(
                    label = Label("1", "Design", "#FF6B6B"),
                    onRemove = null
                )
                LabelChip(
                    label = Label("2", "Urgent", "#FFD93D"),
                    onRemove = {}
                )
                LabelChip(
                    label = Label("3", "Very Long Label Name That Might Need Truncation", "#4ECDC4"),
                    onRemove = {}
                )
            }
        }
    }
}

/**
 * Preview with multiple labels
 */
@Preview(name = "Label Chips - Multiple", showBackground = true)
@Composable
private fun LabelChipsListPreview(
    @PreviewParameter(LabelsPreviewProvider::class) labels: List<Label>
) {
    KinoTheme {
        Surface {
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(labels) { label ->
                    LabelChip(label = label)
                }
            }
        }
    }
}
