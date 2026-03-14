package com.swapwalls.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapwalls.app.data.models.Wallpaper
import com.swapwalls.app.data.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Success(val wallpapers: List<Wallpaper>) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

class CategoryViewModel(
    private val repository: WallpaperRepository = WallpaperRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun fetchWallpapers(categoryId: String) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            // Placeholder URL: replace <username>/<repo> with actual values
            val manifestUrl = "https://raw.githubusercontent.com/<username>/<repo>/main/manifest.json"
            
            repository.getWallpapersByCategory(manifestUrl, categoryId).onSuccess { wallpapers ->
                _uiState.value = CategoryUiState.Success(wallpapers)
            }.onFailure { error ->
                _uiState.value = CategoryUiState.Error(error.message ?: "Unknown error")
            }
        }
    }
}
