package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model

import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Template for recurring tasks that generates task instances based on recurrence rules
 */
data class RecurringTask(
    val id: String,
    val title: String,
    val description: String,
    val section: TaskSection,
    val priority: Priority,
    val labels: List<Label>,
    val recurrenceRule: RecurrenceRule,
    val startDate: LocalDate,
    val endDate: LocalDate?,  // null = never ends
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastGeneratedDate: LocalDate?,  // Last date an instance was generated
    val defaultColumn: TaskColumn = TaskColumn.TODO_THIS_WEEK,
    val checklistTemplate: List<String> = emptyList(), // Template checklist items
    val dueDateOffsetDays: Int = 0 // Days to add to creation date for due date (0 = same day)
) {
    /**
     * Check if this recurring task should generate instances on a given date.
     * Recurrence semantics are delegated to [RecurrenceCalculator] so the rule
     * matching logic exists in exactly one place.
     */
    fun shouldGenerateOn(date: LocalDate): Boolean {
        if (!isActive) return false
        if (date.isBefore(startDate)) return false
        if (endDate != null && date.isAfter(endDate)) return false
        return RecurrenceCalculator.matchesRule(recurrenceRule, date, startDate)
    }
}
