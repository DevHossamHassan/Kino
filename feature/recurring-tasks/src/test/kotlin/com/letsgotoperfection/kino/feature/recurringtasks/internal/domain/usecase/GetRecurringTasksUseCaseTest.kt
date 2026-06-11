package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTasksRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class GetRecurringTasksUseCaseTest {

    private lateinit var repository: FakeRecurringTasksRepository
    private lateinit var useCase: GetRecurringTasksUseCase

    @BeforeEach
    fun setup() {
        repository = FakeRecurringTasksRepository()
        useCase = GetRecurringTasksUseCase(repository)
    }

    private fun task(id: String, isActive: Boolean = true) = RecurringTask(
        id = id,
        title = "Task $id",
        description = "",
        section = TaskSection.PERSONAL,
        priority = Priority.LOW,
        labels = emptyList(),
        recurrenceRule = RecurrenceRule(
            frequency = RecurrenceFrequency.DAILY,
            interval = 1,
            timeOfDay = LocalTime.of(9, 0)
        ),
        startDate = LocalDate.of(2026, 1, 1),
        endDate = null,
        isActive = isActive,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
        updatedAt = LocalDateTime.of(2026, 1, 1, 0, 0),
        lastGeneratedDate = null
    )

    @Test
    fun `active tasks flow only emits active templates`() = runTest {
        repository.addTask(task("active-1"))
        repository.addTask(task("inactive-1", isActive = false))

        useCase.getActiveRecurringTasks().test {
            val tasks = awaitItem()
            assertThat(tasks.map { it.id }).containsExactly("active-1")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `all tasks flow emits active and inactive templates`() = runTest {
        repository.addTask(task("active-1"))
        repository.addTask(task("inactive-1", isActive = false))

        useCase.getAllRecurringTasks().test {
            val tasks = awaitItem()
            assertThat(tasks.map { it.id }).containsExactly("active-1", "inactive-1")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `get by id returns matching task or null`() = runTest {
        repository.addTask(task("known"))

        assertThat(useCase.getRecurringTaskById("known")?.id).isEqualTo("known")
        assertThat(useCase.getRecurringTaskById("unknown")).isNull()
    }

    @Test
    fun `observe by id reflects later updates`() = runTest {
        val original = task("watched")
        repository.addTask(original)

        useCase.observeRecurringTaskById("watched").test {
            assertThat(awaitItem()?.isActive).isTrue()

            repository.addTask(original.copy(isActive = false))

            assertThat(awaitItem()?.isActive).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
