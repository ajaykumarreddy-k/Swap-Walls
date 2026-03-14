package com.swapwalls.walldrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swapwalls.walldrop.ui.screens.*
import com.swapwalls.walldrop.ui.theme.WallDropTheme

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            val showBottomBar = currentDestination in listOf("home", "settings", "donation")
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home") },
                        selected = currentDestination == "home",
                        onClick = {
                            if (currentDestination != "home") {
                                navController.navigate("home") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text("Settings") },
                        selected = currentDestination == "settings",
                        onClick = {
                            if (currentDestination != "settings") {
                                navController.navigate("settings") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text("Donate") },
                        selected = currentDestination == "donation",
                        onClick = {
                            if (currentDestination != "donation") {
                                navController.navigate("donation") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
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

            composable("donation") {
                DonationScreen(onBack = { navController.navigateUp() })
            }

            composable("settings") {
                SettingsScreen(onBack = { navController.navigateUp() })
            }

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
        }
    }
}
 
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WallDropTheme {
                MainScreen()
            }
        }
    }
}
