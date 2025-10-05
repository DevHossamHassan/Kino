package com.letsgotoperfection.kino.feature.settings.internal.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationFrequency
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsAction

@Composable
internal fun NotificationSettingsSection(
    notificationSettings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.NotificationSettings,
    onAction: (SettingsAction) -> Unit
) {
    Column {
        // Master notification switch
        SettingsSwitch(
            title = "Enable Notifications",
            subtitle = if (notificationSettings.enabled) {
                "Notifications enabled"
            } else {
                "Notifications disabled"
            },
            checked = notificationSettings.enabled,
            onCheckedChange = { enabled ->
                onAction(SettingsAction.UpdateNotificationEnabled(enabled))
            }
        )
        
        if (notificationSettings.enabled) {
            // Task Reminders
            SettingsSwitch(
                title = "Task Reminders",
                subtitle = "Get reminded about upcoming tasks",
                checked = notificationSettings.taskReminders,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateTaskReminders(enabled))
                }
            )
            
            // Smart Suggestions
            SettingsSwitch(
                title = "Smart Suggestions",
                subtitle = "Get AI-powered task suggestions",
                checked = notificationSettings.smartSuggestions,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateSmartSuggestions(enabled))
                }
            )
            
            // Achievements
            SettingsSwitch(
                title = "Achievements",
                subtitle = "Get notified about achievements and streaks",
                checked = notificationSettings.achievements,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateAchievements(enabled))
                }
            )
            
            // Note Reminders
            SettingsSwitch(
                title = "Note Reminders",
                subtitle = "Get reminded about important notes",
                checked = notificationSettings.noteReminders,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateNoteReminders(enabled))
                }
            )
            
            // Notification Frequency
            SettingsDropdown(
                title = "Notification Frequency",
                subtitle = notificationSettings.notificationFrequency.name.lowercase().replaceFirstChar { it.uppercase() },
                options = NotificationFrequency.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } },
                selectedOption = notificationSettings.notificationFrequency.name.lowercase().replaceFirstChar { it.uppercase() },
                onOptionSelected = { selected ->
                    val frequency = NotificationFrequency.values().find { 
                        it.name.lowercase().replaceFirstChar { char -> char.uppercase() } == selected 
                    } ?: NotificationFrequency.MEDIUM
                    onAction(SettingsAction.UpdateNotificationFrequency(frequency))
                }
            )
            
            // Quiet Hours
            SettingsSwitch(
                title = "Quiet Hours",
                subtitle = if (notificationSettings.quietHoursEnabled) {
                    "${notificationSettings.quietHoursStart} - ${notificationSettings.quietHoursEnd}"
                } else {
                    "Disabled"
                },
                checked = notificationSettings.quietHoursEnabled,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateQuietHours(enabled, null, null))
                }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Notification Settings Link
            SettingsItem(
                title = "Notification Settings",
                subtitle = "Manage system notification settings",
                onClick = { onAction(SettingsAction.OpenNotificationSettings) },
                showArrow = true
            )
            
            // Channel Settings
            SettingsItem(
                title = "Notification Channels",
                subtitle = "Customize notification types",
                onClick = { onAction(SettingsAction.OpenChannelSettings("task_reminders")) },
                showArrow = true
            )
        }
    }
}

@Composable
internal fun SettingsItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    showArrow: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (showArrow) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
