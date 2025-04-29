package com.jetbrains.inlock.screens.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.inlock.data.FirebaseService
import com.jetbrains.inlock.data.Product
import com.jetbrains.inlock.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssetsUiState(
    val isLoading: Boolean = true,
    val assets: List<Product> = emptyList(),
    val error: String = ""
)

class AssetsViewModel(
    private val firebaseService: FirebaseService,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AssetsUiState())
    val uiState: StateFlow<AssetsUiState> = _uiState.asStateFlow()

    init {
        loadAssets()
    }

    fun loadAssets() {
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

            productRepository.getUserProducts(userId)
                .onSuccess { products ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            assets = products
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load assets"
                        )
                    }
                }
        }
    }
}