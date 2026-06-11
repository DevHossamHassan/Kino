package com.letsgotoperfection.kino.feature.recurringtasks.internal.fake

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.kanban.api.KanbanApi
import com.letsgotoperfection.kino.feature.kanban.api.TaskUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Recording fake of [KanbanApi] for unit tests.
 */
internal class FakeKanbanApi : KanbanApi {

    val createdTasks = mutableListOf<Task>()
    var shouldFailCreate = false

    override suspend fun getTask(taskId: String): Result<Task> =
        createdTasks.find { it.id == taskId }
            ?.let { Result.Success(it) }
            ?: Result.Error(NoSuchElementException("Task $taskId not found"))

    override suspend fun createTask(task: Task): Result<String> {
        if (shouldFailCreate) return Result.Error(IllegalStateException("createTask failed"))
        createdTasks += task
        return Result.Success(task.id)
    }

    override suspend fun updateTask(task: Task): Result<Unit> = Result.Success(Unit)

    override suspend fun deleteTask(taskId: String): Result<Unit> = Result.Success(Unit)

    override fun getTaskDetailRoute(taskId: String): String = "task/$taskId"

    override fun observeTaskUpdates(): Flow<TaskUpdate> = emptyFlow()

    @Composable
    override fun KanbanScreen(
        onTaskClick: (String) -> Unit,
        onCreateTask: () -> Unit,
        modifier: Modifier
    ) = Unit
}
