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
import androidx.compose.ui.Modifier
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
                    val screens = listOf(
                        Triple("home", Icons.Default.Home, "Home"),
                        Triple("settings", Icons.Default.Settings, "Settings"),
                        Triple("donation", Icons.Default.Favorite, "Donate")
                    )
                    screens.forEach { (route, icon, label) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(label) },
                            selected = currentDestination == route,
                            onClick = {
                                if (currentDestination != route) {
                                    navController.navigate(route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo("home") {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
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
                        navController.navigate("settings") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
