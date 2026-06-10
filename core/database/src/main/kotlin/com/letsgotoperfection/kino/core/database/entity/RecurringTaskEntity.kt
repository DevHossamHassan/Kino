package com.letsgotoperfection.kino.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "recurring_tasks")
data class RecurringTaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val section: String,  // personal, work, family
    val priority: String, // high, medium, low
    val frequency: String, // daily, weekly, monthly, yearly
    val interval: Int,  // Every X days/weeks/months/years
    val daysOfWeek: String,  // JSON array of day numbers (1-7, Monday=1)
    val dayOfMonth: Int?,  // For monthly/yearly (1-31)
    val monthOfYear: Int?,  // For yearly (1-12)
    val timeOfDay: String,  // ISO LocalTime string (HH:mm:ss)
    val startDate: Long,  // Epoch day
    val endDate: Long?,  // Epoch day, null = never ends
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val lastGeneratedDate: Long?,  // Epoch day, last date an instance was generated
    val defaultColumn: String = "todo_this_week",  // Default column for generated tasks
    val checklistTemplate: String = "[]",  // JSON array of checklist template items
    val dueDateOffsetDays: Int = 0  // Days to add to creation date for due date
)

