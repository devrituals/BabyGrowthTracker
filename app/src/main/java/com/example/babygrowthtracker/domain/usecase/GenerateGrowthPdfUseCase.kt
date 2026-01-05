package com.example.babygrowthtracker.domain.usecase

import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.domain.repository.PdfRepository
import javax.inject.Inject

class GenerateGrowthPdfUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(logs: List<LogEntry>) {
        return pdfRepository.generatePdf(logs)
    }
}