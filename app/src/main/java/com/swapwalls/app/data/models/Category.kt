package com.swapwalls.app.data.models

data class Category(
    val id: String,
    val name: String,
    val wallpapers: List<Wallpaper>
)
