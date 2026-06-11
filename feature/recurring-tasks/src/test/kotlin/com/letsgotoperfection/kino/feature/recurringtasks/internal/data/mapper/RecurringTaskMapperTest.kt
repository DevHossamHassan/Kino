package com.letsgotoperfection.kino.feature.recurringtasks.internal.data.mapper

import com.google.common.truth.Truth.assertThat
import com.letsgotoperfection.kino.core.database.entity.RecurringTaskEntity
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class RecurringTaskMapperTest {

    private fun testEntity(
        frequency: String = "weekly",
        daysOfWeek: String = "[1,3]",
        dayOfMonth: Int? = null,
        monthOfYear: Int? = null,
        checklistTemplate: String = """["Step 1","Step 2"]"""
    ) = RecurringTaskEntity(
        id = "task-1",
        title = "Weekly review",
        description = "Review the week",
        section = "work",
        priority = "high",
        frequency = frequency,
        interval = 2,
        daysOfWeek = daysOfWeek,
        dayOfMonth = dayOfMonth,
        monthOfYear = monthOfYear,
        timeOfDay = "09:30",
        startDate = LocalDate.of(2026, 1, 5).toEpochDay(),
        endDate = LocalDate.of(2026, 12, 31).toEpochDay(),
        isActive = true,
        createdAt = 1_700_000_000_000L,
        updatedAt = 1_700_000_100_000L,
        lastGeneratedDate = LocalDate.of(2026, 1, 5).toEpochDay(),
        defaultColumn = "in_progress",
        checklistTemplate = checklistTemplate,
        dueDateOffsetDays = 3
    )

    private fun testDomain() = RecurringTask(
        id = "task-2",
        title = "Monthly bills",
        description = "Pay all bills",
        section = TaskSection.PERSONAL,
        priority = Priority.MEDIUM,
        labels = emptyList(),
        recurrenceRule = RecurrenceRule(
            frequency = RecurrenceFrequency.MONTHLY,
            interval = 1,
            dayOfMonth = 28,
            timeOfDay = LocalTime.of(8, 0)
        ),
        startDate = LocalDate.of(2026, 2, 28),
        endDate = null,
        isActive = false,
        createdAt = LocalDateTime.of(2026, 1, 1, 10, 0),
        updatedAt = LocalDateTime.of(2026, 1, 2, 11, 0),
        lastGeneratedDate = null,
        defaultColumn = TaskColumn.BACKLOG,
        checklistTemplate = listOf("Electricity", "Internet"),
        dueDateOffsetDays = 0
    )

    @Test
    fun `entity maps to domain with all fields`() {
        val domain = testEntity().toDomain()

        assertThat(domain.id).isEqualTo("task-1")
        assertThat(domain.section).isEqualTo(TaskSection.WORK)
        assertThat(domain.priority).isEqualTo(Priority.HIGH)
        assertThat(domain.recurrenceRule.frequency).isEqualTo(RecurrenceFrequency.WEEKLY)
        assertThat(domain.recurrenceRule.interval).isEqualTo(2)
        assertThat(domain.recurrenceRule.daysOfWeek)
            .containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        assertThat(domain.recurrenceRule.timeOfDay).isEqualTo(LocalTime.of(9, 30))
        assertThat(domain.startDate).isEqualTo(LocalDate.of(2026, 1, 5))
        assertThat(domain.endDate).isEqualTo(LocalDate.of(2026, 12, 31))
        assertThat(domain.defaultColumn).isEqualTo(TaskColumn.IN_PROGRESS)
        assertThat(domain.checklistTemplate).containsExactly("Step 1", "Step 2").inOrder()
        assertThat(domain.dueDateOffsetDays).isEqualTo(3)
    }

    @Test
    fun `domain maps to entity with lowercase enum names`() {
        val entity = testDomain().toEntity()

        assertThat(entity.section).isEqualTo("personal")
        assertThat(entity.priority).isEqualTo("medium")
        assertThat(entity.frequency).isEqualTo("monthly")
        assertThat(entity.dayOfMonth).isEqualTo(28)
        assertThat(entity.timeOfDay).isEqualTo("08:00")
        assertThat(entity.endDate).isNull()
        assertThat(entity.isActive).isFalse()
        assertThat(entity.defaultColumn).isEqualTo("backlog")
        assertThat(entity.checklistTemplate).isEqualTo("""["Electricity","Internet"]""")
    }

    @Test
    fun `round trip preserves domain model`() {
        val original = testDomain()

        val roundTripped = original.toEntity().toDomain()

        assertThat(roundTripped).isEqualTo(original)
    }

    @Test
    fun `malformed daysOfWeek json maps to empty set for non-weekly rule`() {
        val domain = testEntity(
            frequency = "daily",
            daysOfWeek = "not-json"
        ).toDomain()

        assertThat(domain.recurrenceRule.daysOfWeek).isEmpty()
    }

    @Test
    fun `malformed checklist template json maps to empty list`() {
        val domain = testEntity(checklistTemplate = "{broken").toDomain()

        assertThat(domain.checklistTemplate).isEmpty()
    }

    @Test
    fun `empty daysOfWeek serializes to empty json array`() {
        val entity = testDomain().toEntity()

        assertThat(entity.daysOfWeek).isEqualTo("[]")
    }
}
