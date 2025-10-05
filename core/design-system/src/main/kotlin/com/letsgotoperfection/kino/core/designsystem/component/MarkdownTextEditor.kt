package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * Rich markdown-aware editor with an inline formatting toolbar that supports
 * bold/italic/underline, strikethrough, inline and block code, quotes, bulleted,
 * numbered and checklist lists. An optional attachment button can be surfaced
 * to integrate media pickers from the hosting screen.
 */
@Composable
fun MarkdownTextEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    minLines: Int = 8,
    onAddAttachment: (() -> Unit)? = null
) {
    val editorState = remember(value) { MarkdownEditorState.from(value) }

    Column(modifier = modifier) {
        MarkdownToolbar(
            state = editorState,
            onBoldClick = { onValueChange(toggleBold(value)) },
            onItalicClick = { onValueChange(toggleItalic(value)) },
            onUnderlineClick = { onValueChange(toggleUnderline(value)) },
            onStrikethroughClick = { onValueChange(toggleStrikethrough(value)) },
            onInlineCodeClick = { onValueChange(toggleInlineCode(value)) },
            onQuoteClick = { onValueChange(toggleQuote(value)) },
            onBulletClick = { onValueChange(toggleBullet(value)) },
            onNumberedClick = { onValueChange(toggleNumbered(value)) },
            onChecklistClick = { onValueChange(toggleChecklist(value)) },
            onCodeBlockClick = { onValueChange(toggleCodeBlock(value)) },
            onAddAttachment = onAddAttachment
        )

        Divider()

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            minLines = minLines
        )
    }
}

@Composable
private fun MarkdownToolbar(
    state: MarkdownEditorState,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit,
    onStrikethroughClick: () -> Unit,
    onInlineCodeClick: () -> Unit,
    onQuoteClick: () -> Unit,
    onBulletClick: () -> Unit,
    onNumberedClick: () -> Unit,
    onChecklistClick: () -> Unit,
    onCodeBlockClick: () -> Unit,
    onAddAttachment: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MarkdownToolbarButton(
            selected = state.isBold,
            onClick = onBoldClick,
            icon = Icons.Default.FormatBold,
            contentDescription = "Bold"
        )
        MarkdownToolbarButton(
            selected = state.isItalic,
            onClick = onItalicClick,
            icon = Icons.Default.FormatItalic,
            contentDescription = "Italic"
        )
        MarkdownToolbarButton(
            selected = state.isUnderline,
            onClick = onUnderlineClick,
            icon = Icons.Default.FormatUnderlined,
            contentDescription = "Underline"
        )
        MarkdownToolbarButton(
            selected = state.isStrikethrough,
            onClick = onStrikethroughClick,
            icon = Icons.Default.FormatStrikethrough,
            contentDescription = "Strikethrough"
        )
        MarkdownToolbarButton(
            selected = state.isInlineCode,
            onClick = onInlineCodeClick,
            icon = Icons.Default.Code,
            contentDescription = "Inline code"
        )

        Spacer(modifier = Modifier.width(8.dp))

        MarkdownToolbarButton(
            selected = state.isQuote,
            onClick = onQuoteClick,
            icon = Icons.Default.FormatQuote,
            contentDescription = "Quote"
        )
        MarkdownToolbarButton(
            selected = state.isBullet,
            onClick = onBulletClick,
            icon = Icons.Default.FormatListBulleted,
            contentDescription = "Bullet list"
        )
        MarkdownToolbarButton(
            selected = state.isNumbered,
            onClick = onNumberedClick,
            icon = Icons.Default.FormatListNumbered,
            contentDescription = "Numbered list"
        )
        MarkdownToolbarButton(
            selected = state.isChecklist,
            onClick = onChecklistClick,
            icon = Icons.Default.CheckBox,
            contentDescription = "Checklist"
        )
        MarkdownToolbarButton(
            selected = state.isCodeBlock,
            onClick = onCodeBlockClick,
            icon = Icons.Default.Code,
            contentDescription = "Code block"
        )

        if (onAddAttachment != null) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onAddAttachment) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Add attachment"
                )
            }
        }
    }
}

