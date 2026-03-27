package com.fintech.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary          = RoyalBlue,
    onPrimary        = androidx.compose.ui.graphics.Color.White,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkBlue,
    secondary        = BrightGreen,
    onSecondary      = androidx.compose.ui.graphics.Color.White,
    background       = Gray50,
    onBackground     = Gray800,
    surface          = androidx.compose.ui.graphics.Color.White,
    onSurface        = Gray800,
    error            = Error,
    onError          = androidx.compose.ui.graphics.Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary          = SkyBlue,
    onPrimary        = DeepNavy,
    primaryContainer = DarkBlue,
    onPrimaryContainer = SkyBlue,
    secondary        = BrightGreen,
    onSecondary      = DeepNavy,
    background       = DeepNavy,
    onBackground     = Gray100,
    surface          = Gray800,
    onSurface        = Gray100,
    error            = Error,
    onError          = androidx.compose.ui.graphics.Color.White,
)

@Composable
fun FinTechTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
