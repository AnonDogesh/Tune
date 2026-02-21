package com.tune.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = SeaGreen,
    secondary = WarmOrange,
    tertiary = CoralRed,
    background = DeepTeal,
    surface = DarkSurface,
    onPrimary = Color.White,
    onBackground = SoftYellow,
    onSurface = Color.White
)

private val LightScheme = lightColorScheme(
    primary = SeaGreen,
    secondary = WarmOrange,
    tertiary = CoralRed,
    background = LightSurface,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = DeepTeal,
    onSurface = DeepTeal
)

@Composable
fun TuneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = TuneTypography,
        content = content
    )
}
