package com.letsgotoperfection.kino.feature.recurringtasks.internal.fake

import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskNotFoundException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.repository.RecurringTasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * In-memory fake of [RecurringTasksRepository] for unit tests.
 */
internal class FakeRecurringTasksRepository : RecurringTasksRepository {

    private val tasks = MutableStateFlow<Map<String, RecurringTask>>(emptyMap())
    private val instances = mutableMapOf<Pair<String, LocalDate>, Task>()
    private val instanceKeys = mutableSetOf<Pair<String, LocalDate>>()

    var shouldFail = false

    fun addTask(task: RecurringTask) {
        tasks.value = tasks.value + (task.id to task)
    }

    /** Marks a (template, date) pair as already materialized on the board. */
    fun markInstanceExists(recurringTaskId: String, scheduledDate: LocalDate) {
        instanceKeys += recurringTaskId to scheduledDate
    }

    fun clear() {
        tasks.value = emptyMap()
        instances.clear()
        instanceKeys.clear()
        shouldFail = false
    }

    override fun getActiveRecurringTasks(): Flow<List<RecurringTask>> =
        tasks.map { all -> all.values.filter { it.isActive } }

    override fun getAllRecurringTasks(): Flow<List<RecurringTask>> =
        tasks.map { it.values.toList() }

    override suspend fun getRecurringTaskById(id: String): RecurringTask? = tasks.value[id]

    override fun observeRecurringTaskById(id: String): Flow<RecurringTask?> =
        tasks.map { it[id] }

    override suspend fun createRecurringTask(recurringTask: RecurringTask): Result<String> {
        if (shouldFail) return Result.failure(Exception("createRecurringTask failed"))
        addTask(recurringTask)
        return Result.success(recurringTask.id)
    }

    override suspend fun updateRecurringTask(recurringTask: RecurringTask): Result<Unit> {
        if (shouldFail) return Result.failure(Exception("updateRecurringTask failed"))
        if (!tasks.value.containsKey(recurringTask.id)) {
            return Result.failure(RecurringTaskNotFoundException(recurringTask.id))
        }
        addTask(recurringTask)
        return Result.success(Unit)
    }

    override suspend fun deleteRecurringTask(id: String): Result<Unit> {
        if (shouldFail) return Result.failure(Exception("deleteRecurringTask failed"))
        tasks.value = tasks.value - id
        return Result.success(Unit)
    }

    override suspend fun setRecurringTaskActive(id: String, isActive: Boolean): Result<Unit> {
        val task = tasks.value[id]
            ?: return Result.failure(RecurringTaskNotFoundException(id))
        addTask(task.copy(isActive = isActive))
        return Result.success(Unit)
    }

    override suspend fun updateLastGeneratedDate(id: String, date: LocalDate): Result<Unit> {
        val task = tasks.value[id]
            ?: return Result.failure(RecurringTaskNotFoundException(id))
        addTask(task.copy(lastGeneratedDate = date))
        return Result.success(Unit)
    }

    override suspend fun getRecurringTasksNeedingGeneration(): List<RecurringTask> =
        tasks.value.values.filter { it.isActive }

    override suspend fun taskInstanceExists(
        recurringTaskId: String,
        scheduledDate: LocalDate
    ): Boolean = (recurringTaskId to scheduledDate) in instanceKeys ||
        instances.containsKey(recurringTaskId to scheduledDate)

    override fun getTaskInstances(
        recurringTaskId: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Flow<List<Task>> = tasks.map {
        instances
            .filterKeys { (id, date) ->
                id == recurringTaskId && !date.isBefore(fromDate) && !date.isAfter(toDate)
            }
            .values
            .toList()
    }
}
