package com.example.babygrowthtracker.domain.usecase

import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.domain.repository.LogRepository
import javax.inject.Inject

class AddLogEntryUseCase @Inject constructor(
    private val repository: LogRepository
) {
    suspend operator fun invoke(logEntry: LogEntry) {
        repository.addLog(logEntry)
    }
}