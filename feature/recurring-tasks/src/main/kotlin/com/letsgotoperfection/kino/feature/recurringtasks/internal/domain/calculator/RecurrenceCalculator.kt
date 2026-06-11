package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator

import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for recurring task date calculations.
 * Handles all recurrence patterns: daily, weekly, monthly, and yearly.
 *
 * All occurrence checks are anchored to the recurring task's start date so that
 * interval-based rules (e.g. "every 2 weeks") stay consistent across the app.
 */
@Singleton
class RecurrenceCalculator @Inject constructor() {

    /**
     * Check if a specific date matches the recurrence rule, anchored at [startDate].
     */
    fun isOccurrenceDate(
        rule: RecurrenceRule,
        date: LocalDate,
        startDate: LocalDate
    ): Boolean = matchesRule(rule, date, startDate)

    /**
     * Calculate the next occurrence strictly after [fromDate], anchored at [startDate].
     *
     * @param endDate Optional inclusive upper bound; null means no bound.
     * @return The next occurrence date, or null when none exists within bounds.
     */
    fun nextOccurrenceAfter(
        rule: RecurrenceRule,
        startDate: LocalDate,
        fromDate: LocalDate,
        endDate: LocalDate? = null
    ): LocalDate? {
        var candidate = maxOf(fromDate.plusDays(1), startDate)
        val bound = endDate ?: candidate.plusDays(MAX_SCAN_DAYS)
        while (!candidate.isAfter(bound)) {
            if (matchesRule(rule, candidate, startDate)) return candidate
            candidate = candidate.plusDays(1)
        }
        return null
    }

    /**
     * Get the next [count] occurrences on or after [fromDate], anchored at [startDate].
     */
    fun getNextOccurrences(
        rule: RecurrenceRule,
        startDate: LocalDate,
        fromDate: LocalDate,
        count: Int,
        endDate: LocalDate? = null
    ): List<LocalDate> {
        val occurrences = mutableListOf<LocalDate>()
        var candidate = maxOf(fromDate, startDate)
        val bound = endDate ?: candidate.plusDays(MAX_SCAN_DAYS)
        while (occurrences.size < count && !candidate.isAfter(bound)) {
            if (matchesRule(rule, candidate, startDate)) {
                occurrences.add(candidate)
            }
            candidate = candidate.plusDays(1)
        }
        return occurrences
    }

    /**
     * Generate all occurrences between [fromDate] and [toDate] (both inclusive),
     * anchored at [startDate].
     */
    fun generateOccurrences(
        rule: RecurrenceRule,
        startDate: LocalDate,
        fromDate: LocalDate,
        toDate: LocalDate
    ): List<LocalDate> {
        if (toDate.isBefore(fromDate)) return emptyList()
        val occurrences = mutableListOf<LocalDate>()
        var candidate = maxOf(fromDate, startDate)
        while (!candidate.isAfter(toDate)) {
            if (matchesRule(rule, candidate, startDate)) {
                occurrences.add(candidate)
            }
            candidate = candidate.plusDays(1)
        }
        return occurrences
    }

    /**
     * Validate if a recurrence rule is internally consistent.
     */
    fun validateRecurrenceRule(rule: RecurrenceRule): Result<Unit> {
        return try {
            require(rule.interval > 0) { "Interval must be positive" }
            when (rule.frequency) {
                RecurrenceFrequency.WEEKLY -> {
                    require(rule.daysOfWeek.isNotEmpty()) {
                        "Weekly recurrence requires at least one day of week"
                    }
                }
                RecurrenceFrequency.MONTHLY -> {
                    require(rule.dayOfMonth != null && rule.dayOfMonth in 1..31) {
                        "Monthly recurrence requires valid day of month (1-31)"
                    }
                }
                RecurrenceFrequency.YEARLY -> {
                    require(rule.monthOfYear != null && rule.monthOfYear in 1..12) {
                        "Yearly recurrence requires valid month of year (1-12)"
                    }
                    require(rule.dayOfMonth != null && rule.dayOfMonth in 1..31) {
                        "Yearly recurrence requires valid day of month (1-31)"
                    }
                }
                RecurrenceFrequency.DAILY -> Unit
            }
            Result.success(Unit)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }

    companion object {
        /** Upper bound for unbounded day scans (covers yearly rules with interval > 1). */
        private const val MAX_SCAN_DAYS = 366L * 5

        /**
         * Pure occurrence check shared with [RecurringTask.shouldGenerateOn] so the
         * recurrence semantics exist in exactly one place.
         */
        fun matchesRule(rule: RecurrenceRule, date: LocalDate, startDate: LocalDate): Boolean {
            if (date.isBefore(startDate)) return false
            return when (rule.frequency) {
                RecurrenceFrequency.DAILY -> {
                    val daysSinceStart = ChronoUnit.DAYS.between(startDate, date)
                    daysSinceStart % rule.interval == 0L
                }
                RecurrenceFrequency.WEEKLY -> {
                    if (!rule.daysOfWeek.contains(date.dayOfWeek)) return false
                    val weeksSinceStart = ChronoUnit.WEEKS.between(startDate, date)
                    weeksSinceStart % rule.interval == 0L
                }
                RecurrenceFrequency.MONTHLY -> {
                    val dayOfMonth = rule.dayOfMonth ?: startDate.dayOfMonth
                    val effectiveDay = minOf(dayOfMonth, date.lengthOfMonth())
                    if (date.dayOfMonth != effectiveDay) return false
                    val monthsSinceStart = ChronoUnit.MONTHS.between(
                        startDate.withDayOfMonth(1),
                        date.withDayOfMonth(1)
                    )
                    monthsSinceStart % rule.interval == 0L
                }
                RecurrenceFrequency.YEARLY -> {
                    val month = rule.monthOfYear ?: startDate.monthValue
                    val dayOfMonth = rule.dayOfMonth ?: startDate.dayOfMonth
                    if (date.monthValue != month) return false
                    val effectiveDay = minOf(dayOfMonth, date.lengthOfMonth())
                    if (date.dayOfMonth != effectiveDay) return false
                    val yearsSinceStart = (date.year - startDate.year).toLong()
                    yearsSinceStart % rule.interval == 0L
                }
            }
        }
    }
}
