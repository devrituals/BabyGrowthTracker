package com.example.babygrowthtracker.domain.usecase

import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBabyLogsUseCase @Inject constructor(
    private val repository: LogRepository
) {
    operator fun invoke(babyId: String): Flow<List<LogEntry>> {
        return repository.getLogsForBaby(babyId)
    }
}