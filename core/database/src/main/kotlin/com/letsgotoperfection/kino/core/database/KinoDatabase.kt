package com.letsgotoperfection.kino.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.letsgotoperfection.kino.core.database.converters.Converters
import com.letsgotoperfection.kino.core.database.dao.*
import com.letsgotoperfection.kino.core.database.entity.*
// Media entities will be added when media feature is implemented

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
            SectionEntity::class
    ],
        version = 3,
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
    // Media DAO will be added when media feature is implemented
    
    companion object {
        @Volatile
        private var INSTANCE: KinoDatabase? = null
        
        fun getDatabase(context: Context): KinoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KinoDatabase::class.java,
                    "kino_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
