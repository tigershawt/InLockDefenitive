package com.jetbrains.inlock.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.inlock.data.FirebaseService
import com.jetbrains.inlock.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userData: UserData? = null,
    val error: String = ""
)

class ProfileViewModel(
    private val firebaseService: FirebaseService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }

            val userId = firebaseService.getCurrentUserId()
            if (userId == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "User not signed in"
                    )
                }
                return@launch
            }

            firebaseService.getUserData(userId)
                .onSuccess { userData ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userData = userData
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load user profile"
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            firebaseService.signOut()
        }
    }
}