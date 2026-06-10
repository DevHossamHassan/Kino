package com.letsgotoperfection.kino.feature.recurringtasks.di

import android.content.Context
import com.letsgotoperfection.kino.core.database.dao.RecurringTaskDao
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.feature.notifications.api.UltraSimpleNotificationApi
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTasksApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.api.RecurringTasksApiImpl
import com.letsgotoperfection.kino.feature.recurringtasks.internal.data.repository.RecurringTasksRepositoryImpl
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.CreateRecurringTaskUseCase
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.GenerateInstancesUseCase
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.GetRecurringTasksUseCase
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.UpdateRecurringTaskUseCase
import com.letsgotoperfection.kino.feature.recurringtasks.internal.notification.RecurringTaskNotificationService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Recurring Tasks feature dependencies
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
    
    companion object {
        
        @Provides
        @Singleton
        fun provideRecurrenceCalculator(): RecurrenceCalculator {
            return RecurrenceCalculator()
        }
        
        @Provides
        @Singleton
        fun provideCreateRecurringTaskUseCase(
            repository: RecurringTasksRepository,
            recurrenceCalculator: RecurrenceCalculator
        ): CreateRecurringTaskUseCase {
            return CreateRecurringTaskUseCase(repository, recurrenceCalculator)
        }
        
        @Provides
        @Singleton
        fun provideUpdateRecurringTaskUseCase(
            repository: RecurringTasksRepository,
            recurrenceCalculator: RecurrenceCalculator
        ): UpdateRecurringTaskUseCase {
            return UpdateRecurringTaskUseCase(repository, recurrenceCalculator)
        }
        
        @Provides
        @Singleton
        fun provideGetRecurringTasksUseCase(
            repository: RecurringTasksRepository
        ): GetRecurringTasksUseCase {
            return GetRecurringTasksUseCase(repository)
        }
        
        @Provides
        @Singleton
        fun provideGenerateInstancesUseCase(
            repository: RecurringTasksRepository,
            recurrenceCalculator: RecurrenceCalculator
        ): GenerateInstancesUseCase {
            return GenerateInstancesUseCase(repository, recurrenceCalculator)
        }
        
        @Provides
        @Singleton
        fun provideRecurringTaskNotificationService(
            notificationApi: UltraSimpleNotificationApi
        ): RecurringTaskNotificationService {
            return RecurringTaskNotificationService(notificationApi)
        }
    }
}
