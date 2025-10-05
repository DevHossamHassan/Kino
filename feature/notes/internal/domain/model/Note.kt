package com.letsgotoperfection.kino.feature.notes.internal.domain.model

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.model.Label
import java.time.LocalDateTime

/**
 * Domain model representing a note with rich text content.
 * This is used for the notes feature functionality.
 */
internal data class Note(
    val id: String,
    val title: String,
    val content: AnnotatedString,  // Rich text content
    val isPinned: Boolean,
    val labels: List<Label>,
    val attachmentCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    val hasAttachments: Boolean get() = attachmentCount > 0
    val isRecentlyUpdated: Boolean get() = 
        updatedAt.isAfter(LocalDateTime.now().minusDays(1))
    val previewText: String get() = content.text.take(100) + if (content.text.length > 100) "..." else ""
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
