package com.example.babygrowthtracker.ui.growth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.domain.model.LogType
import com.example.babygrowthtracker.domain.repository.SubscriptionRepository
import com.example.babygrowthtracker.domain.usecase.GenerateGrowthPdfUseCase
import com.example.babygrowthtracker.domain.usecase.GetBabyLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrowthChartViewModel @Inject constructor(
    private val getBabyLogsUseCase: GetBabyLogsUseCase,
    private val generateGrowthPdfUseCase: GenerateGrowthPdfUseCase,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GrowthChartState())
    val state = _state.asStateFlow()

    init {
        val logsFlow = getBabyLogsUseCase("dummy_baby_id")
            .map { logs ->
                // Sort by time first
                logs.filter { it.type == LogType.GROWTH }
                    .sortedBy { it.timestamp }
            }

        // Force Premium for testing
        val isPremiumFlow = flowOf(true)

        combine(logsFlow, isPremiumFlow) { logs, isPremium ->
            // --- LOGIC: Separate Height vs Weight based on text ---
            val heightLogs = logs.filter { it.value.contains("cm", ignoreCase = true) }
            val weightLogs = logs.filter { it.value.contains("kg", ignoreCase = true) }
            
            GrowthChartState(
                allLogs = logs,
                heightLogs = heightLogs,
                weightLogs = weightLogs,
                isPremium = isPremium
            )
        }.onEach { 
            _state.value = it 
        }.launchIn(viewModelScope)
    }

    fun generatePdfReport() {
        viewModelScope.launch {
            generateGrowthPdfUseCase(state.value.allLogs)
        }
    }
}

data class GrowthChartState(
    val allLogs: List<LogEntry> = emptyList(),
    val heightLogs: List<LogEntry> = emptyList(),
    val weightLogs: List<LogEntry> = emptyList(),
    val isPremium: Boolean = false
)