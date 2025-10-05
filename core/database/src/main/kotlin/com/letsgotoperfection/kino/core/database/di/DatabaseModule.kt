package com.letsgotoperfection.kino.core.database.di

import android.content.Context
import com.letsgotoperfection.kino.core.database.KinoDatabase
import com.letsgotoperfection.kino.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KinoDatabase {
        return KinoDatabase.getDatabase(context)
    }
    
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
}
