package com.letsgotoperfection.kino.feature.notifications.internal.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.letsgotoperfection.kino.feature.notifications.internal.domain.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class NotificationDismissReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var notificationRepository: NotificationRepository
    
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getStringExtra("notification_id") ?: return
        val category = intent.getStringExtra("category") ?: return
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Mark as dismissed in database
                notificationRepository.markAsDismissed(notificationId)
                
                android.util.Log.d("NotificationDismiss", "Notification dismissed: $notificationId (category: $category)")
            } catch (e: Exception) {
                android.util.Log.e("NotificationDismiss", "Failed to mark notification as dismissed", e)
            }
        }
    }
}
