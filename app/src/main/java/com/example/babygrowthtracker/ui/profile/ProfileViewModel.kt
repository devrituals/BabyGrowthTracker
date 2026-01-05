package com.example.babygrowthtracker.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babygrowthtracker.data.local.BabyEntity
import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.domain.model.LogType
import com.example.babygrowthtracker.domain.repository.BabyRepository
import com.example.babygrowthtracker.domain.repository.LogRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val logRepository: LogRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    fun saveProfile(name: String, gender: String, birthWeight: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: "guest"
            val babyId = "dummy_baby_id"

            // 1. WIPE EVERYTHING (Fresh Start)
            babyRepository.clearAllBabies()
            logRepository.clearAllLogs() // <--- NEW: Deletes old history

            // 2. Save the New Profile
            val baby = BabyEntity(
                id = babyId,
                name = name,
                gender = gender,
                dob = System.currentTimeMillis(),
                weightAtBirth = birthWeight,
                parentId = userId
            )
            babyRepository.saveBabyProfile(baby)

            // 3. Save the Birth Weight Log
            if (birthWeight.isNotBlank()) {
                val formattedValue = if (birthWeight.any { it.isLetter() }) birthWeight else "$birthWeight kg"
                val weightLog = LogEntry(
                    id = UUID.randomUUID().toString(),
                    babyId = babyId,
                    timestamp = System.currentTimeMillis(),
                    type = LogType.GROWTH,
                    value = formattedValue,
                    notes = "Birth Weight"
                )
                logRepository.addLog(weightLog)
            }

            onSuccess()
        }
    }
}