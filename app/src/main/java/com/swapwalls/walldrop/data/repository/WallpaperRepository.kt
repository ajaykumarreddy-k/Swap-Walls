package com.swapwalls.walldrop.data.repository
 
import com.swapwalls.walldrop.data.models.Category
import com.swapwalls.walldrop.data.network.RetrofitClient
 
class WallpaperRepository {
 
    private val api = RetrofitClient.apiService
 
    // Fetches and returns categories from the manifest
    // Wraps in Result so ViewModel can handle success/failure cleanly
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val manifest = api.getManifest()
            Result.success(manifest.categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
 
    // Finds a single category by ID
    suspend fun getCategoryById(categoryId: String): Result<Category> {
        return try {
            val manifest = api.getManifest()
            val category = manifest.categories.find { it.id == categoryId }
                ?: return Result.failure(Exception("Category not found: $categoryId"))
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
