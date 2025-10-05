package com.letsgotoperfection.kino.feature.settings.internal.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsAction

@Composable
internal fun GamificationSettingsSection(
    gamificationSettings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.GamificationSettings,
    onAction: (SettingsAction) -> Unit
) {
    Column {
        // Enable Gamification
        SettingsSwitch(
            title = "Enable Gamification",
            subtitle = "Turn productivity into a game with points, streaks, and achievements",
            checked = gamificationSettings.enabled,
            onCheckedChange = { enabled ->
                onAction(SettingsAction.UpdateGamificationEnabled(enabled))
            }
        )
        
        if (gamificationSettings.enabled) {
            // Show Streaks
            SettingsSwitch(
                title = "Show Streaks",
                subtitle = "Display your daily task completion streaks",
                checked = gamificationSettings.showStreaks,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateShowStreaks(enabled))
                }
            )
            
            // Show Achievements
            SettingsSwitch(
                title = "Show Achievements",
                subtitle = "Display unlocked achievements and badges",
                checked = gamificationSettings.showAchievements,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateShowAchievements(enabled))
                }
            )
            
            // Show Progress Celebrations
            SettingsSwitch(
                title = "Progress Celebrations",
                subtitle = "Show animations and celebrations for milestones",
                checked = gamificationSettings.showProgressCelebrations,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateShowCelebrations(enabled))
                }
            )
        }
    }
}
