package com.swapwalls.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.swapwalls.app.data.models.Wallpaper

@Composable
fun WallpaperCard(
    wallpaper: Wallpaper,
    categoryId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Images must load from: https://raw.githubusercontent.com/<username>/<repo>/main/wallpapers/<category>/<image>
    // Assuming 'file' or 'thumb' contains the image filename
    // Replace <username>/<repo> as needed or handle dynamically
    val thumbUrl = "https://raw.githubusercontent.com/<username>/<repo>/main/wallpapers/$categoryId/${wallpaper.thumb}"

    Card(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(0.7f) // Portrait aspect ratio
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = thumbUrl,
            contentDescription = wallpaper.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
