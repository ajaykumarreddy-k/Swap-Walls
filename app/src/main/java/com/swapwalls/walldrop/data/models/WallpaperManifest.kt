package com.swapwalls.walldrop.data.models
 
import com.google.gson.annotations.SerializedName
 
data class WallpaperManifest(
    @SerializedName("version")    val version: Int = 1,
    @SerializedName("updated")    val updated: String = "",
    @SerializedName("categories") val categories: List<Category> = emptyList()
)
