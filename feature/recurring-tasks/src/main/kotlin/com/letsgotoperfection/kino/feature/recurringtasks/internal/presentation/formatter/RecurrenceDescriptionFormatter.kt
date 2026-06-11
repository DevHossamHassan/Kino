package com.letsgotoperfection.kino.feature.recurringtasks.internal.presentation.formatter

import android.content.Context
import com.letsgotoperfection.kino.feature.recurringtasks.R
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import com.letsgotoperfection.kino.feature.recurringtasks.internal.util.formatForDevice
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds localized, human-readable descriptions of recurrence rules.
 *
 * Day and month names come from the current locale; times respect the
 * device's 12/24-hour preference.
 */
@Singleton
class RecurrenceDescriptionFormatter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun format(rule: RecurrenceRule): String {
        val time = rule.timeOfDay.formatForDevice(context)
        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> {
                if (rule.interval == 1) {
                    context.getString(R.string.recurrence_daily, time)
                } else {
                    context.getString(R.string.recurrence_every_n_days, rule.interval, time)
                }
            }
            RecurrenceFrequency.WEEKLY -> {
                val days = rule.daysOfWeek
                    .sorted()
                    .joinToString(separator = context.getString(R.string.list_separator)) {
                        it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    }
                if (rule.interval == 1) {
                    context.getString(R.string.recurrence_weekly, days, time)
                } else {
                    context.getString(R.string.recurrence_every_n_weeks, rule.interval, days, time)
                }
            }
            RecurrenceFrequency.MONTHLY -> {
                val day = rule.dayOfMonth ?: 1
                if (rule.interval == 1) {
                    context.getString(R.string.recurrence_monthly, day, time)
                } else {
                    context.getString(R.string.recurrence_every_n_months, rule.interval, day, time)
                }
            }
            RecurrenceFrequency.YEARLY -> {
                val month = Month.of(rule.monthOfYear ?: 1)
                    .getDisplayName(TextStyle.FULL, Locale.getDefault())
                val day = rule.dayOfMonth ?: 1
                if (rule.interval == 1) {
                    context.getString(R.string.recurrence_yearly, month, day, time)
                } else {
                    context.getString(R.string.recurrence_every_n_years, rule.interval, month, day, time)
                }
            }
        }
    }
}
