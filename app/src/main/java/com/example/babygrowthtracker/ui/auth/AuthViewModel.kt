package com.example.babygrowthtracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babygrowthtracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState = _signInState.asStateFlow()

    fun signInWithGoogle(idToken: String) {
        performAuth { authRepository.signInWithGoogle(idToken) }
    }

    fun signInWithEmail(email: String, pass: String) {
        performAuth { authRepository.signInWithEmail(email, pass) }
    }

    fun signUpWithEmail(email: String, pass: String) {
        performAuth { authRepository.signUpWithEmail(email, pass) }
    }

    fun signInAnonymously() {
        performAuth { authRepository.signInAnonymously() }
    }

    // Helper to reduce duplicated code
    private fun performAuth(authAction: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            val result = authAction()
            result.onSuccess {
                _signInState.value = SignInState.Success
            }.onFailure {
                _signInState.value = SignInState.Error(it.message ?: "Authentication failed")
            }
        }
    }
}

sealed class SignInState {
    object Idle : SignInState()
    object Loading : SignInState()
    object Success : SignInState()
    data class Error(val message: String) : SignInState()
}