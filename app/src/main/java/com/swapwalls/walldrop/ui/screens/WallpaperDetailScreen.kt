package com.swapwalls.walldrop.ui.screens
 
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.swapwalls.walldrop.data.models.Wallpaper
import com.swapwalls.walldrop.data.repository.WallpaperRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
 
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperDetailScreen(
    categoryId: String,
    wallpaperId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
 
    // Find the wallpaper from the repository
    var wallpaper by remember { mutableStateOf<Wallpaper?>(null) }
    LaunchedEffect(wallpaperId) {
        val repo = WallpaperRepository()
        repo.getCategoryById(categoryId).onSuccess { category ->
            wallpaper = category.wallpapers.find { it.id == wallpaperId }
        }
    }
 
    // Bottom sheet visibility
    var showSheet by remember { mutableStateOf(false) }
    // Wallpaper target dialog
    var showTargetDialog by remember { mutableStateOf(false) }
    var isSettingWallpaper by remember { mutableStateOf(false) }
 
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { _ ->
 
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable { showSheet = true }
        ) {
            wallpaper?.let { wp ->
                // Full screen image: first load thumb as placeholder, then full image
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(wp.fullUrl(categoryId))
                        .crossfade(true)
                        .build(),
                    contentDescription = wp.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
 
            // Back button — always visible top left
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
 
        // Bottom sheet: Set Wallpaper options
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                ) {
                    wallpaper?.title?.takeIf { it.isNotEmpty() }?.let { title ->
                        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                    }
 
                    // Primary action
                    Button(
                        onClick = { showTargetDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSettingWallpaper
                    ) {
                        if (isSettingWallpaper) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Set as Wallpaper")
                        }
                    }
 
                    Spacer(Modifier.height(8.dp))
 
                    // Share button
                    OutlinedButton(
                        onClick = {
                            wallpaper?.let { wp ->
                                val sendIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, wp.fullUrl(categoryId))
                                    type = "text/plain"
                                }
                                context.startActivity(android.content.Intent.createChooser(sendIntent, "Share wallpaper"))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Share")
                    }
                }
            }
        }
 
        // Dialog: choose Home / Lock / Both
        if (showTargetDialog) {
            AlertDialog(
                onDismissRequest = { showTargetDialog = false },
                confirmButton = {
                    TextButton(onClick = { showTargetDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Set Wallpaper") },
                text = {
                    Column {
                        Text("Where would you like to set this wallpaper?")
                        Spacer(Modifier.height(16.dp))
                        listOf(
                            "Home Screen" to WallpaperManager.FLAG_SYSTEM,
                            "Lock Screen" to WallpaperManager.FLAG_LOCK,
                            "Both"        to (WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
                        ).forEach { (label, flag) ->
                            TextButton(
                                onClick = {
                                    showTargetDialog = false
                                    showSheet = false
                                    isSettingWallpaper = true
                                    scope.launch {
                                        val success = applyWallpaper(
                                            context,
                                            wallpaper!!.fullUrl(categoryId),
                                            flag
                                        )
                                        isSettingWallpaper = false
                                        snackbarHostState.showSnackbar(
                                            if (success) "Wallpaper set!" else "Failed. Please try again."
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(label, fontSize = 16.sp)
                            }
                        }
                    }
                }
            )
        }
    }
}
 
// Downloads the image and applies it as wallpaper
// Runs on IO dispatcher to avoid blocking the main thread
private suspend fun applyWallpaper(
    context: Context,
    imageUrl: String,
    flag: Int
): Boolean = withContext(Dispatchers.IO) {
    return@withContext try {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context).data(imageUrl).build()
        val result = loader.execute(request)
        val bitmap = (result as SuccessResult).drawable.let {
            (it as BitmapDrawable).bitmap
        }
        val wallpaperManager = WallpaperManager.getInstance(context)
        wallpaperManager.setBitmap(bitmap, null, true, flag)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
