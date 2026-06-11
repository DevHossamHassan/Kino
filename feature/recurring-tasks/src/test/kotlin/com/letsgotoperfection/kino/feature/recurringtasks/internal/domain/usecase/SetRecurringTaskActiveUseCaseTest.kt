package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskNotFoundException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTasksRepository
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.RecurringTaskTestData
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SetRecurringTaskActiveUseCaseTest {

    private lateinit var repository: FakeRecurringTasksRepository
    private lateinit var alarmScheduler: FakeRecurringTaskAlarmScheduler
    private lateinit var useCase: SetRecurringTaskActiveUseCase

    @BeforeEach
    fun setup() {
        repository = FakeRecurringTasksRepository()
        alarmScheduler = FakeRecurringTaskAlarmScheduler()
        useCase = SetRecurringTaskActiveUseCase(repository, alarmScheduler)
    }

    @Test
    fun `pausing cancels all pending alarms`() = runTest {
        val task = RecurringTaskTestData.dailyTask(isActive = true)
        repository.addTask(task)

        val result = useCase(task.id, isActive = false)

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.getRecurringTaskById(task.id)?.isActive).isFalse()
        assertThat(alarmScheduler.cancelledAll).containsExactly(task.id)
        assertThat(alarmScheduler.scheduledWindows).isEmpty()
    }

    @Test
    fun `resuming schedules upcoming occurrences with active flag`() = runTest {
        val task = RecurringTaskTestData.dailyTask(isActive = false)
        repository.addTask(task)

        val result = useCase(task.id, isActive = true)

        assertThat(result.isSuccess).isTrue()
        val scheduled = alarmScheduler.scheduledWindows.single()
        assertThat(scheduled.id).isEqualTo(task.id)
        assertThat(scheduled.isActive).isTrue()
        assertThat(alarmScheduler.cancelledAll).isEmpty()
    }

    @Test
    fun `fails for unknown task without touching alarms`() = runTest {
        val result = useCase("missing-id", isActive = true)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RecurringTaskNotFoundException::class.java)
        assertThat(alarmScheduler.scheduledWindows).isEmpty()
        assertThat(alarmScheduler.cancelledAll).isEmpty()
    }
}
