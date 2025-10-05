package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity

@Entity(
    tableName = "note_labels",
    primaryKeys = ["noteId", "labelId"]
)
data class NoteLabelCrossRef(
    val noteId: String,
    val labelId: String
)





