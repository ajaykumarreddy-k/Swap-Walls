package com.swapwalls.walldrop.viewmodel
 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapwalls.walldrop.data.models.Category
import com.swapwalls.walldrop.data.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
 
// Represents all possible states the Home screen can be in
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val categories: List<Category>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
 
class HomeViewModel : ViewModel() {
 
    private val repository = WallpaperRepository()
 
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
 
    // Auto-fetch on creation
    init { loadCategories() }
 
    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            repository.getCategories()
                .onSuccess { categories ->
                    _uiState.value = HomeUiState.Success(categories)
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(
                        error.message ?: "Failed to load wallpapers"
                    )
                }
        }
    }
 
    // Called when user pulls to refresh
    fun refresh() = loadCategories()
}
