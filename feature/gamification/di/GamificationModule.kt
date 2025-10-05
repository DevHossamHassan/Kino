package com.letsgotoperfection.kino.feature.gamification.di

import android.content.Context
import androidx.work.WorkManager
import com.letsgotoperfection.kino.feature.gamification.api.GamificationApi
import com.letsgotoperfection.kino.feature.gamification.internal.data.repository.GamificationApiImpl
import com.letsgotoperfection.kino.feature.gamification.internal.data.repository.AchievementTracker
import com.letsgotoperfection.kino.feature.gamification.internal.data.repository.StreakManager
import com.letsgotoperfection.kino.feature.notifications.api.NotificationApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GamificationModule {
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideStreakManager(): StreakManager {
        return StreakManager()
    }
    
    @Provides
    @Singleton
    fun provideAchievementTracker(): AchievementTracker {
        return AchievementTracker()
    }
    
    @Provides
    @Singleton
    fun provideGamificationApi(
        notificationApi: NotificationApi,
        streakManager: StreakManager,
        achievementTracker: AchievementTracker
    ): GamificationApi {
        return GamificationApiImpl(
            notificationApi = notificationApi,
            streakManager = streakManager,
            achievementTracker = achievementTracker
        )
    }
}
