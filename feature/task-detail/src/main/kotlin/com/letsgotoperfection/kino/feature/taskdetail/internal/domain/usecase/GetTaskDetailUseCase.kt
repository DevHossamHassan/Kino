package com.letsgotoperfection.kino.feature.taskdetail.internal.domain.usecase

import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model.TaskDetail
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.repository.TaskDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting detailed task information.
 * This encapsulates the business logic for retrieving task details.
 */
@Singleton
class GetTaskDetailUseCase @Inject constructor(
    private val repository: TaskDetailRepository
) {
    /**
     * Get detailed task information with all related data.
     * 
     * @param taskId The unique identifier of the task
     * @return Flow of TaskDetail containing all task information
     */
    operator fun invoke(taskId: String): Flow<TaskDetail> {
        return repository.getTaskDetail(taskId)
    }
}
