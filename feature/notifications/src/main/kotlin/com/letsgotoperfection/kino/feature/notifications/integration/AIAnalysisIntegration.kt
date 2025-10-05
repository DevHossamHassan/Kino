package com.letsgotoperfection.kino.feature.notifications.integration

import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import javax.inject.Inject

/**
 * Integration example for AI Analysis feature
 */
class AIAnalysisIntegration @Inject constructor(
    private val notificationApi: NotificationApi
) {
    
    /**
     * Send smart suggestion notification
     */
    suspend fun sendSmartSuggestion(
        taskId: String,
        suggestion: String
    ) {
        notificationApi.sendSmartSuggestion(
            taskId = taskId,
            suggestion = suggestion
        )
    }
    
    /**
     * Send productivity insight notification
     */
    suspend fun sendProductivityInsight(
        insight: String,
        category: String
    ) {
        notificationApi.sendNotification(
            title = "Productivity Insight",
            message = "$category: $insight",
            category = com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory.SMART_SUGGESTION,
            deepLink = "kino://insights"
        )
    }
    
    /**
     * Send task breakdown suggestion
     */
    suspend fun sendTaskBreakdownSuggestion(
        taskId: String,
        breakdownSuggestions: List<String>
    ) {
        val suggestionText = breakdownSuggestions.joinToString("\n• ", "• ")
        notificationApi.sendNotification(
            title = "Task Breakdown Suggestion",
            message = "Consider breaking down this task:\n$suggestionText",
            category = com.letsgotoperfection.kino.feature.notifications.internal.domain.model.NotificationCategory.SMART_SUGGESTION,
            deepLink = "kino://task/$taskId"
        )
    }
}
