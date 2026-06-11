package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model

import com.google.common.truth.Truth.assertThat
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class RecurringTaskTest {

    private fun dailyTask(
        isActive: Boolean = true,
        startDate: LocalDate = LocalDate.of(2026, 1, 1),
        endDate: LocalDate? = null
    ) = RecurringTask(
        id = "task-1",
        title = "Daily standup",
        description = "",
        section = TaskSection.WORK,
        priority = Priority.MEDIUM,
        labels = emptyList(),
        recurrenceRule = RecurrenceRule(
            frequency = RecurrenceFrequency.DAILY,
            interval = 1,
            timeOfDay = LocalTime.of(9, 0)
        ),
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = LocalDateTime.of(2026, 1, 1, 8, 0),
        updatedAt = LocalDateTime.of(2026, 1, 1, 8, 0),
        lastGeneratedDate = null
    )

    @Test
    fun `generates on matching date when active`() {
        val task = dailyTask()

        assertThat(task.shouldGenerateOn(LocalDate.of(2026, 1, 15))).isTrue()
    }

    @Test
    fun `does not generate when inactive`() {
        val task = dailyTask(isActive = false)

        assertThat(task.shouldGenerateOn(LocalDate.of(2026, 1, 15))).isFalse()
    }

    @Test
    fun `does not generate before start date`() {
        val task = dailyTask(startDate = LocalDate.of(2026, 1, 10))

        assertThat(task.shouldGenerateOn(LocalDate.of(2026, 1, 9))).isFalse()
        assertThat(task.shouldGenerateOn(LocalDate.of(2026, 1, 10))).isTrue()
    }

    @Test
    fun `does not generate after end date`() {
        val task = dailyTask(endDate = LocalDate.of(2026, 1, 31))

        assertThat(task.shouldGenerateOn(LocalDate.of(2026, 1, 31))).isTrue()
        assertThat(task.shouldGenerateOn(LocalDate.of(2026, 2, 1))).isFalse()
    }
}
