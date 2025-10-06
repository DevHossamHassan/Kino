package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross-reference table for Note-Label many-to-many relationship.
 * 
 * PERFORMANCE IMPROVEMENTS:
 * - Index on noteId for fast label lookup by note
 * - Index on labelId for fast note lookup by label
 * - Foreign keys ensure referential integrity with CASCADE delete
 */
@Entity(
    tableName = "note_labels",
    primaryKeys = ["noteId", "labelId"],
    indices = [
        Index(value = ["noteId"], name = "idx_note_label_note_id"),
        Index(value = ["labelId"], name = "idx_note_label_label_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LabelEntity::class,
            parentColumns = ["id"],
            childColumns = ["labelId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NoteLabelCrossRef(
    val noteId: String,
    val labelId: String
)






