package com.example.babygrowthtracker.domain.repository

import android.app.Activity
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun isUserPremium(): Flow<Boolean>
    suspend fun purchasePremium(activity: Activity)
    suspend fun restorePurchases()
}