package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// QUSHO GAMING THEME — Futuristic Neon Islamic Aesthetic v2
// ═══════════════════════════════════════════════════════════════

// ─── Core Backgrounds ───
val DarkBackground = Color(0xFF050811)       // Deeper space black
val DarkSurface = Color(0xFF0B0F1E)          // Card surface
val DarkSurfaceVariant = Color(0xFF13182B)   // Inner surface / borders
val DarkSurfaceElevated = Color(0xFF151A2F)  // Elevated cards

// ─── Islamic Neon Greens ───
val IslamicGreen = Color(0xFF00F0A0)         // Hyper neon emerald
val IslamicGreenDim = Color(0xFF00C27A)      // Muted green
val IslamicGreenGlow = Color(0x5500F0A0)     // Glow effect green

// ─── Gold and Amber ───
val GoldAccent = Color(0xFFFFE066)           // Bright gold
val GoldGlow = Color(0x40FFE066)             // Gold glow effect
val AmberFlame = Color(0xFFFFB020)           // Warm amber
val AmberGlow = Color(0x35FFB020)

// ─── Ritual Ring Colors (Neon) ───
val RingRed = Color(0xFFFF4D6D)              // Neon crimson
val RingRedGlow = Color(0x50FF4D6D)          // Red glow
val RingGreen = Color(0xFF00F0A0)            // Neon emerald
val RingGreenGlow = Color(0x5000F0A0)        // Green glow
val RingBlue = Color(0xFF00D4FF)             // Neon cyan-blue
val RingBlueGlow = Color(0x5000D4FF)         // Blue glow

// ─── Text Colors ───
val TextLight = Color(0xFFF5F8FF)            // Bright white-blue
val TextMuted = Color(0xFF7A82A3)            // Muted slate
val TextGold = Color(0xFFFFE066)

// ─── Accent Colors ───
val CyanAccent = Color(0xFF00E5FF)           // Neon cyan
val CyanGlow = Color(0x4000E5FF)
val OrangeFlame = Color(0xFFFF7A45)          // Flame orange
val PurpleNeon = Color(0xFFB760FF)           // Neon purple
val PurpleGlow = Color(0x40B760FF)
val PinkNeon = Color(0xFFFF5EB8)             // Neon pink
val PinkGlow = Color(0x40FF5EB8)

// ─── XP and Level Colors ───
val XpBarGreen = Color(0xFF00F0A0)
val XpBarTrack = Color(0xFF13182B)
val LevelUpGold = Color(0xFFFFE066)

// ─── Card Gradient Colors ───
val CardGradientStart = Color(0xFF0B0F1E)
val CardGradientEnd = Color(0xFF11162B)
val CardBorderGlow = Color(0x2200F0A0)

// ═══════════════════════════════════════════════════════════════
// GRADIENT COLOR SETS (for Brush construction)
// ═══════════════════════════════════════════════════════════════

val GradientGreenGold = listOf(IslamicGreen, GoldAccent)
val GradientCyanGreen = listOf(CyanAccent, IslamicGreen)
val GradientPurplePink = listOf(PurpleNeon, PinkNeon)
val GradientGoldAmber = listOf(GoldAccent, AmberFlame)
val GradientBlueCyan = listOf(RingBlue, CyanAccent)
val GradientRedPink = listOf(RingRed, PinkNeon)
val GradientDarkSurface = listOf(DarkSurface, DarkSurfaceElevated)

// ─── Neon shadow alpha helpers ───
fun Color.neonShadow(alpha: Float = 0.35f) = this.copy(alpha = alpha)
