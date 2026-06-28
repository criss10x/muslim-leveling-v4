package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// NUR QUEST THEME — Gaming-Infused Modernism
// Redesign (2026-06-28). Tokens dari DESIGN.md Stitch.
// Palette: deep midnight surface + emerald quest + royal gold +
// mana cyan + epic red. Variable names dipertahankan untuk compat
// dengan 8 file Compose yang sudah reference nama lama.
// ═══════════════════════════════════════════════════════════════

// ─── Core Surfaces (deep midnight green-black, NOT pure black) ───
val DarkBackground = Color(0xFF0E1512)        // surface / background
val DarkSurface = Color(0xFF1A211E)           // surface-container
val DarkSurfaceVariant = Color(0xFF2F3632)    // surface-variant
val DarkSurfaceElevated = Color(0xFF242C28)   // surface-container-high

// ─── Primary: Emerald Quest (success, log, level-up) ───
val IslamicGreen = Color(0xFF42E5B1)          // primary (kept name)
val IslamicGreenDim = Color(0xFF00C897)       // primary-container
val IslamicGreenGlow = Color(0x5542E5B1)

// ─── Secondary: Royal Gold (rank, prestige, streak) ───
val GoldAccent = Color(0xFFFFE16D)            // secondary-fixed (kept name)
val GoldGlow = Color(0x40FFE16D)
val AmberFlame = Color(0xFFFFDB3C)            // secondary-container
val AmberGlow = Color(0x35FFDB3C)

// ─── Tertiary: Mana Cyan (prayer times, electric UI) ───
val CyanAccent = Color(0xFF00E1EF)            // tertiary (kept name)
val CyanGlow = Color(0x4000E1EF)

// ─── Status: Epic Red (missed, low energy, warning) ───
val RingRed = Color(0xFFFFB4AB)               // error (kept name)
val RingRedGlow = Color(0x50FFB4AB)
val RingGreen = Color(0xFF42E5B1)             // unified to primary
val RingGreenGlow = Color(0x5042E5B1)
val RingBlue = Color(0xFF00E1EF)              // unified to tertiary
val RingBlueGlow = Color(0x5000E1EF)

// ─── Text Colors ───
val TextLight = Color(0xFFDCE4DE)             // on-surface
val TextMuted = Color(0xFFBACAC1)             // on-surface-variant
val TextGold = Color(0xFFFFE16D)

// ─── Accent Colors (kept for compat; redirect ke palette baru) ───
val OrangeFlame = Color(0xFFFFDB3C)
val PurpleNeon = Color(0xFF42E5B1)            // ponytail: redirect ke primary, gak dipakai di mockup
val PurpleGlow = Color(0x4042E5B1)
val PinkNeon = Color(0xFFFFB4AB)              // ponytail: redirect ke error
val PinkGlow = Color(0x40FFB4AB)

// ─── XP and Level Colors ───
val XpBarGreen = Color(0xFF42E5B1)
val XpBarTrack = Color(0xFF2F3632)            // surface-container-highest
val LevelUpGold = Color(0xFFFFE16D)

// ─── Card Gradient Colors ───
val CardGradientStart = Color(0xFF1A211E)     // surface-container
val CardGradientEnd = Color(0xFF242C28)       // surface-container-high
val CardBorderGlow = Color(0x2242E5B1)

// ─── Outline (borders) ───
val OutlineDefault = Color(0xFF85948C)        // outline
val OutlineVariant = Color(0xFF3C4A43)        // outline-variant

// ═══════════════════════════════════════════════════════════════
// GRADIENT COLOR SETS (for Brush construction)
// ═══════════════════════════════════════════════════════════════

val GradientGreenGold = listOf(IslamicGreen, GoldAccent)
val GradientCyanGreen = listOf(CyanAccent, IslamicGreen)
val GradientPurplePink = listOf(IslamicGreen, CyanAccent)   // ponytail: redirect ke teal-cyan, gak dipakai di mockup
val GradientGoldAmber = listOf(GoldAccent, AmberFlame)
val GradientBlueCyan = listOf(RingBlue, CyanAccent)
val GradientRedPink = listOf(RingRed, PinkNeon)
val GradientDarkSurface = listOf(DarkSurface, DarkSurfaceElevated)

// ─── Glow alpha helpers ───
fun Color.neonShadow(alpha: Float = 0.35f) = this.copy(alpha = alpha)
