package com.example.babygrowthtracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "babies")
data class BabyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val gender: String, // "Boy" or "Girl"
    val dob: Long, // Timestamp
    val weightAtBirth: String,
    val parentId: String // Links to the Firebase Auth User
)