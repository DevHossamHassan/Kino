package com.letsgotoperfection.kino.feature.media.internal.data.mapper

import android.net.Uri
import com.letsgotoperfection.kino.core.database.entity.MediaEntity
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal fun MediaEntity.toDomain(): Media {
    return Media(
        id = id,
        uri = Uri.parse(uri),
        filename = filename,
        mimeType = mimeType,
        size = size,
        dateAdded = epochMillisToLocalDateTime(dateAdded),
        dateModified = epochMillisToLocalDateTime(dateModified),
        width = width,
        height = height,
        duration = duration,
        thumbnailUri = thumbnailUri?.let { Uri.parse(it) },
        sourceType = MediaSourceType.valueOf(sourceType),
        sourceId = sourceId
    )
}

internal fun Media.toEntity(): MediaEntity {
    return MediaEntity(
        id = id,
        uri = uri.toString(),
        filename = filename,
        mimeType = mimeType,
        size = size,
        dateAdded = localDateTimeToEpochMillis(dateAdded),
        dateModified = localDateTimeToEpochMillis(dateModified),
        width = width,
        height = height,
        duration = duration,
        thumbnailUri = thumbnailUri?.toString(),
        sourceType = sourceType.name,
        sourceId = sourceId
    )
}

private fun epochMillisToLocalDateTime(millis: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())

private fun localDateTimeToEpochMillis(dateTime: LocalDateTime): Long =
    dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
