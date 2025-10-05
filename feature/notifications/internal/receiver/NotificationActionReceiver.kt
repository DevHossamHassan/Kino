package com.letsgotoperfection.kino.feature.notifications.internal.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.ActionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class NotificationActionReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var eventBus: com.letsgotoperfection.kino.core.common.event.EventBus
    
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getStringExtra("notification_id") ?: return
        val actionType = intent.getStringExtra("action_type") ?: return
        
        when (ActionType.valueOf(actionType)) {
            ActionType.COMPLETE_TASK -> handleCompleteTask(intent, context)
            ActionType.COMPLETE_MICRO_TASK -> handleCompleteMicroTask(intent, context)
            ActionType.SNOOZE -> handleSnooze(intent, context)
            ActionType.MARK_AS_DONE -> handleMarkAsDone(intent, context)
            ActionType.OPEN_NOTE -> handleOpenNote(intent, context)
            ActionType.DISMISS -> handleDismiss(intent, context)
        }
        
        // Cancel notification
        NotificationManagerCompat.from(context).cancel(notificationId.hashCode())
    }
    
    private fun handleCompleteTask(intent: Intent, context: Context) {
        val taskId = intent.getStringExtra("meta_taskId") ?: return
        
        // Use coroutine scope from application
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Emit event for task completion
                eventBus.emit(com.letsgotoperfection.kino.core.common.event.AppEvent.TaskCompleted(taskId))
                android.util.Log.d("NotificationAction", "Task completed: $taskId")
            } catch (e: Exception) {
                android.util.Log.e("NotificationAction", "Failed to complete task", e)
            }
        }
    }
    
    private fun handleCompleteMicroTask(intent: Intent, context: Context) {
        val microTaskId = intent.getStringExtra("meta_microTaskId") ?: return
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Emit event for micro-task completion
                eventBus.emit(com.letsgotoperfection.kino.core.common.event.AppEvent.MicroTaskCompleted(microTaskId))
                android.util.Log.d("NotificationAction", "Micro-task completed: $microTaskId")
            } catch (e: Exception) {
                android.util.Log.e("NotificationAction", "Failed to complete micro-task", e)
            }
        }
    }
    
    private fun handleSnooze(intent: Intent, context: Context) {
        val notificationId = intent.getStringExtra("notification_id") ?: return
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Emit event for snooze action
                eventBus.emit(com.letsgotoperfection.kino.core.common.event.AppEvent.NotificationSnoozed(notificationId))
                android.util.Log.d("NotificationAction", "Notification snoozed: $notificationId")
            } catch (e: Exception) {
                android.util.Log.e("NotificationAction", "Failed to snooze notification", e)
            }
        }
    }
    
    private fun handleMarkAsDone(intent: Intent, context: Context) {
        val itemId = intent.getStringExtra("meta_itemId") ?: return
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Emit event for mark as done action
                eventBus.emit(com.letsgotoperfection.kino.core.common.event.AppEvent.ItemMarkedAsDone(itemId))
                android.util.Log.d("NotificationAction", "Item marked as done: $itemId")
            } catch (e: Exception) {
                android.util.Log.e("NotificationAction", "Failed to mark item as done", e)
            }
        }
    }
    
    private fun handleOpenNote(intent: Intent, context: Context) {
        val noteId = intent.getStringExtra("meta_noteId") ?: return
        
        try {
            val deepLinkIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("kino://note/$noteId")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(deepLinkIntent)
            android.util.Log.d("NotificationAction", "Opened note: $noteId")
        } catch (e: Exception) {
            android.util.Log.e("NotificationAction", "Failed to open note", e)
        }
    }
    
    private fun handleDismiss(intent: Intent, context: Context) {
        val notificationId = intent.getStringExtra("notification_id") ?: return
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Emit event for dismiss action
                eventBus.emit(com.letsgotoperfection.kino.core.common.event.AppEvent.NotificationDismissed(notificationId))
                android.util.Log.d("NotificationAction", "Notification dismissed: $notificationId")
            } catch (e: Exception) {
                android.util.Log.e("NotificationAction", "Failed to dismiss notification", e)
            }
        }
    }
}
