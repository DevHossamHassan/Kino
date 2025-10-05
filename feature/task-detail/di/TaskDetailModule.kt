package com.letsgotoperfection.kino.feature.taskdetail.di

import com.letsgotoperfection.kino.feature.taskdetail.api.TaskDetailApi
import com.letsgotoperfection.kino.feature.taskdetail.internal.data.repository.TaskDetailRepositoryImpl
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository.TaskDetailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Task Detail feature dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TaskDetailModule {
    
    @Binds
    @Singleton
    abstract fun bindTaskDetailRepository(
        impl: TaskDetailRepositoryImpl
    ): TaskDetailRepository
    
    // TODO: Add TaskDetailApi implementation binding when created
}