@Composable
private fun MarkdownToolbarButton(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

private data class MarkdownEditorState(
    val isBold: Boolean,
    val isItalic: Boolean,
    val isUnderline: Boolean,
    val isStrikethrough: Boolean,
    val isInlineCode: Boolean,
    val isQuote: Boolean,
    val isBullet: Boolean,
    val isNumbered: Boolean,
    val isChecklist: Boolean,
    val isCodeBlock: Boolean
) {
    companion object {
        fun from(value: TextFieldValue): MarkdownEditorState {
            return MarkdownEditorState(
                isBold = isSelectionWrapped(value, "**"),
                isItalic = isSelectionWrapped(value, "_"),
                isUnderline = isSelectionWrapped(value, "<u>", "</u>"),
                isStrikethrough = isSelectionWrapped(value, "~~"),
                isInlineCode = isSelectionWrapped(value, "`"),
                isQuote = isSelectionLinePrefixed(value, QUOTE_PREFIX),
                isBullet = isSelectionLinePrefixed(value, BULLET_PREFIX),
                isNumbered = isSelectionLinePrefixed(value, NUMBERED_PREFIX),
                isChecklist = isSelectionLinePrefixed(value, CHECKLIST_PREFIX),
                isCodeBlock = isSelectionWrapped(value, CODE_BLOCK_PREFIX, CODE_BLOCK_SUFFIX)
            )
        }
    }
}

private const val BULLET_PREFIX = "- "
private const val NUMBERED_PREFIX = "1. "
private const val CHECKLIST_PREFIX = "- [ ] "
private const val QUOTE_PREFIX = "> "
private const val CODE_BLOCK_PREFIX = "```\n"
private const val CODE_BLOCK_SUFFIX = "\n```"

private fun toggleBold(value: TextFieldValue): TextFieldValue = toggleWrappedText(value, "**")
private fun toggleItalic(value: TextFieldValue): TextFieldValue = toggleWrappedText(value, "_")
private fun toggleUnderline(value: TextFieldValue): TextFieldValue = toggleWrappedText(value, "<u>", "</u>")
private fun toggleStrikethrough(value: TextFieldValue): TextFieldValue = toggleWrappedText(value, "~~")
private fun toggleInlineCode(value: TextFieldValue): TextFieldValue = toggleWrappedText(value, "`")
private fun toggleQuote(value: TextFieldValue): TextFieldValue = toggleLinePrefix(value, QUOTE_PREFIX)
private fun toggleBullet(value: TextFieldValue): TextFieldValue = toggleLinePrefix(value, BULLET_PREFIX)
private fun toggleNumbered(value: TextFieldValue): TextFieldValue = toggleLinePrefix(value, NUMBERED_PREFIX)
private fun toggleChecklist(value: TextFieldValue): TextFieldValue = toggleLinePrefix(value, CHECKLIST_PREFIX)
private fun toggleCodeBlock(value: TextFieldValue): TextFieldValue = toggleWrappedText(value, CODE_BLOCK_PREFIX, CODE_BLOCK_SUFFIX)

private fun toggleWrappedText(value: TextFieldValue, prefix: String, suffix: String = prefix): TextFieldValue {
    val text = value.text
    val (start, end) = value.normalizedSelection()

    if (start == end) {
        val insertion = prefix + suffix
        val newText = text.insertAt(start, insertion)
        val cursor = start + prefix.length
        return TextFieldValue(newText, TextRange(cursor, cursor))
    }

    val hasWrapper = start >= prefix.length && end + suffix.length <= text.length &&
        text.substring(start - prefix.length, start) == prefix &&
        text.substring(end, end + suffix.length) == suffix

    return if (hasWrapper) {
        val withoutSuffix = text.removeRange(end, end + suffix.length)
        val withoutPrefix = withoutSuffix.removeRange(start - prefix.length, start)
        TextFieldValue(
            text = withoutPrefix,
            selection = TextRange(start - prefix.length, end - prefix.length)
        )
    } else {
        val wrapped = text.insertAt(end, suffix).insertAt(start, prefix)
        TextFieldValue(
            text = wrapped,
            selection = TextRange(start + prefix.length, end + prefix.length)
        )
    }
}

private fun toggleLinePrefix(value: TextFieldValue, prefix: String): TextFieldValue {
    val text = value.text
    val (start, end) = value.normalizedSelection()

    val lineStart = text.lineStart(start)
    val lineEnd = text.lineEnd(end)
    val block = text.substring(lineStart, lineEnd)
    val lines = block.split('\n')

    val allPrefixed = lines.all { it.startsWith(prefix) }
    val updatedLines = if (allPrefixed) {
        lines.map { it.removePrefix(prefix) }
    } else {
        lines.map { line -> if (line.startsWith(prefix)) line else prefix + line }
    }
    val updatedBlock = updatedLines.joinToString("\n")
    val newText = text.replaceRange(lineStart, lineEnd, updatedBlock)

    val newSelection = TextRange(lineStart, lineStart + updatedBlock.length)
    return TextFieldValue(newText, newSelection)
}

private fun isSelectionWrapped(value: TextFieldValue, prefix: String, suffix: String = prefix): Boolean {
    val text = value.text
    val (start, end) = value.normalizedSelection()
    if (start == end) return false
    if (start < prefix.length || end + suffix.length > text.length) return false
    return text.substring(start - prefix.length, start) == prefix &&
        text.substring(end, end + suffix.length) == suffix
}

private fun isSelectionLinePrefixed(value: TextFieldValue, prefix: String): Boolean {
    val text = value.text
    if (text.isEmpty()) return false
    val (start, end) = value.normalizedSelection()
    val lineStart = text.lineStart(start)
    val lineEnd = text.lineEnd(end)
    if (lineStart >= lineEnd) return false
    val block = text.substring(lineStart, lineEnd)
    val lines = block.split('\n')
    return lines.isNotEmpty() && lines.all { it.startsWith(prefix) }
}

private fun String.insertAt(index: Int, value: String): String {
    return this.substring(0, index) + value + this.substring(index)
}

private fun String.lineStart(index: Int): Int {
    if (isEmpty()) return 0
    val safeIndex = index.coerceIn(0, length)
    val precedingBreak = lastIndexOf('\n', startIndex = safeIndex - 1)
    return if (precedingBreak == -1) 0 else precedingBreak + 1
}

private fun String.lineEnd(index: Int): Int {
    if (isEmpty()) return 0
    val safeIndex = index.coerceIn(0, length)
    val nextBreak = indexOf('\n', startIndex = safeIndex)
    return if (nextBreak == -1) length else nextBreak
}

private fun TextFieldValue.normalizedSelection(): Pair<Int, Int> {
    val start = selection.start.coerceIn(0, text.length)
    val end = selection.end.coerceIn(0, text.length)
    return min(start, end) to max(start, end)
}
