package com.letsgotoperfection.kino.feature.settings.internal.presentation.ui

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.FontScale
import com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeMode
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsAction

@Composable
internal fun AppearanceSettingsSection(
    themeSettings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.ThemeSettings,
    onAction: (SettingsAction) -> Unit
) {
    Column {
        // Theme Mode
        SettingsDropdown(
            title = "Theme",
            subtitle = themeSettings.themeMode.name.lowercase().replaceFirstChar { it.uppercase() },
            options = ThemeMode.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } },
            selectedOption = themeSettings.themeMode.name.lowercase().replaceFirstChar { it.uppercase() },
            onOptionSelected = { selected ->
                val mode = ThemeMode.values().find { 
                    it.name.lowercase().replaceFirstChar { char -> char.uppercase() } == selected 
                } ?: ThemeMode.SYSTEM
                onAction(SettingsAction.UpdateThemeMode(mode))
            }
        )
        
        // Dynamic Colors (Material You - Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SettingsSwitch(
                title = "Dynamic Colors",
                subtitle = "Use colors from your wallpaper",
                checked = themeSettings.useDynamicColors,
                onCheckedChange = { enabled ->
                    onAction(SettingsAction.UpdateDynamicColors(enabled))
                }
            )
        }
        
        // Font Scale
        SettingsDropdown(
            title = "Font Size",
            subtitle = themeSettings.fontSize.name.lowercase().replaceFirstChar { it.uppercase() },
            options = FontScale.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } },
            selectedOption = themeSettings.fontSize.name.lowercase().replaceFirstChar { it.uppercase() },
            onOptionSelected = { selected ->
                val scale = FontScale.values().find { 
                    it.name.lowercase().replaceFirstChar { char -> char.uppercase() } == selected 
                } ?: FontScale.NORMAL
                onAction(SettingsAction.UpdateFontScale(scale))
            }
        )
    }
}

@Composable
internal fun SettingsSwitch(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Column(modifier = androidx.compose.ui.Modifier.weight(1f)) {
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
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
internal fun SettingsDropdown(
    title: String,
    subtitle: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        androidx.compose.foundation.layout.Row(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Column(modifier = androidx.compose.ui.Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    leadingIcon = if (option == selectedOption) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }
        }
    }
}
