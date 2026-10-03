package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandAmber500,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = BrandAmber600.copy(alpha = 0.25f),
    onPrimaryContainer = BrandAmber400,

    secondary = BrandCyan400,
    onSecondary = Color(0xFF082F49),
    secondaryContainer = BrandCyan600.copy(alpha = 0.25f),
    onSecondaryContainer = BrandCyan400,

    tertiary = BrandAmber400,
    onTertiary = Color(0xFF451A03),

    background = BrandNavy900,
    onBackground = TextPrimary,

    surface = BrandNavy800,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,

    outline = BorderSubtle,
    outlineVariant = BrandNavy500,
    error = BrandError,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme(
    // Holding Hub uses dark navy & premium high-contrast design
    primary = BrandAmber500,
    onPrimary = Color(0xFF0F172A),
    secondary = BrandCyan500,
    onSecondary = Color.White,
    background = BrandNavy900,
    onBackground = TextPrimary,
    surface = BrandNavy800,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = BrandError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
