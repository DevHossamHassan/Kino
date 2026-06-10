package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model

import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
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
    val defaultColumn: com.letsgotoperfection.kino.core.model.TaskColumn = com.letsgotoperfection.kino.core.model.TaskColumn.TODO_THIS_WEEK,
    val checklistTemplate: List<String> = emptyList(), // Template checklist items
    val dueDateOffsetDays: Int = 0 // Days to add to creation date for due date (0 = same day)
) {
    /**
     * Check if this recurring task should generate instances on a given date
     */
    fun shouldGenerateOn(date: LocalDate): Boolean {
        if (!isActive) return false
        if (date.isBefore(startDate)) return false
        if (endDate != null && date.isAfter(endDate)) return false
        
        return when (recurrenceRule.frequency) {
            RecurrenceFrequency.DAILY -> {
                val daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(startDate, date)
                daysSinceStart % recurrenceRule.interval == 0L
            }
            RecurrenceFrequency.WEEKLY -> {
                recurrenceRule.daysOfWeek.contains(date.dayOfWeek) &&
                isWeeklyIntervalMatch(date)
            }
            RecurrenceFrequency.MONTHLY -> {
                val dayOfMonth = recurrenceRule.dayOfMonth ?: startDate.dayOfMonth
                date.dayOfMonth == dayOfMonth && isMonthlyIntervalMatch(date)
            }
            RecurrenceFrequency.YEARLY -> {
                val month = recurrenceRule.monthOfYear ?: startDate.monthValue
                val dayOfMonth = recurrenceRule.dayOfMonth ?: startDate.dayOfMonth
                date.monthValue == month && date.dayOfMonth == dayOfMonth && isYearlyIntervalMatch(date)
            }
        }
    }
    
    private fun isWeeklyIntervalMatch(date: LocalDate): Boolean {
        val weeksSinceStart = java.time.temporal.ChronoUnit.WEEKS.between(startDate, date)
        return weeksSinceStart % recurrenceRule.interval == 0L
    }
    
    private fun isMonthlyIntervalMatch(date: LocalDate): Boolean {
        val monthsSinceStart = java.time.temporal.ChronoUnit.MONTHS.between(startDate, date)
        return monthsSinceStart % recurrenceRule.interval == 0L
    }
    
    private fun isYearlyIntervalMatch(date: LocalDate): Boolean {
        val yearsSinceStart = java.time.temporal.ChronoUnit.YEARS.between(startDate, date)
        return yearsSinceStart % recurrenceRule.interval == 0L
    }
}

/**
 * Generated task instance with link to parent recurring task
 */
data class TaskInstance(
    val taskId: String,
    val recurringTaskId: String,  // Parent template ID
    val scheduledDate: LocalDate,
    val isCompleted: Boolean,
    val completedAt: LocalDateTime?
)
