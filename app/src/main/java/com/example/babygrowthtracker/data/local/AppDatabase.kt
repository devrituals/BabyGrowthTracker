package com.example.babygrowthtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Updated version to 2, added BabyEntity
@Database(entities = [LogEntryEntity::class, BabyEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun babyDao(): BabyDao
}