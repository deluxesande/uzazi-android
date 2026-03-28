package com.uzazi.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SoftRose,
    secondary = PetalLight,
    tertiary = MintGreen,
    background = NightBlue,
    surface = NightSurface,
    onPrimary = NightBlue,
    onSecondary = NightBlue,
    onTertiary = NightBlue,
    onBackground = SoftRose,
    onSurface = SoftRose
)

private val LightColorScheme = lightColorScheme(
    primary = BloomPink,
    secondary = DeepPlum,
    tertiary = MintGreen,
    background = SoftRose,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DeepPlum,
    onSurface = DeepPlum
)

@Composable
fun UzaziTheme(
    nightMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (nightMode) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !nightMode
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
