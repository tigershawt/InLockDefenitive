package com.jetbrains.inlock.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.inlock.data.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String = "",
    val isAuthenticated: Boolean = false,
    val isRegistrationMode: Boolean = false
)

class LoginViewModel(
    private val firebaseService: FirebaseService
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _uiState.update { it.copy(error = "Please fill in all fields") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = "") }

            firebaseService.signIn(email, password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Authentication failed"
                        )
                    }
                }
        }
    }

    fun signUp(email: String, password: String, displayName: String, username: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank() || displayName.isBlank() || username.isBlank()) {
                _uiState.update { it.copy(error = "Please fill in all fields") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = "") }

            firebaseService.signUp(email, password, displayName, username)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Registration failed"
                        )
                    }
                }
        }
    }

    fun toggleRegistrationMode() {
        _uiState.update { it.copy(isRegistrationMode = !it.isRegistrationMode, error = "") }
    }
}