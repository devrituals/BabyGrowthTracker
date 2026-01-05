package com.example.babygrowthtracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntryEntity)

    @Query("SELECT * FROM logs WHERE babyId = :babyId ORDER BY timestamp DESC")
    fun getLogsForBaby(babyId: String): Flow<List<LogEntryEntity>>

    @Query("SELECT * FROM logs WHERE isSynced = 0")
    suspend fun getUnsyncedLogs(): List<LogEntryEntity>

    @Query("UPDATE logs SET isSynced = 1 WHERE id = :logId")
    suspend fun markAsSynced(logId: String)

    // NEW: Delete all logs
    @Query("DELETE FROM logs")
    suspend fun deleteAllLogs()
}