package com.example.babygrowthtracker.domain.repository

import com.example.babygrowthtracker.domain.model.LogEntry

interface PdfRepository {
    suspend fun generatePdf(logs: List<LogEntry>)
}