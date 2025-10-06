package com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase

import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.common.runCatching
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository.TaskDetailRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskDetailRepository
) {
    suspend operator fun invoke(taskId: String): Result<Unit> = com.letsgotoperfection.kino.core.common.runCatching {
        repository.deleteTask(taskId)
    }
}
