package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SaiyanGold,
    onPrimary = DarkSpaceBg,
    primaryContainer = KiFlameOrange,
    onPrimaryContainer = TextPrimary,
    secondary = CapsuleCyan,
    onSecondary = DarkSpaceBg,
    secondaryContainer = VegetaRoyalBlue,
    onSecondaryContainer = TextPrimary,
    tertiary = ScouterGreen,
    onTertiary = DarkSpaceBg,
    background = DarkSpaceBg,
    onBackground = TextPrimary,
    surface = DarkSpaceSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSpaceCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkSpaceBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Keep dark anime scouter theme as primary aesthetic for maximum visual punch
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
