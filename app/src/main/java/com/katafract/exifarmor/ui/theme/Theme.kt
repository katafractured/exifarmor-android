package com.katafract.exifarmor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PrimaryColor = Color(0xFF8B5CF6)
private val SecondaryColor = Color(0xFF6B7280)
private val TertiaryColor = Color(0xFFD97706)
private val ErrorColor = Color(0xFFEF4444)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = TertiaryColor,
    error = ErrorColor,
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFFFFFF),
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = TertiaryColor,
    error = ErrorColor,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
)

@Composable
fun ExifArmorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content,
    )
}
