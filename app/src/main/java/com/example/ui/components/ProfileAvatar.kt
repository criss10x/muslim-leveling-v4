package com.example.ui.components

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ═══════════════════════════════════════════════════════════════
// TIER PROFILE AVATAR — Square rounded 16dp with progressive tier borders
//
// Tier progression (makin tinggi makin epik):
//   Warrior        — Solid 2dp purple
//   Elite          — Solid 3dp blue + corner accents
//   Master         — Gradient teal→emerald 3dp
//   Grandmaster    — Gradient gold→amber 4dp + soft glow
//   Epic           — Gradient crimson 4dp + animated glow pulse
//   Legend         — Gradient white→gold 4dp + rotating shimmer ring
//   Mythic         — Gradient crimson→gold 5dp + rotating ring + particles
//   Mythic Honor   — + double rotating ring (opposite directions)
//   Mythic Glory   — + animated sparkles on border
//   Mythic Immortal — Full legendary: crown emblem + all effects active
// ═══════════════════════════════════════════════════════════════

/**
 * Tier visual configuration data
 */
data class TierVisualConfig(
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val borderWidth: Dp,
    val hasCornerAccents: Boolean = false,
    val hasGlow: Boolean = false,
    val hasPulsingGlow: Boolean = false,
    val hasRotatingRing: Boolean = false,
    val hasParticles: Boolean = false,
    val hasDoubleRing: Boolean = false,
    val hasSparkles: Boolean = false,
    val hasCrownEmblem: Boolean = false,
    val cornerEmblem: String? = null  // emoji shown at top-right corner
)

/**
 * Returns the TierVisualConfig for a given tier name.
 * Tier name is from GameViewModel.getTierName(level).
 */
fun getTierVisualConfig(tierName: String): TierVisualConfig {
    return when (tierName) {
        "Warrior" -> TierVisualConfig(
            name = "Warrior",
            primaryColor = Color(0xFF8B5CF6),  // Purple
            secondaryColor = Color(0xFF6366F1),  // Indigo
            borderWidth = 2.dp
        )
        "Elite" -> TierVisualConfig(
            name = "Elite",
            primaryColor = Color(0xFF3B82F6),  // Blue
            secondaryColor = Color(0xFF06B6D4),  // Cyan
            borderWidth = 3.dp,
            hasCornerAccents = true
        )
        "Master" -> TierVisualConfig(
            name = "Master",
            primaryColor = Color(0xFF14B8A6),  // Teal
            secondaryColor = Color(0xFF10B981),  // Emerald
            borderWidth = 3.dp
        )
        "Grandmaster" -> TierVisualConfig(
            name = "Grandmaster",
            primaryColor = Color(0xFFF59E0B),  // Gold
            secondaryColor = Color(0xFFFCD34D),  // Amber
            borderWidth = 4.dp,
            hasGlow = true
        )
        "Epic" -> TierVisualConfig(
            name = "Epic",
            primaryColor = Color(0xFFDC2626),  // Crimson
            secondaryColor = Color(0xFFEC4899),  // Pink
            borderWidth = 4.dp,
            hasGlow = true,
            hasPulsingGlow = true
        )
        "Legend" -> TierVisualConfig(
            name = "Legend",
            primaryColor = Color(0xFFFFFFFF),  // White
            secondaryColor = Color(0xFFF59E0B),  // Gold
            borderWidth = 4.dp,
            hasGlow = true,
            hasRotatingRing = true
        )
        "Mythic" -> TierVisualConfig(
            name = "Mythic",
            primaryColor = Color(0xFFDC2626),  // Crimson
            secondaryColor = Color(0xFFF59E0B),  // Gold
            borderWidth = 5.dp,
            hasGlow = true,
            hasRotatingRing = true,
            hasParticles = true
        )
        "Mythic Honor" -> TierVisualConfig(
            name = "Mythic Honor",
            primaryColor = Color(0xFFF59E0B),  // Gold
            secondaryColor = Color(0xFFDC2626),  // Crimson
            borderWidth = 5.dp,
            hasGlow = true,
            hasRotatingRing = true,
            hasParticles = true,
            hasDoubleRing = true
        )
        "Mythic Glory" -> TierVisualConfig(
            name = "Mythic Glory",
            primaryColor = Color(0xFFFFFFFF),  // White
            secondaryColor = Color(0xFFDC2626),  // Crimson
            borderWidth = 5.dp,
            hasGlow = true,
            hasRotatingRing = true,
            hasParticles = true,
            hasDoubleRing = true,
            hasSparkles = true
        )
        "Mythic Immortal" -> TierVisualConfig(
            name = "Mythic Immortal",
            primaryColor = Color(0xFFF59E0B),  // Gold
            secondaryColor = Color(0xFFFFFFFF),  // White
            borderWidth = 5.dp,
            hasGlow = true,
            hasPulsingGlow = true,
            hasRotatingRing = true,
            hasParticles = true,
            hasDoubleRing = true,
            hasSparkles = true,
            hasCrownEmblem = true,
            cornerEmblem = "👑"
        )
        else -> TierVisualConfig(  // Fallback (unknown tier)
            name = "Unknown",
            primaryColor = TextMuted,
            secondaryColor = TextLight,
            borderWidth = 2.dp
        )
    }
}

