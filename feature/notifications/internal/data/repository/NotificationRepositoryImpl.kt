package com.letsgotoperfection.kino.feature.notifications.internal.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.letsgotoperfection.kino.feature.notifications.internal.data.local.NotificationDao
import com.letsgotoperfection.kino.feature.notifications.internal.data.local.NotificationEntity
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationData
import com.letsgotoperfection.kino.feature.notifications.internal.domain.repository.NotificationRepository
import com.letsgotoperfection.kino.feature.notifications.internal.domain.repository.NotificationStats
import com.letsgotoperfection.kino.feature.notifications.internal.worker.ScheduledNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
    private val notificationManager: androidx.core.app.NotificationManagerCompat,
    private val notificationBuilder: com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationBuilderFactory,
    private val permissionManager: com.letsgotoperfection.kino.feature.notifications.internal.manager.NotificationPermissionManager,
    @ApplicationContext private val context: Context
) : NotificationRepository {
    
    override suspend fun sendNotification(data: NotificationData): Result<Unit> = 
        withContext(Dispatchers.IO) {
            runCatching {
                // Check permission
                if (!permissionManager.hasNotificationPermission()) {
                    throw NotificationPermissionDeniedException()
                }
                
                // Build notification
                val notification = notificationBuilder.buildNotification(data)
                
                // Save to database
                notificationDao.insert(data.toEntity())
                
                // Show notification
                notificationManager.notify(
                    data.id.hashCode(),
                    notification
                )
                
                android.util.Log.d("NotificationRepository", "Notification sent: ${data.id}")
            }
        }
    
    override suspend fun scheduleNotification(
        data: NotificationData,
        scheduledTime: LocalDateTime
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val notificationWithTime = data.copy(scheduledTime = scheduledTime)
            
            // Save to database with scheduled time
            notificationDao.insert(notificationWithTime.toEntity())
            
            // Schedule with WorkManager
            scheduleWithWorkManager(notificationWithTime)
            
            android.util.Log.d("NotificationRepository", "Notification scheduled: ${data.id} at $scheduledTime")
        }
    }
    
    override suspend fun cancelNotification(notificationId: String): Result<Unit> = 
        withContext(Dispatchers.IO) {
            runCatching {
                // Cancel displayed notification
                notificationManager.cancel(notificationId.hashCode())
                
                // Mark as cancelled in database
                notificationDao.markAsCancelled(notificationId)
                
                // Cancel scheduled work
                cancelScheduledWork(notificationId)
            }
        }
    
    override fun getScheduledNotifications(): Flow<List<NotificationData>> {
        return notificationDao.getScheduledNotifications()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }
    
    override suspend fun markAsDelivered(notificationId: String) {
        withContext(Dispatchers.IO) {
            notificationDao.markAsDelivered(notificationId)
        }
    }
    
    override suspend fun markAsDismissed(notificationId: String) {
        withContext(Dispatchers.IO) {
            notificationDao.markAsDismissed(notificationId)
        }
    }
    
    override suspend fun getNotificationStats(): NotificationStats = withContext(Dispatchers.IO) {
        val pendingCount = notificationDao.getPendingCount()
        val today = LocalDateTime.now().toLocalDate().atStartOfDay()

        val deliveredToday = notificationDao.getDeliveredCountSince(today)
        val dismissedToday = notificationDao.getDismissedCountSince(today)
        val categoryStats = notificationDao.getDeliveredCountsByCategory()
            .associate { count ->
                val key = normalizeCategory(count.category)
                key to count.count
            }

        NotificationStats(
            pendingCount = pendingCount,
            deliveredToday = deliveredToday,
            dismissedToday = dismissedToday,
            categoryStats = categoryStats
        )
    }
    
    private fun scheduleWithWorkManager(data: NotificationData) {
        val delay = Duration.between(
            LocalDateTime.now(),
            data.scheduledTime ?: return
        )
        
        if (delay.isNegative) return
        
        val workRequest = OneTimeWorkRequestBuilder<ScheduledNotificationWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(
                androidx.work.workDataOf(
                    "notification_id" to data.id,
                    "notification_data" to serializeNotificationData(data)
                )
            )
            .addTag("notification_${data.id}")
            .build()
        
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "notification_${data.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }
    
    private fun cancelScheduledWork(notificationId: String) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("notification_$notificationId")
    }
    
    private fun serializeNotificationData(data: NotificationData): String {
        return kotlinx.serialization.json.Json.encodeToString(data)
    }

    private fun normalizeCategory(category: String?): String {
        return category?.takeIf { it.isNotBlank() } ?: "uncategorized"
    }
}

internal class NotificationPermissionDeniedException : 
    Exception("Notification permission not granted")

// Extension functions for mapping between domain and data models
private fun NotificationData.toEntity(): NotificationEntity {
    return NotificationEntity(
        id = id,
        channelId = channelId,
        title = title,
        message = message,
        priority = priority,
        category = category,
        deepLink = deepLink,
        largeIcon = largeIcon,
        bigTextStyle = bigTextStyle,
        groupKey = groupKey,
        autoCancel = autoCancel,
        scheduledTime = scheduledTime,
        metadata = metadata
    )
}

private fun NotificationEntity.toDomain(): NotificationData {
    return NotificationData(
        id = id,
        channelId = channelId,
        title = title,
        message = message,
        priority = priority,
        category = category,
        deepLink = deepLink,
        largeIcon = largeIcon,
        bigTextStyle = bigTextStyle,
        groupKey = groupKey,
        autoCancel = autoCancel,
        scheduledTime = scheduledTime,
        metadata = metadata
    )
}
