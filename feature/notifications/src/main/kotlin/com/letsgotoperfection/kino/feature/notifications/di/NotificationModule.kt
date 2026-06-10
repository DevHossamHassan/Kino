package com.letsgotoperfection.kino.feature.notifications.di

import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelManager
import com.letsgotoperfection.kino.feature.notifications.api.NotificationChannelSettings
import com.letsgotoperfection.kino.feature.notifications.api.NotificationInitializer
import com.letsgotoperfection.kino.feature.notifications.api.UltraSimpleNotificationApi
import com.letsgotoperfection.kino.feature.notifications.internal.api.NotificationApiImpl
import com.letsgotoperfection.kino.feature.notifications.internal.api.UltraSimpleNotificationApiImpl
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationChannelManagerImpl
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationChannelSettingsImpl
import com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationInitializerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for notification dependencies.
 * This module provides clean, simple notification services.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    
    // Main API - what users actually need
    @Binds
    @Singleton
    abstract fun bindUltraSimpleNotificationApi(
        impl: UltraSimpleNotificationApiImpl
    ): UltraSimpleNotificationApi

    // Internal APIs (for advanced use cases)
    @Binds
    @Singleton
    abstract fun bindNotificationApi(
        impl: NotificationApiImpl
    ): NotificationApi
    
    @Binds
    @Singleton
    abstract fun bindNotificationChannelManager(
        impl: NotificationChannelManagerImpl
    ): NotificationChannelManager
    
    @Binds
    @Singleton
    abstract fun bindNotificationChannelSettings(
        impl: NotificationChannelSettingsImpl
    ): NotificationChannelSettings
    
    @Binds
    @Singleton
    abstract fun bindNotificationInitializer(
        impl: NotificationInitializerImpl
    ): NotificationInitializer
}
