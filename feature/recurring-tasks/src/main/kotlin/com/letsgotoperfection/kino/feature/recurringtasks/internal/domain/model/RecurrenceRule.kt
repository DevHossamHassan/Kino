package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

/**
 * Recurrence pattern definition
 */
data class RecurrenceRule(
    val frequency: RecurrenceFrequency,
    val interval: Int,  // Every X days/weeks/months/years
    val daysOfWeek: Set<DayOfWeek> = emptySet(),  // For weekly
    val dayOfMonth: Int? = null,  // For monthly (1-31)
    val monthOfYear: Int? = null,  // For yearly (1-12)
    val timeOfDay: LocalTime
) {
    init {
        require(interval > 0) { "Interval must be positive" }
        when (frequency) {
            RecurrenceFrequency.WEEKLY -> require(daysOfWeek.isNotEmpty()) { 
                "Weekly recurrence requires at least one day of week" 
            }
            RecurrenceFrequency.MONTHLY -> require(dayOfMonth != null && dayOfMonth in 1..31) { 
                "Monthly recurrence requires valid day of month (1-31)" 
            }
            RecurrenceFrequency.YEARLY -> {
                require(monthOfYear != null && monthOfYear in 1..12) { 
                    "Yearly recurrence requires valid month of year (1-12)" 
                }
                require(dayOfMonth != null && dayOfMonth in 1..31) { 
                    "Yearly recurrence requires valid day of month (1-31)" 
                }
            }
            else -> {}
        }
    }
}

