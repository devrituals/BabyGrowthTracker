package com.example.babygrowthtracker.data.repository

import com.example.babygrowthtracker.data.local.LogDao
import com.example.babygrowthtracker.data.local.LogEntryEntity
import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.domain.model.LogType
import com.example.babygrowthtracker.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val logDao: LogDao
) : LogRepository {

    override fun getLogsForBaby(babyId: String): Flow<List<LogEntry>> {
        return logDao.getLogsForBaby(babyId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addLog(log: LogEntry) {
        logDao.insertLog(log.toEntity())
    }

    // NEW IMPLEMENTATION
    override suspend fun clearAllLogs() {
        logDao.deleteAllLogs()
    }

    private fun LogEntryEntity.toDomain(): LogEntry {
        return LogEntry(
            id = this.id,
            babyId = this.babyId,
            timestamp = this.timestamp,
            type = LogType.valueOf(this.type),
            value = this.value,
            notes = this.notes
        )
    }

    private fun LogEntry.toEntity(): LogEntryEntity {
        return LogEntryEntity(
            id = this.id,
            babyId = this.babyId,
            timestamp = this.timestamp,
            type = this.type.name,
            value = this.value,
            notes = this.notes
        )
    }
}