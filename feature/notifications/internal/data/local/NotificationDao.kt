package com.letsgotoperfection.kino.feature.notifications.internal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
internal interface NotificationDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)
    
    @Update
    suspend fun update(notification: NotificationEntity)
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getById(id: String): NotificationEntity?
    
    @Query("SELECT * FROM notifications WHERE isDelivered = 0 AND isCancelled = 0 ORDER BY createdAt DESC")
    fun getPendingNotifications(): Flow<List<NotificationEntity>>
    
    @Query("SELECT * FROM notifications WHERE scheduledTime IS NOT NULL AND isDelivered = 0 AND isCancelled = 0 ORDER BY scheduledTime ASC")
    fun getScheduledNotifications(): Flow<List<NotificationEntity>>
    
    @Query("SELECT * FROM notifications WHERE isDelivered = 1 ORDER BY deliveredAt DESC")
    fun getDeliveredNotifications(): Flow<List<NotificationEntity>>
    
    @Query("SELECT * FROM notifications WHERE isDismissed = 1 ORDER BY dismissedAt DESC")
    fun getDismissedNotifications(): Flow<List<NotificationEntity>>
    
    @Query("UPDATE notifications SET isDelivered = 1, deliveredAt = :deliveredAt WHERE id = :id")
    suspend fun markAsDelivered(id: String, deliveredAt: LocalDateTime = LocalDateTime.now())
    
    @Query("UPDATE notifications SET isCancelled = 1 WHERE id = :id")
    suspend fun markAsCancelled(id: String)
    
    @Query("UPDATE notifications SET isDismissed = 1, dismissedAt = :dismissedAt WHERE id = :id")
    suspend fun markAsDismissed(id: String, dismissedAt: LocalDateTime = LocalDateTime.now())
    
    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM notifications WHERE isDelivered = 1 AND deliveredAt < :cutoffDate")
    suspend fun deleteOldDeliveredNotifications(cutoffDate: LocalDateTime)
    
    @Query("DELETE FROM notifications WHERE isDismissed = 1 AND dismissedAt < :cutoffDate")
    suspend fun deleteOldDismissedNotifications(cutoffDate: LocalDateTime)
    
    @Query("SELECT COUNT(*) FROM notifications WHERE isDelivered = 0 AND isCancelled = 0")
    suspend fun getPendingCount(): Int
    
    @Query("SELECT COUNT(*) FROM notifications WHERE isDelivered = 1 AND deliveredAt >= :since")
    suspend fun getDeliveredCountSince(since: LocalDateTime): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE isDismissed = 1 AND dismissedAt >= :since")
    suspend fun getDismissedCountSince(since: LocalDateTime): Int

    @Query("SELECT category AS category, COUNT(*) AS count FROM notifications WHERE isDelivered = 1 GROUP BY category")
    suspend fun getDeliveredCountsByCategory(): List<CategoryCount>
}

internal data class CategoryCount(
    val category: String?,
    val count: Int
)
