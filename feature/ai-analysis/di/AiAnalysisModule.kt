package com.letsgotoperfection.kino.feature.ai_analysis.di

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.letsgotoperfection.kino.feature.ai_analysis.api.AiAnalysisApi
import com.letsgotoperfection.kino.feature.ai_analysis.internal.ai.gemini.GeminiTaskAnalyzer
import com.letsgotoperfection.kino.feature.ai_analysis.internal.ai.ondevice.OnDeviceTaskAnalyzer
import com.letsgotoperfection.kino.feature.ai_analysis.internal.data.repository.AiAnalysisApiImpl
import com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.analyzer.TaskAnalyzer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiAnalysisModule {
    
    @Provides
    @Singleton
    fun provideGeminiApiKey(): String {
        // In production, this should come from BuildConfig or secure storage
        return System.getenv("GEMINI_API_KEY") ?: ""
    }
    
    @Provides
    @Singleton
    fun provideGenerativeModel(apiKey: String): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 2048
            }
        )
    }
    
    @Provides
    @Singleton
    @CloudAi
    fun provideCloudTaskAnalyzer(
        generativeModel: GenerativeModel
    ): TaskAnalyzer {
        return GeminiTaskAnalyzer(generativeModel)
    }
    
    @Provides
    @Singleton
    @OnDeviceAi
    fun provideOnDeviceTaskAnalyzer(
        @ApplicationContext context: Context
    ): TaskAnalyzer {
        return OnDeviceTaskAnalyzer(context)
    }
    
    @Provides
    @Singleton
    fun provideTaskAnalyzer(
        @CloudAi cloudAnalyzer: TaskAnalyzer,
        @OnDeviceAi onDeviceAnalyzer: TaskAnalyzer,
        @ApplicationContext context: Context
    ): TaskAnalyzer {
        // Use cloud AI if available, fallback to on-device
        return HybridTaskAnalyzer(cloudAnalyzer, onDeviceAnalyzer)
    }
    
    @Provides
    @Singleton
    fun provideAiAnalysisApi(
        taskAnalyzer: TaskAnalyzer
    ): AiAnalysisApi {
        return AiAnalysisApiImpl(taskAnalyzer)
    }
}

/**
 * Hybrid task analyzer that tries cloud AI first, falls back to on-device
 */
@Singleton
internal class HybridTaskAnalyzer @Inject constructor(
    private val cloudAnalyzer: TaskAnalyzer,
    private val onDeviceAnalyzer: TaskAnalyzer
) : TaskAnalyzer {
    
    override suspend fun analyzeTask(
        task: com.letsgotoperfection.kino.core.model.Task,
        currentTime: java.time.LocalDateTime
    ): Result<com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.TaskAnalysis> {
        return if (cloudAnalyzer.isAvailable()) {
            cloudAnalyzer.analyzeTask(task, currentTime)
        } else {
            onDeviceAnalyzer.analyzeTask(task, currentTime)
        }
    }
    
    override suspend fun generateMotivationalMessage(
        context: com.letsgotoperfection.kino.feature.ai_analysis.internal.domain.model.MotivationContext
    ): Result<String> {
        return if (cloudAnalyzer.isAvailable()) {
            cloudAnalyzer.generateMotivationalMessage(context)
        } else {
            onDeviceAnalyzer.generateMotivationalMessage(context)
        }
    }
    
    override suspend fun isAvailable(): Boolean {
        return cloudAnalyzer.isAvailable() || onDeviceAnalyzer.isAvailable()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CloudAi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OnDeviceAi
