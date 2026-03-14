package com.swapwalls.app.data.repository

import com.swapwalls.app.data.models.Category
import com.swapwalls.app.data.models.Wallpaper
import com.swapwalls.app.data.models.WallpaperManifest
import com.swapwalls.app.data.network.RetrofitClient

class WallpaperRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getManifest(url: String): Result<WallpaperManifest> {
        return try {
            val manifest = apiService.getManifest(url)
            Result.success(manifest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(url: String): Result<List<Category>> {
        return getManifest(url).map { it.categories }
    }

    suspend fun getWallpapersByCategory(url: String, categoryId: String): Result<List<Wallpaper>> {
        return getManifest(url).mapCatching { manifest ->
            manifest.categories.find { it.id == categoryId }?.wallpapers
                ?: throw Exception("Category not found")
        }
    }
}
