package com.example.babygrowthtracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class LogEntryEntity(
    @PrimaryKey val id: String,
    val babyId: String,
    val timestamp: Long,
    val type: String,
    val value: String,
    val notes: String?,
    val isSynced: Boolean = false
)