package com.example.babygrowthtracker.data.repository

import com.example.babygrowthtracker.data.local.BabyDao
import com.example.babygrowthtracker.data.local.BabyEntity
import com.example.babygrowthtracker.domain.repository.BabyRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BabyRepositoryImpl @Inject constructor(
    private val babyDao: BabyDao,
    private val firestore: FirebaseFirestore
) : BabyRepository {

    override suspend fun saveBabyProfile(baby: BabyEntity) {
        babyDao.insertBaby(baby)
        try {
            firestore.collection("babies").document(baby.id).set(baby)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getBabyProfile(): Flow<BabyEntity?> {
        return babyDao.getBaby()
    }

    // NEW IMPLEMENTATION
    override suspend fun clearAllBabies() {
        babyDao.deleteAllBabies()
    }
}