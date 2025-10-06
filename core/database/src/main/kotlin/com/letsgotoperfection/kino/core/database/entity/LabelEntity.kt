package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "labels")
data class LabelEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: String  // Hex color
)






