package com.letsgotoperfection.kino.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.letsgotoperfection.kino.core.database.KinoDatabase
import com.letsgotoperfection.kino.core.database.KinoDatabaseMigrations
import com.letsgotoperfection.kino.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database module.
 *
 * - Lazy database initialization (Hilt Singleton = lazy by default)
 * - Unscoped DAO providers (lightweight proxies)
 * - Real migrations via [KinoDatabaseMigrations]; no destructive fallback
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides the Room database instance.
     * 
     * PERFORMANCE: Singleton ensures single instance, but initialization is lazy.
     * Database is only created when first DAO is injected.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): KinoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            KinoDatabase::class.java,
            "kino_database"
        )
            .addMigrations(*KinoDatabaseMigrations.ALL)
            // PERFORMANCE: Enable query optimization
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .build()
    }
    
    /**
     * DAOs are not scoped to allow Room to optimize their creation.
     * They are lightweight proxies, not heavyweight objects.
     */
    @Provides
    fun provideTaskDao(database: KinoDatabase): TaskDao = database.taskDao()
    
    @Provides
    fun provideChecklistDao(database: KinoDatabase): ChecklistDao = database.checklistDao()
    
    @Provides
    fun provideLabelDao(database: KinoDatabase): LabelDao = database.labelDao()
    
    @Provides
    fun provideAttachmentDao(database: KinoDatabase): AttachmentDao = database.attachmentDao()
    
    @Provides
    fun provideNoteDao(database: KinoDatabase): NoteDao = database.noteDao()
    
    @Provides
    fun provideRecurringTaskDao(database: KinoDatabase): RecurringTaskDao = database.recurringTaskDao()

    @Provides
    fun provideSectionDao(database: KinoDatabase): SectionDao = database.sectionDao()

    @Provides
    fun provideMediaDao(database: KinoDatabase): MediaDao = database.mediaDao()
}
