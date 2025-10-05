package com.letsgotoperfection.kino

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.letsgotoperfection.kino.navigation.AppNavHost
import com.letsgotoperfection.kino.ui.BottomNavigationBar
import com.letsgotoperfection.kino.core.designsystem.KinoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the Kino task management app
 * 
 * This is the entry point that sets up the navigation system
 * and provides the main UI structure with bottom navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KinoTheme {
                KinoApp()
            }
        }
    }
}

/**
 * Main app composable that sets up navigation and UI structure
 */
@Composable
fun KinoApp() {
    val navController = rememberNavController()
    
    // Handle deep links from notifications
    LaunchedEffect(Unit) {
        // This will be called when the app is launched from a notification
        // The deep link handling is done in the navigation graph
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}