package com.letsgotoperfection.kino.feature.settings.internal.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun AboutSettingsSection() {
    Column {
        // App Version
        SettingsItem(
            title = "Version",
            subtitle = "1.0.0",
            onClick = { /* Show version info */ },
            showArrow = false
        )
        
        // Terms of Service
        SettingsItem(
            title = "Terms of Service",
            subtitle = "Read our terms and conditions",
            onClick = { /* Open terms */ },
            showArrow = true
        )
        
        // Open Source Licenses
        SettingsItem(
            title = "Open Source Licenses",
            subtitle = "View third-party library licenses",
            onClick = { /* Open licenses */ },
            showArrow = true
        )
        
        // Contact Support
        SettingsItem(
            title = "Contact Support",
            subtitle = "Get help or report issues",
            onClick = { /* Open support */ },
            showArrow = true
        )
        
        // Rate App
        SettingsItem(
            title = "Rate App",
            subtitle = "Rate us on the Play Store",
            onClick = { /* Open Play Store */ },
            showArrow = true
        )
    }
}
