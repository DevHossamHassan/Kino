package com.letsgotoperfection.kino.feature.notifications.di

import android.content.Context
import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import com.letsgotoperfection.kino.feature.notifications.internal.api.NotificationApiImpl
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationChannelManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Notifications feature dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationApi(
        notificationApiImpl: NotificationApiImpl
    ): NotificationApi

    companion object {
        @Provides
        @Singleton
        fun provideNotificationChannelManager(
            @ApplicationContext context: Context
        ): NotificationChannelManager {
            return NotificationChannelManager(context)
        }
    }
}




