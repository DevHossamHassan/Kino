package com.letsgotoperfection.kino.navigation

import androidx.lifecycle.ViewModel
import com.letsgotoperfection.kino.core.model.TaskCreationRequest
import com.letsgotoperfection.kino.data.TaskRepository
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class TaskCreationViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    suspend fun createTask(request: TaskCreationRequest): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val created = taskRepository.createTask(
                title = request.title.trim(),
                description = request.description,
                section = request.section,
                column = request.column,
                priority = request.priority,
                dueDate = request.dueDate,
                progress = 0, // Default progress for new tasks
                labels = request.labels
            )
            created.id
        }
    }
}