/**
 * Tier Profile Avatar — square rounded 16dp with progressive tier border effects.
 *
 * @param profileImagePath Local file path to uploaded photo (null = default 👑 avatar)
 * @param tierName Tier name from GameViewModel.getTierName(level)
 * @param sizeDp Size of the avatar in dp (default 120 for profile screen)
 * @param showEditBadge Show camera emoji badge at bottom-right (for edit action)
 */
@Composable
fun TierProfileAvatar(
    profileImagePath: String?,
    tierName: String,
    sizeDp: Dp = 120.dp,
    showEditBadge: Boolean = false,
    modifier: Modifier = Modifier
) {
    val config = remember(tierName) { getTierVisualConfig(tierName) }
    val cornerRadius = 16.dp

    // Load bitmap from local file (if exists)
    var bitmap by remember(profileImagePath) {
        mutableStateOf<Bitmap?>(null)
    }
    LaunchedEffect(profileImagePath) {
        bitmap = null
        if (profileImagePath != null) {
            val file = File(profileImagePath)
            if (file.exists()) {
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    runCatching {
                        BitmapFactory.decodeFile(file.absolutePath)
                    }.getOrNull()
                }?.let { bitmap = it }
            }
        }
    }

    // ─── Animations ───
    val infiniteTransition = rememberInfiniteTransition(label = "tier_avatar")

    // Rotating ring angle (8s slow rotation)
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    // Counter-rotating ring for Mythic Honor+ (double ring)
    val ringRotationReverse by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation_reverse"
    )

    // Pulsing glow (Epic+ & Immortal)
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Particle drift (Mythic+)
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_phase"
    )

    // Sparkle twinkle (Glory+)
    val sparklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_phase"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(sizeDp + 16.dp)  // Extra space for effects
    ) {
        // ─── Layer 1: Outer glow (for Grandmaster+) ───
        if (config.hasGlow) {
            val glowAlpha = if (config.hasPulsingGlow) pulseAlpha else 0.4f
            Box(
                modifier = Modifier
                    .size(sizeDp + 12.dp)
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    config.primaryColor.copy(alpha = glowAlpha),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = size.width / 1.4f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                (cornerRadius + 6.dp).toPx(),
                                (cornerRadius + 6.dp).toPx()
                            )
                        )
                    }
            )
        }

        // ─── Layer 2: Rotating shimmer ring (Legend+) ───
        if (config.hasRotatingRing) {
            Canvas(
                modifier = Modifier
                    .size(sizeDp + 8.dp)
                    .graphicsLayer { rotationZ = ringRotation }
            ) {
                val strokeWidth = 2.dp.toPx()
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(
                            config.primaryColor.copy(alpha = 0.9f),
                            config.secondaryColor.copy(alpha = 0.5f),
                            config.primaryColor.copy(alpha = 0.9f)
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 300f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // ─── Layer 2b: Counter-rotating ring (Mythic Honor+) ───
        if (config.hasDoubleRing) {
            Canvas(
                modifier = Modifier
                    .size(sizeDp + 4.dp)
                    .graphicsLayer { rotationZ = ringRotationReverse }
            ) {
                val strokeWidth = 1.5.dp.toPx()
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(
                            config.secondaryColor.copy(alpha = 0.8f),
                            Color.Transparent,
                            config.secondaryColor.copy(alpha = 0.8f)
                        )
                    ),
                    startAngle = 45f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // ─── Layer 3: Particles orbit (Mythic+) ───
        if (config.hasParticles) {
            Canvas(modifier = Modifier.size(sizeDp + 14.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val orbitRadius = size.width / 2f - 2f
                val particleCount = 6
                for (i in 0 until particleCount) {
                    val angle = Math.toRadians((particlePhase + i * (360f / particleCount)).toDouble())
                    val x = center.x + (orbitRadius * cos(angle)).toFloat()
                    val y = center.y + (orbitRadius * sin(angle)).toFloat()
                    val pAlpha = 0.5f + 0.5f * sin(Math.toRadians((particlePhase + i * 60f).toDouble())).toFloat()
                    drawCircle(
                        color = config.secondaryColor.copy(alpha = pAlpha.coerceIn(0.2f, 0.9f)),
                        radius = 2.5f,
                        center = Offset(x, y)
                    )
                }
            }
        }

        // ─── Layer 4: Sparkles (Mythic Glory+) ───
        if (config.hasSparkles) {
            Canvas(modifier = Modifier.size(sizeDp + 12.dp)) {
                val sparkleCount = 8
                for (i in 0 until sparkleCount) {
                    val seed = i * 45
                    val angle = Math.toRadians((seed + sparklePhase * 0.5).toDouble())
                    val r = size.width / 2f - 4f
                    val cx = size.width / 2f + (r * cos(angle)).toFloat()
                    val cy = size.height / 2f + (r * sin(angle)).toFloat()
                    val twinkle = (sin(Math.toRadians((sparklePhase + seed).toDouble())) * 0.5 + 0.5)
                    if (twinkle > 0.5) {
                        // Draw 4-point star
                        val starSize = (3f + 2f * twinkle.toFloat())
                        drawLine(
                            color = Color.White.copy(alpha = twinkle.toFloat()),
                            start = Offset(cx - starSize, cy),
                            end = Offset(cx + starSize, cy),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = Color.White.copy(alpha = twinkle.toFloat()),
                            start = Offset(cx, cy - starSize),
                            end = Offset(cx, cy + starSize),
                            strokeWidth = 1f
                        )
                    }
                }
            }
        }

        // ─── Layer 5: Main avatar box with border ───
        Box(
            modifier = Modifier
                .size(sizeDp)
                .shadow(
                    elevation = if (config.hasGlow) 16.dp else 8.dp,
                    shape = RoundedCornerShape(cornerRadius),
                    ambientColor = config.primaryColor.copy(alpha = 0.5f),
                    spotColor = config.secondaryColor.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    Brush.radialGradient(
                        listOf(
                            config.primaryColor.copy(alpha = 0.2f),
                            DarkBackground
                        )
                    )
                )
                .border(
                    width = config.borderWidth,
                    brush = if (config.primaryColor == config.secondaryColor) {
                        Brush.linearGradient(listOf(config.primaryColor, config.primaryColor))
                    } else {
                        Brush.linearGradient(
                            listOf(config.primaryColor, config.secondaryColor, config.primaryColor)
                        )
                    },
                    shape = RoundedCornerShape(cornerRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Photo or default emoji
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Profile Photo",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Default avatar emoji based on tier
                val defaultEmoji = when {
                    config.hasCrownEmblem -> "🤴"
                    tierName.startsWith("Mythic") -> "🧙"
                    tierName == "Legend" -> "🌟"
                    tierName == "Epic" -> "🔥"
                    tierName == "Grandmaster" -> "👑"
                    tierName == "Master" -> "🎓"
                    tierName == "Elite" -> "🛡️"
                    else -> "🧕"
                }
                Text(text = defaultEmoji, fontSize = (sizeDp.value * 0.45f).sp)
            }
        }

        // ─── Layer 6: Corner accents (Elite+) ───
        if (config.hasCornerAccents) {
            Canvas(modifier = Modifier.size(sizeDp)) {
                val strokeW = 2.dp.toPx()
                val cornerLen = 12.dp.toPx()
                val color = config.secondaryColor
                // Top-left bracket
                drawLine(color, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW, StrokeCap.Round)
                drawLine(color, Offset(0f, 0f), Offset(0f, cornerLen), strokeW, StrokeCap.Round)
                // Top-right
                drawLine(color, Offset(size.width - cornerLen, 0f), Offset(size.width, 0f), strokeW, StrokeCap.Round)
                drawLine(color, Offset(size.width, 0f), Offset(size.width, cornerLen), strokeW, StrokeCap.Round)
                // Bottom-left
                drawLine(color, Offset(0f, size.height - cornerLen), Offset(0f, size.height), strokeW, StrokeCap.Round)
                drawLine(color, Offset(0f, size.height), Offset(cornerLen, size.height), strokeW, StrokeCap.Round)
                // Bottom-right
                drawLine(color, Offset(size.width - cornerLen, size.height), Offset(size.width, size.height), strokeW, StrokeCap.Round)
                drawLine(color, Offset(size.width, size.height - cornerLen), Offset(size.width, size.height), strokeW, StrokeCap.Round)
            }
        }

        // ─── Layer 7: Crown emblem at top (Mythic Immortal) ───
        if (config.hasCrownEmblem) {
            Box(
                modifier = Modifier
                    .size(sizeDp + 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "👑",
                    fontSize = 20.sp,
                    modifier = Modifier.graphicsLayer { translationY = -8f }
                )
            }
        }

        // ─── Layer 8: Edit badge (optional) ───
        if (showEditBadge) {
            Box(
                modifier = Modifier
                    .size(sizeDp + 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(6.dp, RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(GradientGreenGold))
                        .border(2.dp, DarkBackground, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📷", fontSize = 14.sp)
                }
            }
        }
    }
}

// SmallTierAvatar — uses fillMaxSize() directly

// ═══════════════════════════════════════════════════════════════
// SMALL TIER AVATAR — Simplified version for top bars (40dp)
// Only shows border color + photo, no animations (perf-friendly)
// ═══════════════════════════════════════════════════════════════

@Composable
fun SmallTierAvatar(
    profileImagePath: String?,
    tierName: String,
    sizeDp: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val config = remember(tierName) { getTierVisualConfig(tierName) }
    val cornerRadius = 10.dp

    var bitmap by remember(profileImagePath) {
        mutableStateOf<Bitmap?>(null)
    }
    LaunchedEffect(profileImagePath) {
        bitmap = null
        if (profileImagePath != null) {
            val file = File(profileImagePath)
            if (file.exists()) {
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    runCatching { BitmapFactory.decodeFile(file.absolutePath) }.getOrNull()
                }?.let { bitmap = it }
            }
        }
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .shadow(4.dp, RoundedCornerShape(cornerRadius), ambientColor = config.primaryColor.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(cornerRadius))
            .background(DarkBackground)
            .border(
                width = if (config.borderWidth > 2.dp) 2.dp else config.borderWidth,
                brush = Brush.linearGradient(listOf(config.primaryColor, config.secondaryColor)),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Profile",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("🧕", fontSize = (sizeDp.value * 0.5f).sp)
        }
    }
}
