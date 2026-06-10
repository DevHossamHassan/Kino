package com.letsgotoperfection.kino

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.letsgotoperfection.kino.navigation.KinoNavHost
import com.letsgotoperfection.kino.core.designsystem.SettingsAwareTheme
import com.letsgotoperfection.kino.feature.settings.api.SettingsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main Activity for the Kino task management app
 * 
 * This is the entry point that sets up the navigation system
 * and provides the main UI structure with bottom navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsApi: SettingsApi
    
    // Permission request launcher for notifications
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission granted or denied
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Request notification permission for Android 13+
        requestNotificationPermission()
        
        setContent {
            SettingsAwareTheme(
                darkTheme = isSystemInDarkTheme(),
                useDynamicColors = true
            ) {
                KinoApp(settingsApi = settingsApi)
            }
        }
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep links when app is already running
        // This will be handled by the navigation system
    }
}

/**
 * Main app composable that sets up navigation and UI structure
 */
@Composable
fun KinoApp(
    settingsApi: SettingsApi
) {
    val navController = rememberNavController()
    
    // Handle deep links from notifications
    LaunchedEffect(Unit) {
        // This will be called when the app is launched from a notification
        // The deep link handling is done in the navigation graph
    }
    
    // Single Scaffold with both bottomBar and content
    KinoNavHost(
        navController = navController
    )
}