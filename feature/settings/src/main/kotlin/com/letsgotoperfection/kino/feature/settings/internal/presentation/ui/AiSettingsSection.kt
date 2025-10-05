package com.letsgotoperfection.kino.feature.settings.internal.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsAction

@Composable
internal fun AiSettingsSection(
    aiSettings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.AiSettings,
    onAction: (SettingsAction) -> Unit
) {
    Column {
        // Enable AI Analysis
        SettingsSwitch(
            title = "Enable AI Analysis",
            subtitle = "Use AI to analyze and improve your tasks",
            checked = aiSettings.enableAiAnalysis,
            onCheckedChange = { enabled ->
                onAction(SettingsAction.UpdateAiEnabled(enabled))
            }
        )
        
        if (aiSettings.enableAiAnalysis) {
            // Use Cloud AI
            SettingsSwitch(
                title = "Use Cloud AI",
                subtitle = "Use cloud-based AI for better performance (requires internet)",
                checked = aiSettings.useCloudAi,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateCloudAi(enabled))
                }
            )
            
            // Auto Analyze Tasks
            SettingsSwitch(
                title = "Auto Analyze Tasks",
                subtitle = "Automatically analyze new tasks for insights",
                checked = aiSettings.autoAnalyzeTasks,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateAutoAnalyzeTasks(enabled))
                }
            )
            
            // Smart Task Breakdown
            SettingsSwitch(
                title = "Smart Task Breakdown",
                subtitle = "Automatically break down complex tasks into smaller steps",
                checked = aiSettings.smartTaskBreakdown,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateSmartTaskBreakdown(enabled))
                }
            )
        }
    }
}
