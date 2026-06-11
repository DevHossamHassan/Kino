package com.letsgotoperfection.kino.feature.recurringtasks.di

import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.alarm.RecurringTaskAlarmSchedulerImpl
import com.letsgotoperfection.kino.feature.recurringtasks.internal.api.RecurringTasksApiImpl
import com.letsgotoperfection.kino.feature.recurringtasks.internal.data.repository.RecurringTasksRepositoryImpl
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import com.letsgotoperfection.kino.feature.recurringtasks.internal.notification.RecurringTaskNotificationService
import com.letsgotoperfection.kino.feature.recurringtasks.internal.notification.RecurringTaskNotificationServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Recurring Tasks feature dependencies.
 *
 * Use cases and the calculator are constructor-injected
 * ([javax.inject.Inject]) and need no explicit providers.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RecurringTasksModule {

    @Binds
    @Singleton
    abstract fun bindRecurringTasksRepository(
        impl: RecurringTasksRepositoryImpl
    ): RecurringTasksRepository

    @Binds
    @Singleton
    abstract fun bindRecurringTasksApi(
        impl: RecurringTasksApiImpl
    ): RecurringTasksApi

    @Binds
    @Singleton
    abstract fun bindRecurringTaskAlarmScheduler(
        impl: RecurringTaskAlarmSchedulerImpl
    ): RecurringTaskAlarmScheduler

    @Binds
    @Singleton
    abstract fun bindRecurringTaskNotificationService(
        impl: RecurringTaskNotificationServiceImpl
    ): RecurringTaskNotificationService
}
