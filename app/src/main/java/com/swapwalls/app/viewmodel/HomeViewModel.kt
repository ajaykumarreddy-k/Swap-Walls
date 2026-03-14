package com.swapwalls.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapwalls.app.data.models.Category
import com.swapwalls.app.data.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val categories: List<Category>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val repository: WallpaperRepository = WallpaperRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val repository = WallpaperRepository()
            // Placeholder URL: replace <username>/<repo> with actual values
            val manifestUrl = "https://raw.githubusercontent.com/<username>/<repo>/main/manifest.json"
            
            repository.getCategories(manifestUrl).onSuccess { categories ->
                _uiState.value = HomeUiState.Success(categories)
            }.onFailure { error ->
                _uiState.value = HomeUiState.Error(error.message ?: "Unknown error")
            }
        }
    }
}
