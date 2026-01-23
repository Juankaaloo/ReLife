package com.relife.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Emerald500,
    onPrimary = Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald900,
    
    secondary = Teal500,
    onSecondary = Color.White,
    secondaryContainer = Teal100,
    onSecondaryContainer = Teal900,
    
    tertiary = Cyan500,
    onTertiary = Color.White,
    tertiaryContainer = Cyan100,
    onTertiaryContainer = Cyan900,
    
    error = Error,
    onError = Color.White,
    errorContainer = Rose100,
    onErrorContainer = Rose600,
    
    background = BackgroundLight,
    onBackground = Stone900,
    
    surface = SurfaceLight,
    onSurface = Stone900,
    surfaceVariant = Stone100,
    onSurfaceVariant = Stone600,
    
    outline = Stone300,
    outlineVariant = Stone200,
    
    scrim = Color.Black.copy(alpha = 0.32f),
    inverseSurface = Stone800,
    inverseOnSurface = Stone100,
    inversePrimary = Emerald200
)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald400,
    onPrimary = Emerald900,
    primaryContainer = Emerald700,
    onPrimaryContainer = Emerald100,
    
    secondary = Teal400,
    onSecondary = Teal900,
    secondaryContainer = Teal700,
    onSecondaryContainer = Teal100,
    
    tertiary = Cyan400,
    onTertiary = Cyan900,
    tertiaryContainer = Cyan700,
    onTertiaryContainer = Cyan100,
    
    error = Rose400,
    onError = Rose900,
    errorContainer = Rose700,
    onErrorContainer = Rose100,
    
    background = BackgroundDark,
    onBackground = Stone100,
    
    surface = SurfaceDark,
    onSurface = Stone100,
    surfaceVariant = Stone800,
    onSurfaceVariant = Stone400,
    
    outline = Stone600,
    outlineVariant = Stone700,
    
    scrim = Color.Black.copy(alpha = 0.32f),
    inverseSurface = Stone200,
    inverseOnSurface = Stone800,
    inversePrimary = Emerald600
)

@Composable
fun ReLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
