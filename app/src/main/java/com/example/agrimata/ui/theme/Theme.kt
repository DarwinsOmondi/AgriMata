package com.example.agrimata.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define dark and light color schemes
private val LightColorScheme = lightColorScheme(
    primary = Orange80,    // Orange for main buttons, accents
    secondary = BlackGrey80,  // Black for icons, text
    background = White80,   // White background
    surface = White80,
    onPrimary = White80,    // Text on primary button
    onSecondary = White80,
    onBackground = BlackGrey80,  // Text on white background
    onSurface = BlackGrey80
)

private val DarkColorScheme = darkColorScheme(
    primary = Orange80,    // Orange remains as the main accent
    secondary = BlackGrey80,   // White text/icons in dark mode
    background = White80, // Black background
    surface = BlackGrey80,
    onPrimary = BlackGrey80,
    onSecondary = BlackGrey80,
    onBackground = White80,
    onSurface = White80
)

// Apply theme dynamically based on system theme
@Composable
fun AgriMataTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
