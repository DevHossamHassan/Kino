package com.letsgotoperfection.kino.feature.recurringtasks.internal.data.mapper

import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask

/**
 * Mapper between internal and public RecurringTask models
 * Since we're using the same types for both internal and public APIs,
 * these are just identity functions.
 */
internal object RecurringTaskApiMapper {
    
    fun RecurringTask.toApi(): RecurringTask {
        return this
    }

    fun RecurringTask.toInternal(): RecurringTask {
        return this
    }

    fun RecurrenceRule.toApi(): RecurrenceRule {
        return this
    }

    fun RecurrenceRule.toInternal(): RecurrenceRule {
        return this
    }

    fun RecurrenceFrequency.toApi(): RecurrenceFrequency {
        return this
    }

    fun RecurrenceFrequency.toInternal(): RecurrenceFrequency {
        return this
    }
}