package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTasksRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class CreateRecurringTaskUseCaseTest {

    private lateinit var repository: FakeRecurringTasksRepository
    private lateinit var alarmScheduler: FakeRecurringTaskAlarmScheduler
    private lateinit var useCase: CreateRecurringTaskUseCase

    private val dailyRule = RecurrenceRule(
        frequency = RecurrenceFrequency.DAILY,
        interval = 1,
        timeOfDay = LocalTime.of(9, 0)
    )

    @BeforeEach
    fun setup() {
        repository = FakeRecurringTasksRepository()
        alarmScheduler = FakeRecurringTaskAlarmScheduler()
        useCase = CreateRecurringTaskUseCase(
            repository = repository,
            recurrenceCalculator = RecurrenceCalculator(),
            alarmScheduler = alarmScheduler
        )
    }

    private suspend fun create(isActive: Boolean = true) = useCase(
        title = "Daily standup",
        description = "Sync with the team",
        section = TaskSection.WORK,
        priority = Priority.MEDIUM,
        labels = emptyList(),
        recurrenceRule = dailyRule,
        startDate = LocalDate.of(2026, 1, 1),
        endDate = null,
        isActive = isActive
    )

    @Test
    fun `creates active task and schedules upcoming occurrences`() = runTest {
        val result = create()

        assertThat(result.isSuccess).isTrue()
        val createdId = result.getOrNull()
        assertThat(repository.getAllRecurringTasks().first().map { it.id })
            .containsExactly(createdId)
        assertThat(alarmScheduler.scheduledWindows.map { it.id }).containsExactly(createdId)
    }

    @Test
    fun `does not schedule occurrences for inactive task`() = runTest {
        val result = create(isActive = false)

        assertThat(result.isSuccess).isTrue()
        assertThat(alarmScheduler.scheduledWindows).isEmpty()
    }

    @Test
    fun `propagates repository failure without scheduling`() = runTest {
        repository.shouldFail = true

        val result = create()

        assertThat(result.isFailure).isTrue()
        assertThat(alarmScheduler.scheduledWindows).isEmpty()
    }
}
