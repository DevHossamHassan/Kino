package com.letsgotoperfection.kino.feature.notifications.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import com.letsgotoperfection.kino.feature.notifications.internal.api.NotificationApiImpl
import com.letsgotoperfection.kino.feature.notifications.internal.data.local.NotificationDao
import com.letsgotoperfection.kino.feature.notifications.internal.data.local.NotificationDatabase
import com.letsgotoperfection.kino.feature.notifications.internal.data.repository.NotificationRepository
import com.letsgotoperfection.kino.feature.notifications.internal.data.repository.NotificationRepositoryImpl
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationBuilderFactory
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationChannelManager
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationPermissionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    
    @Binds
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository
    
    @Binds
    abstract fun bindNotificationApi(
        impl: NotificationApiImpl
    ): NotificationApi
    
    companion object {
        
        @Provides
        @Singleton
        fun provideNotificationManager(
            @ApplicationContext context: Context
        ): NotificationManager {
            return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        
        @Provides
        @Singleton
        fun provideNotificationManagerCompat(
            @ApplicationContext context: Context
        ): NotificationManagerCompat {
            return NotificationManagerCompat.from(context)
        }
        
        @Provides
        @Singleton
        fun provideNotificationChannelManager(
            @ApplicationContext context: Context,
            notificationManager: NotificationManager
        ): NotificationChannelManager {
            return NotificationChannelManager(context, notificationManager)
        }
        
        @Provides
        @Singleton
        fun provideNotificationPermissionManager(
            @ApplicationContext context: Context
        ): NotificationPermissionManager {
            return NotificationPermissionManager(context)
        }
        
        @Provides
        @Singleton
        fun provideNotificationBuilderFactory(
            @ApplicationContext context: Context
        ): NotificationBuilderFactory {
            return NotificationBuilderFactory(context)
        }
        
        @Provides
        @Singleton
        fun provideNotificationDatabase(
            @ApplicationContext context: Context
        ): NotificationDatabase {
            return NotificationDatabase.create(context)
        }
        
        @Provides
        fun provideNotificationDao(
            database: NotificationDatabase
        ): NotificationDao {
            return database.notificationDao()
        }
    }
}
