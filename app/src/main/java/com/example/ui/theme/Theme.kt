package com.example.ui.theme

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

// ═══════════════════════════════════════════
// QUSHO GAMING — Enhanced Background Pattern
// ═══════════════════════════════════════════

fun Modifier.muslimPattern(): Modifier = this.drawBehind {
    // Subtle hexagonal dot grid — gaming aesthetic
    val dotColor = Color(0xFF00E68A).copy(alpha = 0.03f)
    val dotRadius = 1.0f.dp.toPx()
    val gap = 24.dp.toPx()
    val width = size.width
    val height = size.height
    var x = gap / 2f
    while (x < width) {
        var y = gap / 2f
        while (y < height) {
            drawCircle(color = dotColor, radius = dotRadius, center = Offset(x, y))
            y += gap
        }
        x += gap
    }

    // Subtle radial glow at top center
    val glowColor = Color(0xFF00E68A).copy(alpha = 0.02f)
    drawCircle(
        brush = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(glowColor, Color.Transparent),
            center = Offset(width / 2f, 0f),
            radius = width * 0.6f
        ),
        radius = width * 0.6f,
        center = Offset(width / 2f, 0f)
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.Black,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    tertiary = CyanAccent,
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.White,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    tertiary = CyanAccent,
    background = Color(0xFFF9FBF9),
    onBackground = Color(0xFF1E2421),
    surface = Color.White,
    onSurface = Color(0xFF1E2421),
    surfaceVariant = Color(0xFFE8F0EA),
    onSurfaceVariant = Color(0xFF55605A)
)

@Suppress("DEPRECATION")
@Composable
fun MuslimLevelingTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
