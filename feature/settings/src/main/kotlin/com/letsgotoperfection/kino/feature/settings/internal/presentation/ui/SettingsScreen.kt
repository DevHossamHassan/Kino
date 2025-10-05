package com.letsgotoperfection.kino.feature.settings.internal.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
                is SettingsUiEvent.NavigateBack -> {
                    onNavigateBack()
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
        title = { Text("Settings") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        }
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
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // Appearance Section
        item {
            SettingsSectionHeader(title = "Appearance")
        }
        
        item {
            AppearanceSettingsSection(
                themeSettings = settings.theme,
                onAction = onAction
            )
        }
        
        // Notifications Section
        item {
            SettingsSectionHeader(title = "Notifications")
        }
        
        item {
            NotificationSettingsSection(
                notificationSettings = settings.notifications,
                onAction = onAction
            )
        }
        
        // AI & Smart Features Section
        item {
            SettingsSectionHeader(title = "AI & Smart Features")
        }
        
        item {
            AiSettingsSection(
                aiSettings = settings.ai,
                onAction = onAction
            )
        }
        
        // Gamification Section
        item {
            SettingsSectionHeader(title = "Gamification")
        }
        
        item {
            GamificationSettingsSection(
                gamificationSettings = settings.gamification,
                onAction = onAction
            )
        }
        
        // Privacy Section
        item {
            SettingsSectionHeader(title = "Privacy")
        }
        
        item {
            PrivacySettingsSection(
                privacySettings = settings.privacy,
                onAction = onAction
            )
        }
        
        // About Section
        item {
            SettingsSectionHeader(title = "About")
        }
        
        item {
            AboutSettingsSection()
        }
        
        // Reset Settings
        item {
            Spacer(Modifier.height(24.dp))
            androidx.compose.material3.TextButton(
                onClick = { onAction(SettingsAction.ShowResetDialog) },
                modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Reset to Defaults",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
}
