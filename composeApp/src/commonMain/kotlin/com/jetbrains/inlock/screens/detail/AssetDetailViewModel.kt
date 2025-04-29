package com.jetbrains.inlock.screens.detail

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

data class AssetDetailUiState(
    val isLoading: Boolean = true,
    val asset: Product? = null,
    val error: String = "",
    val isUserOwner: Boolean = false
)

class AssetDetailViewModel(
    private val firebaseService: FirebaseService,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AssetDetailUiState())
    val uiState: StateFlow<AssetDetailUiState> = _uiState.asStateFlow()

    fun loadAssetDetails(assetId: String) {
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

            productRepository.getProduct(assetId)
                .onSuccess { product ->
                    if (product != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                asset = product,
                                isUserOwner = product.currentOwner == userId
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Asset not found"
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load asset details"
                        )
                    }
                }
        }
    }

    fun transferOwnership(newOwnerId: String) {
        val asset = _uiState.value.asset ?: return

        viewModelScope.launch {
            productRepository.transferProductOwnership(asset.id, newOwnerId)
                .onSuccess {
                    // Handle success
                    loadAssetDetails(asset.id)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "Transfer failed")
                    }
                }
        }
    }
}