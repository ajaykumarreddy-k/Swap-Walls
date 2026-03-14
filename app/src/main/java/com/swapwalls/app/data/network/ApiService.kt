package com.swapwalls.app.data.network

import com.swapwalls.app.data.models.WallpaperManifest
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getManifest(@Url url: String): WallpaperManifest
}
