package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Represents a Task with its associated Labels in a single query.
 * 
 * This eliminates the N+1 query problem by using Room's @Relation
 * to fetch tasks and their labels in a single database operation.
 */
data class TaskWithLabels(
    @Embedded val task: TaskEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskLabelCrossRef::class,
            parentColumn = "taskId",
            entityColumn = "labelId"
        )
    )
    val labels: List<LabelEntity>
)

