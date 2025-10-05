package com.letsgotoperfection.kino.feature.recurringtasks.internal.data.mapper

import com.letsgotoperfection.kino.core.database.entity.RecurringTaskEntity
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.core.model.Priority
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceFrequency
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurrenceRule
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.model.RecurringTask
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Mapper functions for converting between RecurringTaskEntity and RecurringTask domain models
 */

fun RecurringTaskEntity.toDomain(labels: List<Label> = emptyList()): RecurringTask {
    return RecurringTask(
        id = id,
        title = title,
        description = description,
        section = TaskSection.valueOf(section.uppercase()),
        priority = Priority.valueOf(priority.uppercase()),
        labels = labels,
        recurrenceRule = RecurrenceRule(
            frequency = RecurrenceFrequency.valueOf(frequency.uppercase()),
            interval = interval,
            daysOfWeek = parseDaysOfWeek(daysOfWeek),
            dayOfMonth = dayOfMonth,
            monthOfYear = monthOfYear,
            timeOfDay = LocalTime.parse(timeOfDay)
        ),
        startDate = LocalDate.ofEpochDay(startDate),
        endDate = endDate?.let { LocalDate.ofEpochDay(it) },
        isActive = isActive,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()),
        updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault()),
        lastGeneratedDate = lastGeneratedDate?.let { LocalDate.ofEpochDay(it) }
    )
}

fun RecurringTask.toEntity(): RecurringTaskEntity {
    return RecurringTaskEntity(
        id = id,
        title = title,
        description = description,
        section = section.name.lowercase(),
        priority = priority.name.lowercase(),
        frequency = recurrenceRule.frequency.name.lowercase(),
        interval = recurrenceRule.interval,
        daysOfWeek = serializeDaysOfWeek(recurrenceRule.daysOfWeek),
        dayOfMonth = recurrenceRule.dayOfMonth,
        monthOfYear = recurrenceRule.monthOfYear,
        timeOfDay = recurrenceRule.timeOfDay.toString(),
        startDate = startDate.toEpochDay(),
        endDate = endDate?.toEpochDay(),
        isActive = isActive,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        lastGeneratedDate = lastGeneratedDate?.toEpochDay()
    )
}

private fun parseDaysOfWeek(daysOfWeekJson: String): Set<DayOfWeek> {
    return try {
        val dayNumbers = Json.decodeFromString<List<Int>>(daysOfWeekJson)
        dayNumbers.map { DayOfWeek.of(it) }.toSet()
    } catch (e: Exception) {
        emptySet()
    }
}

private fun serializeDaysOfWeek(daysOfWeek: Set<DayOfWeek>): String {
    return try {
        val dayNumbers = daysOfWeek.map { it.value }
        Json.encodeToString(dayNumbers)
    } catch (e: Exception) {
        "[]"
    }
}
