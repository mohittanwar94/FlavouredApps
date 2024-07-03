package com.qureka.skool.di

import com.qureka.skool.stopwatch.StopWatchManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideStopWatchManager(): StopWatchManager {
        return StopWatchManager()
    }
}
