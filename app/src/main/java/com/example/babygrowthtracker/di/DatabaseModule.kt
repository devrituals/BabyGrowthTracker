package com.example.babygrowthtracker.di

import android.app.Application
import androidx.room.Room
import com.example.babygrowthtracker.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "baby_growth_db"
        )
        .fallbackToDestructiveMigration() // Wipes data if schema changes (good for dev)
        .build()
    }

    @Provides
    @Singleton
    fun provideLogDao(db: AppDatabase) = db.logDao()
    
    @Provides
    @Singleton
    fun provideBabyDao(db: AppDatabase) = db.babyDao()
}