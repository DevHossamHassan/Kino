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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.letsgotoperfection.kino.navigation.DeepLinkHandler
import com.letsgotoperfection.kino.navigation.KinoNavHost
import com.letsgotoperfection.kino.core.designsystem.SettingsAwareTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the Kino task management app
 * 
 * This is the entry point that sets up the navigation system
 * and provides the main UI structure with bottom navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /** Latest intent to process for deep links; updated by [onNewIntent]. */
    private var deepLinkIntent by mutableStateOf<Intent?>(null)

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

        deepLinkIntent = intent

        setContent {
            SettingsAwareTheme(
                darkTheme = isSystemInDarkTheme(),
                useDynamicColors = true
            ) {
                KinoApp(
                    deepLinkIntent = deepLinkIntent,
                    onDeepLinkConsumed = { deepLinkIntent = null }
                )
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
        // Handle deep links when the app is already running (e.g. notification taps)
        deepLinkIntent = intent
    }
}

/**
 * Main app composable that sets up navigation and UI structure.
 *
 * @param deepLinkIntent the intent carrying a pending deep link, if any
 * @param onDeepLinkConsumed invoked after the deep link has been handled so it is not replayed
 */
@Composable
fun KinoApp(
    deepLinkIntent: Intent?,
    onDeepLinkConsumed: () -> Unit
) {
    val navController = rememberNavController()

    // Navigate when a deep link arrives (app launch or notification tap while running)
    LaunchedEffect(deepLinkIntent) {
        deepLinkIntent?.let { intent ->
            DeepLinkHandler.handleDeepLink(intent, navController)
            onDeepLinkConsumed()
        }
    }

    // Single Scaffold with both bottomBar and content
    KinoNavHost(
        navController = navController
    )
}