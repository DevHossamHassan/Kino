package com.letsgotoperfection.kino.feature.notes.internal.domain.model

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.model.Label
import java.time.LocalDateTime

/**
 * Domain model representing a note with rich text content.
 */
internal data class Note(
    val id: String,
    val title: String,
    val content: AnnotatedString,
    val isPinned: Boolean,
    val labels: List<Label>,
    val attachmentCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    val hasAttachments: Boolean get() = attachmentCount > 0
    val previewText: String
        get() = content.text.take(PREVIEW_LENGTH) +
            if (content.text.length > PREVIEW_LENGTH) "…" else ""

    private companion object {
        const val PREVIEW_LENGTH = 100
    }
}

/**
 * Note filter options for the notes list.
 */
internal enum class NoteFilter {
    ALL,
    PINNED,
    RECENT,
    WITH_LABELS
}

/**
 * Note sort options for the notes list.
 */
internal enum class NoteSort {
    TITLE_ASC,
    TITLE_DESC,
    CREATED_ASC,
    CREATED_DESC,
    UPDATED_ASC,
    UPDATED_DESC
}
