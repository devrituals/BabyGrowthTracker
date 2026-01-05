package com.example.babygrowthtracker.domain.repository

import com.example.babygrowthtracker.domain.model.LogEntry
import kotlinx.coroutines.flow.Flow

interface LogRepository {
    fun getLogsForBaby(babyId: String): Flow<List<LogEntry>>
    suspend fun addLog(log: LogEntry)
    suspend fun clearAllLogs() // <--- ADD THIS
}