package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "sections")
data class SectionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val orderIndex: Int
)



