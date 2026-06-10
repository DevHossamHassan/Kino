package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator

import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculator for recurring task date calculations.
 * Handles all recurrence patterns: daily, weekly, monthly, and yearly.
 */
@Singleton
class RecurrenceCalculator @Inject constructor() {
    
    /**
     * Calculate next occurrence date based on recurrence rule
     */
    fun calculateNextOccurrence(
        rule: RecurrenceRule,
        fromDate: LocalDate
    ): LocalDate? {
        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> calculateNextDaily(rule, fromDate)
            RecurrenceFrequency.WEEKLY -> calculateNextWeekly(rule, fromDate)
            RecurrenceFrequency.MONTHLY -> calculateNextMonthly(rule, fromDate)
            RecurrenceFrequency.YEARLY -> calculateNextYearly(rule, fromDate)
        }
    }
    
    /**
     * Generate all occurrences between two dates
     */
    fun generateOccurrences(
        rule: RecurrenceRule,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<LocalDate> {
        val occurrences = mutableListOf<LocalDate>()
        var currentDate = startDate
        
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            occurrences.add(currentDate)
            
            val nextDate = calculateNextOccurrence(rule, currentDate)
            if (nextDate == null || nextDate.isAfter(endDate)) {
                break
            }
            currentDate = nextDate
        }
        
        return occurrences
    }
    
    /**
     * Check if a specific date matches the recurrence rule
     */
    fun isOccurrenceDate(
        rule: RecurrenceRule,
        date: LocalDate,
        startDate: LocalDate
    ): Boolean {
        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> isDailyOccurrence(rule, date, startDate)
            RecurrenceFrequency.WEEKLY -> isWeeklyOccurrence(rule, date, startDate)
            RecurrenceFrequency.MONTHLY -> isMonthlyOccurrence(rule, date, startDate)
            RecurrenceFrequency.YEARLY -> isYearlyOccurrence(rule, date, startDate)
        }
    }
    
    private fun calculateNextDaily(
        rule: RecurrenceRule,
        fromDate: LocalDate
    ): LocalDate {
        return fromDate.plusDays(rule.interval.toLong())
    }
    
    private fun calculateNextWeekly(
        rule: RecurrenceRule,
        fromDate: LocalDate
    ): LocalDate? {
        require(rule.daysOfWeek.isNotEmpty()) { "Weekly recurrence requires daysOfWeek" }
        
        val sortedDays = rule.daysOfWeek.sorted()
        val currentDayOfWeek = fromDate.dayOfWeek
        
        // Find next day in current week
        val nextDayInWeek = sortedDays.firstOrNull { it > currentDayOfWeek }
        
        return if (nextDayInWeek != null) {
            // Next occurrence is in the same week
            fromDate.with(nextDayInWeek)
        } else {
            // Move to next week(s) and use first day
            val weeksToAdd = rule.interval.toLong()
            fromDate.plusWeeks(weeksToAdd).with(sortedDays.first())
        }
    }
    
    private fun calculateNextMonthly(
        rule: RecurrenceRule,
        fromDate: LocalDate
    ): LocalDate {
        val dayOfMonth = rule.dayOfMonth ?: fromDate.dayOfMonth
        var nextDate = fromDate.plusMonths(rule.interval.toLong())
        
        // Handle months with fewer days (e.g., Feb 30 -> Feb 28)
        val maxDayInMonth = nextDate.lengthOfMonth()
        val adjustedDay = minOf(dayOfMonth, maxDayInMonth)
        
        return nextDate.withDayOfMonth(adjustedDay)
    }
    
    private fun calculateNextYearly(
        rule: RecurrenceRule,
        fromDate: LocalDate
    ): LocalDate {
        val month = rule.monthOfYear ?: fromDate.monthValue
        val dayOfMonth = rule.dayOfMonth ?: fromDate.dayOfMonth
        
        var nextDate = fromDate.plusYears(rule.interval.toLong())
        nextDate = nextDate.withMonth(month)
        
        // Handle leap years and month variations
        val maxDayInMonth = nextDate.lengthOfMonth()
        val adjustedDay = minOf(dayOfMonth, maxDayInMonth)
        
        return nextDate.withDayOfMonth(adjustedDay)
    }
    
    private fun isDailyOccurrence(
        rule: RecurrenceRule,
        date: LocalDate,
        startDate: LocalDate
    ): Boolean {
        val daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(startDate, date)
        return daysSinceStart >= 0 && daysSinceStart % rule.interval == 0L
    }
    
    private fun isWeeklyOccurrence(
        rule: RecurrenceRule,
        date: LocalDate,
        startDate: LocalDate
    ): Boolean {
        if (!rule.daysOfWeek.contains(date.dayOfWeek)) return false
        
        val weeksSinceStart = java.time.temporal.ChronoUnit.WEEKS.between(startDate, date)
        return weeksSinceStart >= 0 && weeksSinceStart % rule.interval == 0L
    }
    
    private fun isMonthlyOccurrence(
        rule: RecurrenceRule,
        date: LocalDate,
        startDate: LocalDate
    ): Boolean {
        val dayOfMonth = rule.dayOfMonth ?: startDate.dayOfMonth
        if (date.dayOfMonth != dayOfMonth) return false
        
        val monthsSinceStart = java.time.temporal.ChronoUnit.MONTHS.between(startDate, date)
        return monthsSinceStart >= 0 && monthsSinceStart % rule.interval == 0L
    }
    
    private fun isYearlyOccurrence(
        rule: RecurrenceRule,
        date: LocalDate,
        startDate: LocalDate
    ): Boolean {
        val month = rule.monthOfYear ?: startDate.monthValue
        val dayOfMonth = rule.dayOfMonth ?: startDate.dayOfMonth
        
        if (date.monthValue != month || date.dayOfMonth != dayOfMonth) return false
        
        val yearsSinceStart = java.time.temporal.ChronoUnit.YEARS.between(startDate, date)
        return yearsSinceStart >= 0 && yearsSinceStart % rule.interval == 0L
    }
    
    /**
     * Get human-readable description of recurrence
     */
    fun getRecurrenceDescription(rule: RecurrenceRule): String {
        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> {
                if (rule.interval == 1) "Every day at ${rule.timeOfDay}"
                else "Every ${rule.interval} days at ${rule.timeOfDay}"
            }
            RecurrenceFrequency.WEEKLY -> {
                val daysStr = rule.daysOfWeek
                    .sorted()
                    .joinToString(", ") { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
                
                if (rule.interval == 1) "Every week on $daysStr at ${rule.timeOfDay}"
                else "Every ${rule.interval} weeks on $daysStr at ${rule.timeOfDay}"
            }
            RecurrenceFrequency.MONTHLY -> {
                val day = rule.dayOfMonth ?: 1
                if (rule.interval == 1) "Monthly on day $day at ${rule.timeOfDay}"
                else "Every ${rule.interval} months on day $day at ${rule.timeOfDay}"
            }
            RecurrenceFrequency.YEARLY -> {
                val month = Month.of(rule.monthOfYear ?: 1)
                    .getDisplayName(TextStyle.FULL, Locale.getDefault())
                val day = rule.dayOfMonth ?: 1
                
                if (rule.interval == 1) "Yearly on $month $day at ${rule.timeOfDay}"
                else "Every ${rule.interval} years on $month $day at ${rule.timeOfDay}"
            }
        }
    }
    
    /**
     * Get next N occurrences from a given date
     */
    fun getNextOccurrences(
        rule: RecurrenceRule,
        fromDate: LocalDate,
        count: Int
    ): List<LocalDate> {
        val occurrences = mutableListOf<LocalDate>()
        var currentDate = fromDate
        
        // First, check if the fromDate itself is a valid occurrence
        if (isOccurrenceDate(rule, fromDate, fromDate)) {
            occurrences.add(fromDate)
            if (occurrences.size >= count) {
                return occurrences
            }
            // Move to next day to find subsequent occurrences
            currentDate = fromDate.plusDays(1)
        }
        
        // Find remaining occurrences
        while (occurrences.size < count) {
            val nextDate = calculateNextOccurrence(rule, currentDate)
            if (nextDate != null) {
                occurrences.add(nextDate)
                currentDate = nextDate.plusDays(1) // Move past this date
            } else {
                break
            }
        }
        
        return occurrences
    }
    
    /**
     * Validate if a recurrence rule is valid
     */
    fun validateRecurrenceRule(rule: RecurrenceRule): Result<Unit> {
        return try {
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
                else -> {}
            }
            Result.success(Unit)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }
}
