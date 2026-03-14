package com.swapwalls.walldrop.viewmodel
 
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapwalls.walldrop.data.models.Category
import com.swapwalls.walldrop.data.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
 
sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Success(val category: Category) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}
 
class CategoryViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
 
    // categoryId is passed via navigation arguments
    private val categoryId: String = checkNotNull(savedStateHandle["categoryId"])
    private val repository = WallpaperRepository()
 
    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()
 
    init { loadCategory() }
 
    fun loadCategory() {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            repository.getCategoryById(categoryId)
                .onSuccess { category ->
                    _uiState.value = CategoryUiState.Success(category)
                }
                .onFailure { error ->
                    _uiState.value = CategoryUiState.Error(
                        error.message ?: "Failed to load category"
                    )
                }
        }
    }
}
