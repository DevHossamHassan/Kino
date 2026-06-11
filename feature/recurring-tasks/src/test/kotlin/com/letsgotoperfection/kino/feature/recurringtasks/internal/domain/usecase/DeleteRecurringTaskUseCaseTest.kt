package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTasksRepository
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.RecurringTaskTestData
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteRecurringTaskUseCaseTest {

    private lateinit var repository: FakeRecurringTasksRepository
    private lateinit var alarmScheduler: FakeRecurringTaskAlarmScheduler
    private lateinit var useCase: DeleteRecurringTaskUseCase

    @BeforeEach
    fun setup() {
        repository = FakeRecurringTasksRepository()
        alarmScheduler = FakeRecurringTaskAlarmScheduler()
        useCase = DeleteRecurringTaskUseCase(repository, alarmScheduler)
    }

    @Test
    fun `deletes task and cancels all pending alarms`() = runTest {
        val task = RecurringTaskTestData.dailyTask()
        repository.addTask(task)

        val result = useCase(task.id)

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.getRecurringTaskById(task.id)).isNull()
        assertThat(alarmScheduler.cancelledAll).containsExactly(task.id)
    }

    @Test
    fun `does not cancel alarms when deletion fails`() = runTest {
        repository.shouldFail = true

        val result = useCase("task-1")

        assertThat(result.isFailure).isTrue()
        assertThat(alarmScheduler.cancelledAll).isEmpty()
    }
}
