package com.example.babygrowthtracker.domain.repository

import com.example.babygrowthtracker.data.local.BabyEntity
import kotlinx.coroutines.flow.Flow

interface BabyRepository {
    suspend fun saveBabyProfile(baby: BabyEntity)
    fun getBabyProfile(): Flow<BabyEntity?>
    suspend fun clearAllBabies() // <--- ADD THIS
}