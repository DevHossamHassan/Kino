package com.letsgotoperfection.kino

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.work.Configuration
import com.letsgotoperfection.kino.feature.notifications.api.NotificationInitializer
import javax.inject.Inject

/**
 * Application class for the Kino task management app
 *
 * This class initializes Hilt dependency injection for the entire app.
 * All modules and dependencies are configured through Hilt.
 */
@HiltAndroidApp
class KinoApplication : Application(), Configuration.Provider {

    @Inject
    override lateinit var workManagerConfiguration: Configuration

    @Inject
    lateinit var notificationInitializer: NotificationInitializer

    override fun onCreate() {
        super.onCreate()

        // Initialize notification system (lazy initialization)
        notificationInitializer.initialize(this)
    }
}

