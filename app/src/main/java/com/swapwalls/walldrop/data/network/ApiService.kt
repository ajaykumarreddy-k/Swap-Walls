package com.swapwalls.walldrop.data.network
 
import com.swapwalls.walldrop.data.models.WallpaperManifest
import retrofit2.http.GET
 
interface ApiService {
    // Fetches the full manifest from GitHub
    // Full URL = BASE_URL + this path
    @GET("ajaykumarreddy-k/Swap-Walls/main/manifest.json")
    suspend fun getManifest(): WallpaperManifest
}
