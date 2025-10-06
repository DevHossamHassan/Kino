package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity

@Entity(
    tableName = "task_labels",
    primaryKeys = ["taskId", "labelId"]
)
data class TaskLabelCrossRef(
    val taskId: String,
    val labelId: String
)






