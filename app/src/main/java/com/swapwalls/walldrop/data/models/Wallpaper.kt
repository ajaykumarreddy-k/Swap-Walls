package com.swapwalls.walldrop.data.models
 
import com.google.gson.annotations.SerializedName
 
data class Wallpaper(
    @SerializedName("id")    val id: String,
    @SerializedName("title") val title: String = "",
    @SerializedName("file")  val file: String,
    @SerializedName("thumb") val thumb: String,
    @SerializedName("color") val color: String = "#1565C0"
) {
    // Full-resolution URL
    fun fullUrl(categoryId: String): String =
        "https://raw.githubusercontent.com/ajaykumarreddy-k/Swap-Walls/main/output/wallpapers/${categoryId}/${file}"
 
    // Thumbnail URL (used in grids for fast loading)
    fun thumbUrl(categoryId: String): String =
        "https://raw.githubusercontent.com/ajaykumarreddy-k/Swap-Walls/main/output/wallpapers/${categoryId}/${thumb}"
}
