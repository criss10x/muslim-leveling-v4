package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Nur Quest splash — shield glow + gradient title + "MEMUAT DATA PEJUANG..."
 *
 * Phases:
 *   0 (0–600ms):   shield glow pulses, background grid
 *   1 (600–1200ms): "MUSLIM LEVELING" gradient title fade+scale in
 *   2 (1200–1800ms): tagline "Level Up Imanmu, Level Up Kehidupanmu" fade in
 *   3 (1800–2400ms): "MEMUAT DATA PEJUANG..." status text + progress bar
 *   4 (2400ms):     onTimeout invoked
 */
@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    var phase by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        delay(600);   phase = 1
        delay(600);   phase = 2
        delay(600);   phase = 3
        delay(800);   onTimeout()
    }

    // Shield glow pulse
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )
    val arcRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "arc_rotation"
    )
    // Loading dots animation
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .futuristicBackground(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Shield icon with rotating glow ring ──
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .drawBehind {
                        val radius = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        // Pulsing radial glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    IslamicGreen.copy(alpha = glowPulse * 0.5f),
                                    IslamicGreen.copy(alpha = 0.05f),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = radius * 1.4f
                            ),
                            center = center,
                            radius = radius * 1.4f
                        )

                        // Rotating gradient arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    IslamicGreen,
                                    CyanAccent,
                                    Color.Transparent
                                )
                            ),
                            startAngle = arcRotation,
                            sweepAngle = 270f,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = IslamicGreen.copy(alpha = 0.6f),
                            spotColor = CyanAccent.copy(alpha = 0.3f)
                        )
                        .background(
                            brush = Brush.verticalGradient(listOf(DarkSurface, DarkSurfaceElevated)),
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(listOf(IslamicGreen, CyanAccent)),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Shield emoji per mockup
                    Text(text = "🛡️", fontSize = 44.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── MUSLIM LEVELING (gradient text) ──
            AnimatedVisibility(
                visible = phase >= 1,
                enter = fadeIn(tween(500)) + scaleIn(tween(500), initialScale = 0.85f)
            ) {
                Text(
                    text = "MUSLIM LEVELING",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = CyanAccent,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tagline ──
            AnimatedVisibility(
                visible = phase >= 2,
                enter = fadeIn(tween(500))
            ) {
                Text(
                    text = "Level Up Imanmu, Level Up Kehidupanmu",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextLight,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Loading status + progress bar ──
            AnimatedVisibility(
                visible = phase >= 3,
                enter = fadeIn(tween(400))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // "MEMUAT DATA PEJUANG..." monospace
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "MEMUAT DATA PEJUANG",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGreen.copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp
                        )
                        // Animated dots (3 dots cycling)
                        repeat(3) { i ->
                            Text(
                                text = ".",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGreen.copy(
                                    alpha = if ((phase + i) % 3 == 0) dotAlpha else 0.3f
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress bar track (indeterminate-style fill)
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(DarkSurfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(glowPulse)
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(IslamicGreen, CyanAccent)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}
