package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model

enum class RecurrenceFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY;

    fun getDisplayName(): String = when (this) {
        DAILY -> "Daily"
        WEEKLY -> "Weekly"
        MONTHLY -> "Monthly"
        YEARLY -> "Yearly"
    }
}

