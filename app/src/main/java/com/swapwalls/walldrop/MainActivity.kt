package com.swapwalls.walldrop
 
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swapwalls.walldrop.ui.screens.CategoryScreen
import com.swapwalls.walldrop.ui.screens.HomeScreen
import com.swapwalls.walldrop.ui.screens.SettingsScreen
import com.swapwalls.walldrop.ui.screens.WallpaperDetailScreen
import com.swapwalls.walldrop.ui.theme.WallDropTheme
 
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Full edge-to-edge display
        setContent {
            WallDropTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
 
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        // Screen 1: Home — category grid
                        composable("home") {
                            HomeScreen(
                                onCategoryClick = { categoryId ->
                                    navController.navigate("category/$categoryId")
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                }
                            )
                        }
 
                        // Screen 2: Category — wallpaper grid
                        composable(
                            route = "category/{categoryId}",
                            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                        ) {
                            CategoryScreen(
                                onBack = { navController.navigateUp() },
                                onWallpaperClick = { wallpaperId, categoryId ->
                                    navController.navigate("detail/$categoryId/$wallpaperId")
                                }
                            )
                        }
 
                        composable(
                            route = "detail/{categoryId}/{wallpaperId}",
                            arguments = listOf(
                                navArgument("categoryId")   { type = NavType.StringType },
                                navArgument("wallpaperId")  { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            WallpaperDetailScreen(
                                categoryId   = backStackEntry.arguments?.getString("categoryId") ?: "",
                                wallpaperId  = backStackEntry.arguments?.getString("wallpaperId") ?: "",
                                onBack       = { navController.navigateUp() }
                            )
                        }
 
                        // Screen 4: Settings
                        composable("settings") {
                            SettingsScreen(onBack = { navController.navigateUp() })
                        }
                    }
                }
            }
        }
    }
}
