package com.letsgotoperfection.kino.feature.notifications.internal.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Broadcast receiver for handling notification actions
 */
class NotificationActionReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        try {
            when (intent.action) {
                ACTION_TASK_REMINDER -> {
                    val taskId = intent.getStringExtra(EXTRA_TASK_ID)
                    if (taskId != null) {
                        handleTaskReminder(context, taskId)
                    }
                }
                ACTION_SMART_SUGGESTION -> {
                    val suggestionId = intent.getStringExtra(EXTRA_SUGGESTION_ID)
                    if (suggestionId != null) {
                        handleSmartSuggestion(context, suggestionId)
                    }
                }
                ACTION_ACHIEVEMENT -> {
                    val achievementId = intent.getStringExtra(EXTRA_ACHIEVEMENT_ID)
                    if (achievementId != null) {
                        handleAchievement(context, achievementId)
                    }
                }
                else -> {
                    Log.w(TAG, "Unknown action: ${intent.action}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling notification action", e)
        }
    }
    
    /**
     * Handles task reminder notification action
     * Opens the app to the specific task detail screen
     *
     * @param context Application context
     * @param taskId ID of the task to open
     */
    private fun handleTaskReminder(context: Context, taskId: String) {
        Log.d(TAG, "Handling task reminder for task: $taskId")
        
        // Fixed: Implemented task reminder handling
        // Create intent to open task detail screen
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        launchIntent?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("task_id", taskId)
            putExtra("source", "notification_reminder")
            
            try {
                context.startActivity(this)
                Log.i(TAG, "Opened task $taskId from reminder notification")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open task from notification", e)
            }
        }
    }
    
    /**
     * Handles smart suggestion notification action
     * Opens the app to show the suggested action
     *
     * @param context Application context
     * @param suggestionId ID of the suggestion to display
     */
    private fun handleSmartSuggestion(context: Context, suggestionId: String) {
        Log.d(TAG, "Handling smart suggestion: $suggestionId")
        
        // Fixed: Implemented smart suggestion handling
        // Log the interaction for ML model improvement
        Log.i(TAG, "User interacted with smart suggestion: $suggestionId")
        
        // Open app to show suggestion details
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        launchIntent?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("suggestion_id", suggestionId)
            putExtra("source", "notification_suggestion")
            
            try {
                context.startActivity(this)
                Log.i(TAG, "Opened app for suggestion $suggestionId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open suggestion from notification", e)
            }
        }
    }
    
    /**
     * Handles achievement notification action
     * Opens the app to show achievement details and rewards
     *
     * @param context Application context
     * @param achievementId ID of the achievement unlocked
     */
    private fun handleAchievement(context: Context, achievementId: String) {
        Log.d(TAG, "Handling achievement: $achievementId")
        
        // Fixed: Implemented achievement handling
        // Celebrate the achievement
        Log.i(TAG, "User viewed achievement: $achievementId")
        
        // Open app to show achievement screen
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        launchIntent?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("achievement_id", achievementId)
            putExtra("source", "notification_achievement")
            
            try {
                context.startActivity(this)
                Log.i(TAG, "Opened achievement screen for $achievementId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open achievement from notification", e)
            }
        }
    }
    
    companion object {
        private const val TAG = "NotificationActionReceiver"
        
        const val ACTION_TASK_REMINDER = "com.letsgotoperfection.kino.ACTION_TASK_REMINDER"
        const val ACTION_SMART_SUGGESTION = "com.letsgotoperfection.kino.ACTION_SMART_SUGGESTION"
        const val ACTION_ACHIEVEMENT = "com.letsgotoperfection.kino.ACTION_ACHIEVEMENT"
        
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_SUGGESTION_ID = "suggestion_id"
        const val EXTRA_ACHIEVEMENT_ID = "achievement_id"
    }
}
