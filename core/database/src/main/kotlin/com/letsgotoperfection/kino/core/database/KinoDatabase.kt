package com.letsgotoperfection.kino.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.letsgotoperfection.kino.core.database.converters.Converters
import com.letsgotoperfection.kino.core.database.dao.*
import com.letsgotoperfection.kino.core.database.entity.*

/**
 * Kino Database with performance optimizations.
 *
 * Version 7 improvements:
 * - Added media table for the media manager feature
 *
 * Version 6 improvements:
 * - Added defaultColumn, checklistTemplate, and dueDateOffsetDays fields to RecurringTaskEntity
 * - Enhanced recurring task functionality with better task generation control
 * 
 * Version 5 improvements:
 * - Added orderPosition field for drag-to-reorder functionality
 * - Added index on (column, orderPosition) for efficient ordering queries
 * 
 * Version 4 improvements:
 * - Added indices on all frequently queried columns
 * - Added foreign key constraints with CASCADE delete
 * - Optimized entity definitions for better query performance
 */
@Database(
    entities = [
        TaskEntity::class,
        ChecklistItemEntity::class,
        LabelEntity::class,
        TaskLabelCrossRef::class,
        NoteLabelCrossRef::class,
        AttachmentEntity::class,
        NoteEntity::class,
        RecurringTaskEntity::class,
        SectionEntity::class,
        MediaEntity::class
    ],
    version = 7,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class KinoDatabase : RoomDatabase() {
    
    abstract fun taskDao(): TaskDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun labelDao(): LabelDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun noteDao(): NoteDao
    abstract fun recurringTaskDao(): RecurringTaskDao
    abstract fun sectionDao(): SectionDao
    abstract fun mediaDao(): MediaDao
}
