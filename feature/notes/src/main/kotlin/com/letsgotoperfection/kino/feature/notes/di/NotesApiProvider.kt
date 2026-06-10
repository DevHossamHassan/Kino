package com.letsgotoperfection.kino.feature.notes.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.letsgotoperfection.kino.feature.notes.api.NotesApi
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Entry point for accessing NotesApi from Compose
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NotesApiEntryPoint {
    fun notesApi(): NotesApi
}

/**
 * Composable function to get NotesApi using Hilt
 */
@Composable
fun rememberNotesApi(): NotesApi? {
    val context = LocalContext.current
    return remember {
        try {
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                NotesApiEntryPoint::class.java
            ).notesApi()
        } catch (e: Exception) {
            null
        }
    }
}




