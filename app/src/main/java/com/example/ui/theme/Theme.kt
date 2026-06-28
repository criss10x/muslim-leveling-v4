package com.example.ui.theme

import android.app.Activity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════════
// ARENA HIKMAH — Backgrounds & Glow Effects
// Esports stage lighting + Islamic geometric star pattern
// ═══════════════════════════════════════════════════════════════

/**
 * Nur Quest background: deep midnight surface + subtle arena spotlights
 * (luminous layering per DESIGN.md — no Islamic star grid, just radial
 * glows that anchor the gaming HUD atmosphere).
 */
fun Modifier.futuristicBackground(
    baseColor: Color = DarkBackground,
    glowColor: Color = IslamicGreen
): Modifier = this.drawBehind {
    drawRect(color = baseColor)

    val width = size.width
    val height = size.height

    // Top-center teal spotlight (primary bloom, ~6% opacity)
    val topGlow = glowColor.copy(alpha = 0.06f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(topGlow, Color.Transparent),
            center = Offset(width / 2f, -50f),
            radius = width * 0.7f
        ),
        radius = width * 0.7f,
        center = Offset(width / 2f, -50f)
    )

    // Bottom-right gold spotlight (royal gold, prestige bloom)
    val goldGlow = GoldAccent.copy(alpha = 0.05f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(goldGlow, Color.Transparent),
            center = Offset(width * 0.9f, height * 1.1f),
            radius = width * 0.6f
        ),
        radius = width * 0.6f,
        center = Offset(width * 0.9f, height * 1.1f)
    )

    // Bottom-left cyan accent (mana cyan, electric depth)
    val cyanGlow = CyanAccent.copy(alpha = 0.04f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(cyanGlow, Color.Transparent),
            center = Offset(width * 0.1f, height * 0.9f),
            radius = width * 0.5f
        ),
        radius = width * 0.5f,
        center = Offset(width * 0.1f, height * 0.9f)
    )
}

/**
 * Old pattern kept for compatibility; now delegates to futuristic background.
 */
fun Modifier.muslimPattern(): Modifier = this.futuristicBackground()

/**
 * Gradient border modifier for cards.
 */
fun Modifier.neonCardBorder(
    color: Color = IslamicGreen,
    shape: RoundedCornerShape,
    strokeWidth: Dp = 1.dp
): Modifier = this
    .border(
        BorderStroke(
            strokeWidth,
            Brush.linearGradient(
                colors = listOf(color.copy(alpha = 0.7f), color.copy(alpha = 0.2f), color.copy(alpha = 0.7f)),
                start = Offset.Zero,
                end = Offset.Infinite
            )
        ),
        shape = shape
    )

/**
 * Animated shimmer gradient brush for progress bars and highlights.
 */
@Composable
fun rememberShimmerBrush(
    colors: List<Color>,
    durationMillis: Int = 2500
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_translate"
    )
    return Brush.linearGradient(
        colors = colors,
        start = Offset(translateAnim.value - 500f, 0f),
        end = Offset(translateAnim.value, 0f),
        tileMode = TileMode.Mirror
    )
}

/**
 * Standard gradient brush presets.
 */
fun neonGreenBrush(): Brush = Brush.horizontalGradient(GradientGreenGold)
fun neonCyanBrush(): Brush = Brush.horizontalGradient(GradientCyanGreen)
fun neonGoldBrush(): Brush = Brush.horizontalGradient(GradientGoldAmber)
fun neonPurpleBrush(): Brush = Brush.horizontalGradient(GradientPurplePink)
fun neonBlueBrush(): Brush = Brush.horizontalGradient(GradientBlueCyan)
fun neonRedBrush(): Brush = Brush.horizontalGradient(GradientRedPink)
fun darkSurfaceBrush(): Brush = Brush.verticalGradient(GradientDarkSurface)

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGreen,
    onPrimary = Color(0xFF003828),          // on-primary
    primaryContainer = IslamicGreenDim,
    onPrimaryContainer = Color(0xFF004D38),
    secondary = GoldAccent,
    onSecondary = Color(0xFF3A3000),
    secondaryContainer = AmberFlame,
    onSecondaryContainer = Color(0xFF725F00),
    tertiary = CyanAccent,
    onTertiary = Color(0xFF00363A),
    tertiaryContainer = Color(0xFF00C3CF),
    onTertiaryContainer = Color(0xFF004B50),
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    surfaceTint = IslamicGreen,
    outline = OutlineDefault,
    outlineVariant = OutlineVariant,
    error = RingRed,
    onError = Color(0xFF690005)
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
