package com.swapwalls.walldrop.ui.components
 
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.swapwalls.walldrop.data.models.Wallpaper

@Composable
fun WallpaperCard(
    wallpaper: Wallpaper,
    categoryId: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        SubcomposeAsyncImage(
            model = wallpaper.thumbUrl(categoryId),
            contentDescription = wallpaper.title.ifEmpty { "Wallpaper" },
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            loading = {
                ShimmerEffect(modifier = Modifier.fillMaxWidth().aspectRatio(0.6f))
            },
            error = {
                Box(Modifier.fillMaxWidth().aspectRatio(0.6f).background(Color.DarkGray))
            }
        )
    }
}
