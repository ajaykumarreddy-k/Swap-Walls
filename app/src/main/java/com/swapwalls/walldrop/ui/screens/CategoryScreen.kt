package com.swapwalls.walldrop.ui.screens
 
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swapwalls.walldrop.ui.components.WallpaperCard
import com.swapwalls.walldrop.viewmodel.CategoryUiState
import com.swapwalls.walldrop.viewmodel.CategoryViewModel
 
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onBack: () -> Unit,
    onWallpaperClick: (wallpaperId: String, categoryId: String) -> Unit,
    viewModel: CategoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
 
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (uiState is CategoryUiState.Success)
                            (uiState as CategoryUiState.Success).category.name
                        else "Loading...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
 
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
 
                is CategoryUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
 
                is CategoryUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadCategory() }) { Text("Retry") }
                        }
                    }
                }
 
                is CategoryUiState.Success -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.category.wallpapers) { wallpaper ->
                            WallpaperCard(
                                wallpaper = wallpaper,
                                categoryId = state.category.id,
                                onClick = {
                                    onWallpaperClick(wallpaper.id, state.category.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
