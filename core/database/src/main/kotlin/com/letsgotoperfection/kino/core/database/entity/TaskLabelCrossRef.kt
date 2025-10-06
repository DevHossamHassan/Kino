package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross-reference table for Task-Label many-to-many relationship.
 * 
 * PERFORMANCE IMPROVEMENTS:
 * - Index on taskId for fast label lookup by task
 * - Index on labelId for fast task lookup by label
 * - Foreign keys ensure referential integrity with CASCADE delete
 */
@Entity(
    tableName = "task_labels",
    primaryKeys = ["taskId", "labelId"],
    indices = [
        Index(value = ["taskId"], name = "idx_task_label_task_id"),
        Index(value = ["labelId"], name = "idx_task_label_label_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
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
data class TaskLabelCrossRef(
    val taskId: String,
    val labelId: String
)






