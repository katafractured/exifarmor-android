package com.katafract.exifarmor.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Brand color schemes built from `Color.kt` tokens.
 * KataGold is primary; KataMidnight + tier surfaces are background+card.
 */
private val DarkColorScheme = darkColorScheme(
    primary = KataGold,
    onPrimary = KataMidnight,
    primaryContainer = Color(0xFF3A2D14),
    onPrimaryContainer = KataChampagne,

    secondary = KataChampagne,
    onSecondary = KataMidnight,
    secondaryContainer = Color(0xFF2A2418),
    onSecondaryContainer = KataChampagne,

    tertiary = KataSapphire,
    onTertiary = KataIce,

    error = DangerRed,
    onError = Color.White,

    background = KataMidnight,
    onBackground = KataIce,
    surface = SurfaceDark,
    onSurface = KataIce,
    surfaceVariant = SurfaceDarkElev,
    onSurfaceVariant = TextSecondaryDark,
    outline = OutlineDark,
    outlineVariant = OutlineDark,
)

private val LightColorScheme = lightColorScheme(
    primary = KataGold,
    onPrimary = Color.White,
    primaryContainer = KataChampagne,
    onPrimaryContainer = Color(0xFF3A2D14),

    secondary = KataSapphire,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD8E1F0),
    onSecondaryContainer = KataSapphire,

    tertiary = KataNavy,
    onTertiary = Color.White,

    error = DangerRed,
    onError = Color.White,

    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = OutlineLight,
    outlineVariant = OutlineLight,
)

@Composable
fun ExifArmorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Drive system bars: transparent + correct icon tint per theme.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            // Status + nav bar transparent so edge-to-edge content paints behind.
            // (Setting colors directly is deprecated for >=A15 but harmless on older.)
            @Suppress("DEPRECATION")
            window.statusBarColor = Color.Transparent.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.Transparent.toArgb()

            val controller = WindowCompat.getInsetsController(window, view)
            // Light theme = dark icons on light status bar; dark theme = the inverse.
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ExifArmorTypography,
        content = content,
    )
}

// Helper extension preserved so existing call sites keep compiling.
val androidx.compose.material3.ColorScheme.onBackgroundVariant: Color
    @Composable
    get() = onSurfaceVariant

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(),
    (red * 255).toInt(),
    (green * 255).toInt(),
    (blue * 255).toInt(),
)
