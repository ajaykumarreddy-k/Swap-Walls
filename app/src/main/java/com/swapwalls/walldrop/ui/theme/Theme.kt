package com.swapwalls.walldrop.ui.theme
 
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
 
// Fallback palette for Android < 12 (no dynamic color)
private val LightColorScheme = lightColorScheme(
    primary          = Color(0xFF1565C0),
    onPrimary        = Color.White,
    secondary        = Color(0xFF0288D1),
    onSecondary      = Color.White,
    background       = Color(0xFFFAFAFA),
    surface          = Color.White,
    onBackground     = Color(0xFF1C1B1F),
    onSurface        = Color(0xFF1C1B1F),
)
 
private val DarkColorScheme = darkColorScheme(
    primary          = Color(0xFF90CAF9),
    onPrimary        = Color(0xFF003258),
    secondary        = Color(0xFF81D4FA),
    background       = Color(0xFF1C1B1F),
    surface          = Color(0xFF2B2B2B),
    onBackground     = Color.White,
    onSurface        = Color.White,
)
 
@Composable
fun SwapWallsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color = Material You (uses device wallpaper colors)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
 
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
 
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
