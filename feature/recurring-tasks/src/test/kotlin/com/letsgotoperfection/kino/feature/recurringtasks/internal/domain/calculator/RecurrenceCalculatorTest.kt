package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator

import com.google.common.truth.Truth.assertThat
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class RecurrenceCalculatorTest {

    private lateinit var calculator: RecurrenceCalculator

    private val nineAm: LocalTime = LocalTime.of(9, 0)

    @BeforeEach
    fun setup() {
        calculator = RecurrenceCalculator()
    }

    private fun dailyRule(interval: Int = 1) = RecurrenceRule(
        frequency = RecurrenceFrequency.DAILY,
        interval = interval,
        timeOfDay = nineAm
    )

    private fun weeklyRule(days: Set<DayOfWeek>, interval: Int = 1) = RecurrenceRule(
        frequency = RecurrenceFrequency.WEEKLY,
        interval = interval,
        daysOfWeek = days,
        timeOfDay = nineAm
    )

    private fun monthlyRule(dayOfMonth: Int, interval: Int = 1) = RecurrenceRule(
        frequency = RecurrenceFrequency.MONTHLY,
        interval = interval,
        dayOfMonth = dayOfMonth,
        timeOfDay = nineAm
    )

    private fun yearlyRule(month: Int, dayOfMonth: Int, interval: Int = 1) = RecurrenceRule(
        frequency = RecurrenceFrequency.YEARLY,
        interval = interval,
        monthOfYear = month,
        dayOfMonth = dayOfMonth,
        timeOfDay = nineAm
    )

    @Nested
    inner class DailyRecurrence {

        @Test
        fun `every day matches all dates from start`() {
            val start = LocalDate.of(2026, 1, 1)

            assertThat(calculator.isOccurrenceDate(dailyRule(), start, start)).isTrue()
            assertThat(calculator.isOccurrenceDate(dailyRule(), start.plusDays(1), start)).isTrue()
            assertThat(calculator.isOccurrenceDate(dailyRule(), start.plusDays(99), start)).isTrue()
        }

        @Test
        fun `date before start never matches`() {
            val start = LocalDate.of(2026, 1, 10)

            assertThat(
                calculator.isOccurrenceDate(dailyRule(), start.minusDays(1), start)
            ).isFalse()
        }

        @Test
        fun `every 3 days matches only anchored multiples`() {
            val start = LocalDate.of(2026, 1, 1)
            val rule = dailyRule(interval = 3)

            assertThat(calculator.isOccurrenceDate(rule, start, start)).isTrue()
            assertThat(calculator.isOccurrenceDate(rule, start.plusDays(1), start)).isFalse()
            assertThat(calculator.isOccurrenceDate(rule, start.plusDays(2), start)).isFalse()
            assertThat(calculator.isOccurrenceDate(rule, start.plusDays(3), start)).isTrue()
            assertThat(calculator.isOccurrenceDate(rule, start.plusDays(6), start)).isTrue()
        }
    }

    @Nested
    inner class WeeklyRecurrence {

        @Test
        fun `matches only selected days of week`() {
            // 2026-01-05 is a Monday
            val start = LocalDate.of(2026, 1, 5)
            val rule = weeklyRule(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))

            assertThat(calculator.isOccurrenceDate(rule, start, start)).isTrue()
            assertThat(calculator.isOccurrenceDate(rule, start.plusDays(1), start)).isFalse()
            assertThat(calculator.isOccurrenceDate(rule, start.plusDays(2), start)).isTrue()
            assertThat(calculator.isOccurrenceDate(rule, start.plusDays(4), start)).isFalse()
        }

        @Test
        fun `every 2 weeks skips alternating weeks`() {
            val start = LocalDate.of(2026, 1, 5) // Monday
            val rule = weeklyRule(setOf(DayOfWeek.MONDAY), interval = 2)

            assertThat(calculator.isOccurrenceDate(rule, start, start)).isTrue()
            assertThat(calculator.isOccurrenceDate(rule, start.plusWeeks(1), start)).isFalse()
            assertThat(calculator.isOccurrenceDate(rule, start.plusWeeks(2), start)).isTrue()
        }
    }

    @Nested
    inner class MonthlyRecurrence {

        @Test
        fun `matches the configured day of month`() {
            val start = LocalDate.of(2026, 1, 15)
            val rule = monthlyRule(dayOfMonth = 15)

            assertThat(calculator.isOccurrenceDate(rule, start, start)).isTrue()
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2026, 2, 15), start)
            ).isTrue()
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2026, 2, 16), start)
            ).isFalse()
        }

        @Test
        fun `day 31 clamps to last day of shorter months`() {
            val start = LocalDate.of(2026, 1, 31)
            val rule = monthlyRule(dayOfMonth = 31)

            // February 2026 has 28 days
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2026, 2, 28), start)
            ).isTrue()
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2026, 4, 30), start)
            ).isTrue()
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2026, 3, 30), start)
            ).isFalse()
        }

        @Test
        fun `every 2 months skips alternating months`() {
            val start = LocalDate.of(2026, 1, 10)
            val rule = monthlyRule(dayOfMonth = 10, interval = 2)

            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2026, 2, 10), start)
            ).isFalse()
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2026, 3, 10), start)
            ).isTrue()
        }
    }

    @Nested
    inner class YearlyRecurrence {

        @Test
        fun `matches the configured month and day each year`() {
            val start = LocalDate.of(2026, 6, 10)
            val rule = yearlyRule(month = 6, dayOfMonth = 10)

            assertThat(calculator.isOccurrenceDate(rule, start, start)).isTrue()
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2027, 6, 10), start)
            ).isTrue()
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2027, 6, 11), start)
            ).isFalse()
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2027, 7, 10), start)
            ).isFalse()
        }

        @Test
        fun `february 29 clamps to february 28 in non-leap years`() {
            val start = LocalDate.of(2024, 2, 29)
            val rule = yearlyRule(month = 2, dayOfMonth = 29)

            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2025, 2, 28), start)
            ).isTrue()
            assertThat(
                calculator.isOccurrenceDate(rule, LocalDate.of(2028, 2, 29), start)
            ).isTrue()
        }
    }

    @Nested
    inner class NextOccurrenceAfter {

        @Test
        fun `returns strictly later occurrence`() {
            val start = LocalDate.of(2026, 1, 1)

            val next = calculator.nextOccurrenceAfter(dailyRule(interval = 3), start, start)

            assertThat(next).isEqualTo(start.plusDays(3))
        }

        @Test
        fun `returns start when fromDate is before start`() {
            val start = LocalDate.of(2026, 5, 1)

            val next = calculator.nextOccurrenceAfter(
                rule = dailyRule(),
                startDate = start,
                fromDate = LocalDate.of(2026, 1, 1)
            )

            assertThat(next).isEqualTo(start)
        }

        @Test
        fun `returns null when end date excludes all candidates`() {
            val start = LocalDate.of(2026, 1, 1)
            val rule = weeklyRule(setOf(DayOfWeek.MONDAY))

            val next = calculator.nextOccurrenceAfter(
                rule = rule,
                startDate = start,
                fromDate = LocalDate.of(2026, 1, 1), // Thursday
                endDate = LocalDate.of(2026, 1, 3) // Saturday, before next Monday
            )

            assertThat(next).isNull()
        }
    }

    @Nested
    inner class GetNextOccurrences {

        @Test
        fun `returns requested number of occurrences including fromDate`() {
            val start = LocalDate.of(2026, 1, 1)

            val occurrences = calculator.getNextOccurrences(
                rule = dailyRule(interval = 2),
                startDate = start,
                fromDate = start,
                count = 3
            )

            assertThat(occurrences).containsExactly(
                start,
                start.plusDays(2),
                start.plusDays(4)
            ).inOrder()
        }

        @Test
        fun `respects end date bound`() {
            val start = LocalDate.of(2026, 1, 1)

            val occurrences = calculator.getNextOccurrences(
                rule = dailyRule(),
                startDate = start,
                fromDate = start,
                count = 10,
                endDate = start.plusDays(2)
            )

            assertThat(occurrences).hasSize(3)
        }
    }

    @Nested
    inner class GenerateOccurrences {

        @Test
        fun `generates all occurrences in inclusive range`() {
            val start = LocalDate.of(2026, 1, 5) // Monday
            val rule = weeklyRule(setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))

            val occurrences = calculator.generateOccurrences(
                rule = rule,
                startDate = start,
                fromDate = start,
                toDate = start.plusDays(13)
            )

            assertThat(occurrences).containsExactly(
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 9),
                LocalDate.of(2026, 1, 12),
                LocalDate.of(2026, 1, 16)
            ).inOrder()
        }

        @Test
        fun `returns empty when range is inverted`() {
            val start = LocalDate.of(2026, 1, 1)

            val occurrences = calculator.generateOccurrences(
                rule = dailyRule(),
                startDate = start,
                fromDate = start.plusDays(5),
                toDate = start
            )

            assertThat(occurrences).isEmpty()
        }

        @Test
        fun `does not generate before start date`() {
            val start = LocalDate.of(2026, 1, 10)

            val occurrences = calculator.generateOccurrences(
                rule = dailyRule(),
                startDate = start,
                fromDate = LocalDate.of(2026, 1, 1),
                toDate = LocalDate.of(2026, 1, 12)
            )

            assertThat(occurrences).containsExactly(
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 1, 11),
                LocalDate.of(2026, 1, 12)
            ).inOrder()
        }
    }

    @Nested
    inner class RuleValidation {

        @Test
        fun `valid rules pass validation`() {
            assertThat(calculator.validateRecurrenceRule(dailyRule()).isSuccess).isTrue()
            assertThat(
                calculator.validateRecurrenceRule(weeklyRule(setOf(DayOfWeek.MONDAY))).isSuccess
            ).isTrue()
            assertThat(
                calculator.validateRecurrenceRule(monthlyRule(dayOfMonth = 28)).isSuccess
            ).isTrue()
            assertThat(
                calculator.validateRecurrenceRule(yearlyRule(month = 12, dayOfMonth = 31)).isSuccess
            ).isTrue()
        }
    }
}
