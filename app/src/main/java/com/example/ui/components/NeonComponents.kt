package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// ═══════════════════════════════════════════════════════════════
// FUTURISTIC NEON UI COMPONENTS
// ═══════════════════════════════════════════════════════════════

/**
 * Custom gradient progress bar with animated fill and optional shimmer.
 */
@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    brush: Brush = neonGreenBrush(),
    trackColor: Color = XpBarTrack,
    glowColor: Color = IslamicGreen,
    animate: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = if (animate) 800 else 0),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(100.dp))
            .background(trackColor)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(100.dp),
                ambientColor = glowColor.copy(alpha = 0.25f),
                spotColor = glowColor.copy(alpha = 0.15f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = animatedProgress)
                .background(brush, RoundedCornerShape(100.dp))
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(100.dp),
                    ambientColor = glowColor.copy(alpha = 0.45f),
                    spotColor = glowColor.copy(alpha = 0.25f)
                )
        )
    }
}

/**
 * Futuristic card surface with gradient border and neon glow.
 */
@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    glowColor: Color = IslamicGreen,
    borderGradient: List<Color> = GradientGreenGold,
    backgroundGradient: List<Color> = GradientDarkSurface,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    elevation: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = glowColor.copy(alpha = 0.18f),
                spotColor = glowColor.copy(alpha = 0.10f)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(borderGradient),
                shape = shape
            )
            .background(
                brush = Brush.verticalGradient(backgroundGradient),
                shape = shape
            )
            .clip(shape)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

/**
 * Futuristic button with gradient background and glow.
 */
@Composable
fun NeonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    brush: Brush = neonGreenBrush(),
    glowColor: Color = IslamicGreen,
    contentColor: Color = Color.Black,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = shape,
                ambientColor = glowColor.copy(alpha = 0.35f),
                spotColor = glowColor.copy(alpha = 0.20f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = DarkSurfaceVariant,
            contentColor = contentColor,
            disabledContentColor = TextMuted
        ),
        shape = shape,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush, shape),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                content()
            }
        }
    }
}

/**
 * Small circular neon badge.
 */
@Composable
fun NeonBadge(
    text: String,
    modifier: Modifier = Modifier,
    brush: Brush = neonGreenBrush(),
    textColor: Color = Color.Black,
    shape: RoundedCornerShape = RoundedCornerShape(100.dp)
) {
    Box(
        modifier = modifier
            .background(brush, shape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}

/**
 * Neon dot with glow.
 */
@Composable
fun NeonDot(
    color: Color,
    size: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = color.copy(alpha = 0.6f),
                spotColor = color.copy(alpha = 0.4f)
            )
            .background(color, CircleShape)
    )
}

/**
 * Futuristic section title with gradient underline.
 */
@Composable
fun NeonSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GoldAccent,
    brush: Brush = neonGoldBrush()
) {
    Column(modifier = modifier) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = color,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(2.dp)
                .background(brush, RoundedCornerShape(100.dp))
        )
    }
}
