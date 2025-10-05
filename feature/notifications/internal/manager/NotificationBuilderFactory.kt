package com.letsgotoperfection.kino.feature.notifications.internal.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.letsgotoperfection.kino.feature.notifications.R
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationAction
import com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationData
import com.letsgotoperfection.kino.feature.notifications.internal.receiver.NotificationActionReceiver
import com.letsgotoperfection.kino.feature.notifications.internal.receiver.NotificationDismissReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationBuilderFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun buildNotification(data: NotificationData): android.app.Notification {
        val channelId = data.channelId
        
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(data.title)
            .setContentText(data.message)
            .setPriority(data.priority.toAndroidPriority())
            .setCategory(data.category.toAndroidCategory())
            .setAutoCancel(data.autoCancel)
        
        // Deep link intent
        data.deepLink?.let { deepLink ->
            val intent = createDeepLinkIntent(deepLink)
            val pendingIntent = PendingIntent.getActivity(
                context,
                data.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingIntent)
        }
        
        // Big text style
        if (data.bigTextStyle) {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(data.message)
            )
        }
        
        // Large icon
        data.largeIcon?.let { iconUri ->
            val bitmap = loadBitmap(iconUri)
            bitmap?.let { builder.setLargeIcon(it) }
        }
        
        // Actions
        data.actions.forEach { action ->
            val actionIntent = createActionIntent(action, data)
            val actionPendingIntent = PendingIntent.getBroadcast(
                context,
                action.id.hashCode(),
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            builder.addAction(
                action.icon,
                action.title,
                actionPendingIntent
            )
        }
        
        // Group
        data.groupKey?.let { groupKey ->
            builder.setGroup(groupKey)
        }
        
        // Delete intent (track dismissals)
        val deleteIntent = createDismissIntent(data)
        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            data.id.hashCode(),
            deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.setDeleteIntent(deletePendingIntent)
        
        return builder.build()
    }
    
    /**
     * Build summary notification for grouped notifications
     */
    fun buildGroupSummary(
        groupKey: String,
        channelId: String,
        title: String,
        message: String
    ): android.app.Notification {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .build()
    }
    
    private fun createDeepLinkIntent(deepLink: String): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(deepLink)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
    
    private fun createActionIntent(
        action: NotificationAction,
        notificationData: NotificationData
    ): Intent {
        return Intent(context, NotificationActionReceiver::class.java).apply {
            this.action = "ACTION_${action.type.name}"
            putExtra("notification_id", notificationData.id)
            putExtra("action_id", action.id)
            putExtra("action_type", action.type.name)
            action.metadata.forEach { (key, value) ->
                putExtra("meta_$key", value)
            }
        }
    }
    
    private fun createDismissIntent(data: NotificationData): Intent {
        return Intent(context, NotificationDismissReceiver::class.java).apply {
            putExtra("notification_id", data.id)
            putExtra("category", data.category.name)
        }
    }
    
    private fun loadBitmap(uri: String): Bitmap? {
        return try {
            context.contentResolver.openInputStream(Uri.parse(uri))?.use { input ->
                BitmapFactory.decodeStream(input)
            }
        } catch (e: Exception) {
            Log.e("NotificationBuilder", "Failed to load bitmap", e)
            null
        }
    }
}
