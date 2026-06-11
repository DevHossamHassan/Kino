package com.letsgotoperfection.kino.feature.kanban.internal.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.database.dao.ChecklistDao
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.entity.TaskEntity
import com.letsgotoperfection.kino.core.database.mapper.toDomain
import com.letsgotoperfection.kino.core.database.mapper.toEntity
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.kanban.api.KanbanApi
import com.letsgotoperfection.kino.feature.kanban.api.TaskUpdate
import com.letsgotoperfection.kino.feature.kanban.api.UpdateType
import com.letsgotoperfection.kino.feature.kanban.KanbanScreen as KanbanScreenComposable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KanbanApiImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val checklistDao: ChecklistDao
) : KanbanApi {

    override suspend fun getTask(taskId: String): Result<Task> {
        return try {
            val entity = taskDao.getTaskById(taskId)
                ?: return Result.Error(IllegalArgumentException("Task not found: $taskId"))
            val checklist = checklistDao.getChecklistItemsOnce(taskId).map { it.toDomain() }
            Result.Success(entity.toDomain(checklist = checklist))
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

    override suspend fun createTask(task: Task): Result<String> {
        return try {
            val entity: TaskEntity = task.toEntity()
            taskDao.upsertTask(entity)
            if (task.checklist.isNotEmpty()) {
                checklistDao.upsertChecklistItems(task.checklist.map { it.toEntity() })
            }
            Result.Success(entity.id)
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            taskDao.upsertTask(task.toEntity())
            if (task.checklist.isNotEmpty()) {
                checklistDao.upsertChecklistItems(task.checklist.map { it.toEntity() })
            }
            Result.Success(Unit)
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            val entity = taskDao.getTaskById(taskId)
                ?: return Result.Error(IllegalArgumentException("Task not found: $taskId"))
            taskDao.deleteTask(entity)
            Result.Success(Unit)
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

    override fun getTaskDetailRoute(taskId: String): String =
        "task_detail/$taskId"

    override fun observeTaskUpdates(): Flow<TaskUpdate> {
        return taskDao.getAllTasks().map { list ->
            // Simple mapping: emit UPDATED for all changes; can be improved with Diff
            val last = list.lastOrNull() ?: return@map TaskUpdate("", UpdateType.UPDATED)
            TaskUpdate(last.id, UpdateType.UPDATED)
        }
    }

    @Composable
    override fun KanbanScreen(
        onTaskClick: (String) -> Unit,
        onCreateTask: () -> Unit,
        modifier: Modifier
    ) {
        KanbanScreenComposable(
            onTaskClick = onTaskClick,
            onCreateTask = onCreateTask,
            modifier = modifier
        )
    }
}


