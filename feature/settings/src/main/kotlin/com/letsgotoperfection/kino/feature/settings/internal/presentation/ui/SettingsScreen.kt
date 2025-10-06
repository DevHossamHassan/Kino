package com.letsgotoperfection.kino.feature.settings.internal.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsAction
import com.letsgotoperfection.kino.feature.settings.internal.presentation.state.SettingsUiEvent
import com.letsgotoperfection.kino.feature.settings.internal.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SettingsUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is SettingsUiEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            SettingsTopBar(onNavigateBack = onNavigateBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.error != null -> ErrorScreen(
                message = uiState.error ?: "Unknown error",
                onRetry = { /* reload settings */ }
            )
            uiState.settings != null -> {
                SettingsContent(
                    settings = uiState.settings!!,
                    onAction = viewModel::onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
    
    if (uiState.showResetDialog) {
        ResetSettingsDialog(
            onConfirm = { viewModel.onAction(SettingsAction.ResetToDefaults) },
            onDismiss = { viewModel.onAction(SettingsAction.HideResetDialog) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { 
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = "Error loading settings",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        androidx.compose.material3.Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun SettingsContent(
    settings: com.letsgotoperfection.kino.feature.settings.internal.domain.model.AppSettings,
    onAction: (SettingsAction) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Appearance & Language Section
        item {
            SettingsCard(
                title = "Appearance & Language",
                icon = Icons.Default.Palette
            ) {
                AppearanceSettingsSection(
                    themeSettings = settings.theme,
                    onAction = onAction
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LanguageSettingsSection(
                    currentLanguage = settings.general.language,
                    onAction = onAction
                )
            }
        }
        
        // Notifications Section
        item {
            SettingsCard(
                title = "Notifications",
                icon = Icons.Default.Notifications
            ) {
                NotificationSettingsSection(
                    notificationSettings = settings.notifications,
                    onAction = onAction
                )
            }
        }
        
        // AI & Smart Features Section
        item {
            SettingsCard(
                title = "AI & Smart Features",
                icon = Icons.Default.SmartToy
            ) {
                AiSettingsSection(
                    aiSettings = settings.ai,
                    onAction = onAction
                )
            }
        }
        
        // Gamification Section
        item {
            SettingsCard(
                title = "Gamification",
                icon = Icons.Default.EmojiEvents
            ) {
                GamificationSettingsSection(
                    gamificationSettings = settings.gamification,
                    onAction = onAction
                )
            }
        }
        
        // Privacy Section
        item {
            SettingsCard(
                title = "Privacy & Data",
                icon = Icons.Default.PrivacyTip
            ) {
                PrivacySettingsSection(
                    privacySettings = settings.privacy,
                    onAction = onAction
                )
            }
        }
        
        // About Section
        item {
            SettingsCard(
                title = "About",
                icon = Icons.Default.Info
            ) {
                AboutSettingsSection()
            }
        }
        
        // Reset Settings
        item {
            Spacer(Modifier.height(24.dp))
            androidx.compose.material3.TextButton(
                onClick = { onAction(SettingsAction.ShowResetDialog) },
                modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp)
            ) {
                Text("Reset to Defaults")
            }
        }
    }
}

/**
 * Modern Material 3 Card for Settings Sections
 */
@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Card Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Card Content
            content()
        }
    }
}

/**
 * Language Settings Section
 */
@Composable
private fun LanguageSettingsSection(
    currentLanguage: String,
    onAction: (SettingsAction) -> Unit
) {
    val languages = listOf(
        "en" to "English",
        "ar" to "العربية",
        "es" to "Español",
        "fr" to "Français",
        "de" to "Deutsch",
        "it" to "Italiano",
        "pt" to "Português",
        "ru" to "Русский",
        "zh" to "中文",
        "ja" to "日本語",
        "ko" to "한국어"
    )
    
    val currentLanguageName = languages.find { it.first == currentLanguage }?.second ?: "English"
    
    SettingsDropdown(
        title = "Language",
        subtitle = currentLanguageName,
        options = languages.map { it.second },
        selectedOption = currentLanguageName,
        onOptionSelected = { selected ->
            val languageCode = languages.find { it.second == selected }?.first ?: "en"
            onAction(SettingsAction.UpdateLanguage(languageCode))
        }
    )
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}
