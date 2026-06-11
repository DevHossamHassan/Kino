package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTasksRepository
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.RecurringTaskTestData
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateRecurringTaskUseCaseTest {

    private lateinit var repository: FakeRecurringTasksRepository
    private lateinit var alarmScheduler: FakeRecurringTaskAlarmScheduler
    private lateinit var useCase: UpdateRecurringTaskUseCase

    @BeforeEach
    fun setup() {
        repository = FakeRecurringTasksRepository()
        alarmScheduler = FakeRecurringTaskAlarmScheduler()
        useCase = UpdateRecurringTaskUseCase(
            repository = repository,
            recurrenceCalculator = RecurrenceCalculator(),
            alarmScheduler = alarmScheduler
        )
    }

    @Test
    fun `updating active task cancels then reschedules alarms`() = runTest {
        val task = RecurringTaskTestData.dailyTask(isActive = true)
        repository.addTask(task)

        val result = useCase(task.copy(title = "Renamed standup"))

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.getRecurringTaskById(task.id)?.title).isEqualTo("Renamed standup")
        assertThat(alarmScheduler.cancelledAll).containsExactly(task.id)
        assertThat(alarmScheduler.scheduledWindows.map { it.id }).containsExactly(task.id)
    }

    @Test
    fun `updating inactive task only cancels alarms`() = runTest {
        val task = RecurringTaskTestData.dailyTask(isActive = false)
        repository.addTask(task)

        val result = useCase(task.copy(title = "Renamed standup"))

        assertThat(result.isSuccess).isTrue()
        assertThat(alarmScheduler.cancelledAll).containsExactly(task.id)
        assertThat(alarmScheduler.scheduledWindows).isEmpty()
    }

    @Test
    fun `updating unknown task fails without touching alarms`() = runTest {
        val result = useCase(RecurringTaskTestData.dailyTask(id = "missing-id"))

        assertThat(result.isFailure).isTrue()
        assertThat(alarmScheduler.cancelledAll).isEmpty()
        assertThat(alarmScheduler.scheduledWindows).isEmpty()
    }
}
