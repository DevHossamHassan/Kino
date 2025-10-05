package com.letsgotoperfection.kino.feature.notifications.internal.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.letsgotoperfection.kino.feature.notifications.internal.data.local.NotificationConverters

@Database(
    entities = [NotificationEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(NotificationConverters::class)
internal abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    
    companion object {
        fun create(context: Context): NotificationDatabase {
            return Room.databaseBuilder(
                context,
                NotificationDatabase::class.java,
                "notification_database"
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}
