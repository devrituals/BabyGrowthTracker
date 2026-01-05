package com.example.babygrowthtracker.ui.dashboard

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.babygrowthtracker.data.workers.ReminderWorker
import com.example.babygrowthtracker.data.workers.SyncWorker
import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.domain.model.LogType
import com.example.babygrowthtracker.domain.repository.BabyRepository
import com.example.babygrowthtracker.domain.usecase.AddLogEntryUseCase
import com.example.babygrowthtracker.domain.usecase.GetBabyLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getBabyLogsUseCase: GetBabyLogsUseCase,
    private val addLogEntryUseCase: AddLogEntryUseCase,
    private val babyRepository: BabyRepository // Injecting BabyRepository
) : ViewModel() {

    private val _babyName = MutableStateFlow("Baby")
    val babyName = _babyName.asStateFlow()

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs = _logs.asStateFlow()

    private val _stats = MutableStateFlow(DashboardStats())
    val stats = _stats.asStateFlow()

    // Timer state
    var isTimerRunning by mutableStateOf(false)
    var timerSeconds by mutableLongStateOf(0L)

    init {
        fetchBabyName()
        getLogs("dummy_baby_id")
    }

    private fun fetchBabyName() {
        viewModelScope.launch {
            babyRepository.getBabyProfile().collect { baby ->
                if (baby != null) {
                    _babyName.value = baby.name
                }
            }
        }
    }

    private fun getLogs(babyId: String) {
        getBabyLogsUseCase(babyId)
            .onEach { logList ->
                val sortedLogs = logList.sortedByDescending { it.timestamp }
                _logs.value = sortedLogs
                calculateStats(sortedLogs)
            }
            .launchIn(viewModelScope)
    }

    private fun calculateStats(logs: List<LogEntry>) {
        val now = System.currentTimeMillis()

        val lastFeed = logs.firstOrNull { it.type == LogType.FEEDING }
        val timeSinceFeed = if (lastFeed != null) {
            val diff = now - lastFeed.timestamp
            formatDuration(diff)
        } else "N/A"

        val diaperCount = logs.count { it.type == LogType.DIAPER }

        val lastSleep = logs.firstOrNull { it.type == LogType.SLEEP }
        val timeSinceSleep = if (lastSleep != null) {
            val diff = now - lastSleep.timestamp
            formatDuration(diff)
        } else "N/A"

        _stats.value = DashboardStats(
            lastFeedTime = timeSinceFeed,
            diapersToday = diaperCount,
            lastSleepTime = timeSinceSleep
        )
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return if (hours > 0) "${hours}h ${minutes}m ago" else "${minutes}m ago"
    }

    fun addLog(type: LogType, value: String) {
        viewModelScope.launch {
            val newLog = LogEntry(
                id = UUID.randomUUID().toString(),
                babyId = "dummy_baby_id",
                timestamp = System.currentTimeMillis(),
                type = type,
                value = value
            )
            addLogEntryUseCase(newLog)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(syncRequest)
        }
    }

    // Timer logic
    fun toggleTimer() {
        isTimerRunning = !isTimerRunning
    }

    fun updateTimer(seconds: Long) {
        timerSeconds = seconds
    }

    fun resetTimer() {
        isTimerRunning = false
        timerSeconds = 0L
    }

    fun saveTimerLog(type: LogType) {
        val minutes = timerSeconds / 60
        val seconds = timerSeconds % 60
        val durationString = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
        addLog(type, "$durationString duration")
        resetTimer()
    }

    fun scheduleReminder() {
        val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(3, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueue(reminderRequest)
    }
}

data class DashboardStats(
    val lastFeedTime: String = "--",
    val diapersToday: Int = 0,
    val lastSleepTime: String = "--"
)