package com.letsgotoperfection.kino.feature.notes.internal.presentation.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.letsgotoperfection.kino.core.designsystem.component.MarkdownTextEditor

@Composable
internal fun RichTextEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    MarkdownTextEditor(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = "Start typing your note..."
    )
}
