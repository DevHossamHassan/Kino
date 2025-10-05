package com.letsgotoperfection.kino

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Kino task management app
 * 
 * This class initializes Hilt dependency injection for the entire app.
 * All modules and dependencies are configured through Hilt.
 */
@HiltAndroidApp
class KinoApplication : Application()


