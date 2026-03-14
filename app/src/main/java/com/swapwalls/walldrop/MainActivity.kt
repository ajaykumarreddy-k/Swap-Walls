package com.swapwalls.walldrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.swapwalls.walldrop.ui.screens.*
import com.swapwalls.walldrop.ui.theme.SwapWallsTheme

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
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding),
            enterTransition = { 
                fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) + 
                slideInHorizontally(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)) { it / 6 } 
            },
            exitTransition = { 
                fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow)) + 
                slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessLow)) { -it / 6 } 
            },
            popEnterTransition = { 
                fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) + 
                slideInHorizontally(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)) { -it / 6 } 
            },
            popExitTransition = { 
                fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow)) + 
                slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessLow)) { it / 6 } 
            }
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
            SwapWallsTheme {
                MainScreen()
            }
        }
    }
}
