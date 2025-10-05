package com.letsgotoperfection.kino.feature.notifications.internal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationPriority
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

@Entity(tableName = "notifications")
@TypeConverters(NotificationConverters::class)
internal data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val channelId: String,
    val title: String,
    val message: String,
    val priority: NotificationPriority,
    val category: NotificationCategory,
    val deepLink: String?,
    val largeIcon: String?,
    val bigTextStyle: Boolean,
    val groupKey: String?,
    val autoCancel: Boolean,
    val scheduledTime: LocalDateTime?,
    val metadata: Map<String, String>,
    val isDelivered: Boolean = false,
    val isCancelled: Boolean = false,
    val isDismissed: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deliveredAt: LocalDateTime? = null,
    val dismissedAt: LocalDateTime? = null
)

internal class NotificationConverters {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @TypeConverter
    fun fromPriority(priority: NotificationPriority): String = priority.name
    
    @TypeConverter
    fun toPriority(priority: String): NotificationPriority = NotificationPriority.valueOf(priority)
    
    @TypeConverter
    fun fromCategory(category: NotificationCategory): String = category.name
    
    @TypeConverter
    fun toCategory(category: String): NotificationCategory = NotificationCategory.valueOf(category)
    
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? = dateTime?.toString()
    
    @TypeConverter
    fun toLocalDateTime(dateTime: String?): LocalDateTime? = dateTime?.let { LocalDateTime.parse(it) }
    
    @TypeConverter
    fun fromStringMap(map: Map<String, String>): String = json.encodeToString(map)
    
    @TypeConverter
    fun toStringMap(jsonString: String): Map<String, String> = json.decodeFromString(jsonString)
}
