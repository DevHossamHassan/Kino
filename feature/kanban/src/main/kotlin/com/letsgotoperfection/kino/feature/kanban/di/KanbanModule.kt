package com.letsgotoperfection.kino.feature.kanban.di

import com.letsgotoperfection.kino.feature.kanban.api.KanbanApi
import com.letsgotoperfection.kino.feature.kanban.internal.api.KanbanApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class KanbanModule {

    @Binds
    @Singleton
    abstract fun bindKanbanApi(impl: KanbanApiImpl): KanbanApi
}


