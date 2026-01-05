package com.example.babygrowthtracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaby(baby: BabyEntity)

    @Query("SELECT * FROM babies LIMIT 1")
    fun getBaby(): Flow<BabyEntity?>

    // NEW: Delete all babies to prevent "Ghost" profiles like Sam from sticking around
    @Query("DELETE FROM babies")
    suspend fun deleteAllBabies()
}