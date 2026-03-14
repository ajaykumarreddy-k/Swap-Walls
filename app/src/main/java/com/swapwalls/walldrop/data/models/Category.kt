package com.swapwalls.walldrop.data.models
 
import com.google.gson.annotations.SerializedName
 
data class Category(
    @SerializedName("id")         val id: String,
    @SerializedName("name")       val name: String,
    @SerializedName("icon")       val icon: String = "image",
    @SerializedName("wallpapers") val wallpapers: List<Wallpaper> = emptyList()
) {
    val count: Int get() = wallpapers.size
 
    // Returns the first wallpaper's thumbnail as the category cover image
    fun coverThumbUrl(): String? =
        wallpapers.firstOrNull()?.thumbUrl(id)
}
