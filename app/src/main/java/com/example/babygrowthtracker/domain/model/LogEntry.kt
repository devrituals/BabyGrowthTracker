package com.example.babygrowthtracker.domain.model

data class LogEntry(
    val id: String,
    val babyId: String,
    val timestamp: Long,
    val type: LogType,
    val value: String,
    val notes: String? = null
)

enum class LogType {
    FEEDING, SLEEP, DIAPER, GROWTH
}