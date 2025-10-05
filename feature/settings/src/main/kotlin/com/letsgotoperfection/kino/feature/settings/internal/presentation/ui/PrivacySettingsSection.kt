package com.letsgotoperfection.kino.feature.settings.internal.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.BackupFrequency
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsAction

@Composable
internal fun PrivacySettingsSection(
    privacySettings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.PrivacySettings,
    onAction: (SettingsAction) -> Unit
) {
    Column {
        // Analytics
        SettingsSwitch(
            title = "Analytics",
            subtitle = "Help improve the app by sharing anonymous usage data",
            checked = privacySettings.analyticsEnabled,
            onCheckedChange = { enabled ->
                onAction(SettingsAction.UpdateAnalytics(enabled))
            }
        )
        
        // Crash Reporting
        SettingsSwitch(
            title = "Crash Reporting",
            subtitle = "Automatically send crash reports to help fix bugs",
            checked = privacySettings.crashReportingEnabled,
            onCheckedChange = { enabled ->
                onAction(SettingsAction.UpdateCrashReporting(enabled))
            }
        )
        
        // Backup
        SettingsSwitch(
            title = "Data Backup",
            subtitle = "Automatically backup your data to the cloud",
            checked = privacySettings.backupEnabled,
            onCheckedChange = { enabled ->
                onAction(SettingsAction.UpdateBackupEnabled(enabled))
            }
        )
        
        if (privacySettings.backupEnabled) {
            // Backup Frequency
            SettingsDropdown(
                title = "Backup Frequency",
                subtitle = privacySettings.backupFrequency.name.lowercase().replaceFirstChar { it.uppercase() },
                options = BackupFrequency.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } },
                selectedOption = privacySettings.backupFrequency.name.lowercase().replaceFirstChar { it.uppercase() },
                onOptionSelected = { selected ->
                    val frequency = BackupFrequency.values().find { 
                        it.name.lowercase().replaceFirstChar { char -> char.uppercase() } == selected 
                    } ?: BackupFrequency.WEEKLY
                    onAction(SettingsAction.UpdateBackupFrequency(frequency))
                }
            )
        }
        
        // Privacy Policy Link
        SettingsItem(
            title = "Privacy Policy",
            subtitle = "Read our privacy policy",
            onClick = { /* Open privacy policy */ },
            showArrow = true
        )
        
        // Data Export
        SettingsItem(
            title = "Export Data",
            subtitle = "Download a copy of your data",
            onClick = { /* Export data */ },
            showArrow = true
        )
    }
}
