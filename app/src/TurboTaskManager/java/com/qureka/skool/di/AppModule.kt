package com.qureka.skool.di

import android.content.Context
import androidx.room.Room
import com.qureka.skool.data.datasource.TaskDataSource
import com.qureka.skool.data.repo.TaskRepository
import com.qureka.skool.room.Database
import com.qureka.skool.room.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideTaskDataSource(taskDao: TaskDao): TaskDataSource {
        return TaskDataSource(taskDao)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(tds: TaskDataSource): TaskRepository {
        return TaskRepository(tds)
    }

    @Provides
    @Singleton
    fun provideTaskDao(@ApplicationContext context: Context): TaskDao {
        val db = Room.databaseBuilder(
            context,
            Database::class.java, "todo_db"
        ).build()
        return db.getTaskDao()
    }
}