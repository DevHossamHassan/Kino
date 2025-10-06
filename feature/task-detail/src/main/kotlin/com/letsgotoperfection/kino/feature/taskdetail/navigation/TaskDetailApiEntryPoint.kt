package com.letsgotoperfection.kino.feature.taskdetail.navigation

import com.letsgotoperfection.kino.feature.taskdetail.api.TaskDetailApi
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TaskDetailEntryPoint {
    fun taskDetailApi(): TaskDetailApi
}



