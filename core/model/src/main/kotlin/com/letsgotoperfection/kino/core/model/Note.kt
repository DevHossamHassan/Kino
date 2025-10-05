package com.letsgotoperfection.kino.core.model

import java.time.LocalDateTime
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val labels: List<Label> = emptyList(),
    val attachments: List<Attachment> = emptyList()
)

data class MediaFile(
    val id: String,
    val uri: String,
    val filename: String,
    val mimeType: String,
    val size: Long,
    val addedAt: LocalDateTime,
    val targetId: String? = null,
    val targetType: String? = null
)