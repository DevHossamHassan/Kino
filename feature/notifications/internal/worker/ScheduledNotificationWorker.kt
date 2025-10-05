package com.letsgotoperfection.kino.feature.notifications.internal.worker

import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationData
import com.letsgotoperfection.kino.feature.notifications.internal.domain.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltWorker
internal class ScheduledNotificationWorker @AssistedInject constructor(
    @Assisted appContext: android.content.Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationRepository: NotificationRepository
) : CoroutineWorker(appContext, workerParams) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    override suspend fun doWork(): Result {
        val notificationId = inputData.getString("notification_id") 
            ?: return Result.failure()
        
        val notificationDataJson = inputData.getString("notification_data") 
            ?: return Result.failure()
        
        return try {
            val notificationData = json.decodeFromString<NotificationData>(notificationDataJson)
            
            notificationRepository.sendNotification(notificationData).fold(
                onSuccess = {
                    notificationRepository.markAsDelivered(notificationId)
                    Result.success()
                },
                onFailure = { error ->
                    android.util.Log.e("ScheduledNotificationWorker", "Failed to send notification", error)
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("ScheduledNotificationWorker", "Error in worker", e)
            Result.failure()
        }
    }
}
