package com.jesil.projectmvvm.todolist.di

import android.app.Application
import androidx.room.Room
import com.jesil.projectmvvm.todolist.annotation.ApplicationScope
import com.jesil.projectmvvm.todolist.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * created by @author [Jesil Toborowei]
 * */

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application,
        callback : TaskDatabase.Callback
    ) = Room.databaseBuilder(application, TaskDatabase::class.java, "task_database")
                .fallbackToDestructiveMigration()
                .addCallback(callback)
                .build()

    @Provides
    fun provideTaskDao(db : TaskDatabase) = db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}


