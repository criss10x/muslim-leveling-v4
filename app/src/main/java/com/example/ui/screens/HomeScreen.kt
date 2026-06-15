package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    state: MuslimLevelingData
) {
    var showUnlogConfirm by remember { mutableStateOf<String?>(null) }
    val todayStr = LocalDate.now().toString()

    // Determine values for rings
    val wajibList = listOf("subuh", "dzuhur", "ashar", "maghrib", "isya")
    val checkedWajibToday = wajibList.count { p ->
        state.prayerLog.any { it.date == todayStr && it.prayer == p }
    }

    val isSantaiMode = state.user.intensityMode == "santai"
    val isSultanMode = state.user.intensityMode == "sultan"

    // Denominators
    val wajibDenominator = if (isSantaiMode) {
        state.user.santaiTrackedPrayers.size.coerceAtLeast(1)
    } else 5

    val checkedTrackedWajibToday = if (isSantaiMode) {
        state.user.santaiTrackedPrayers.count { p ->
            state.prayerLog.any { it.date == todayStr && it.prayer == p }
        }
    } else checkedWajibToday

    val wajibProgress = (checkedTrackedWajibToday.toFloat() / wajibDenominator.toFloat()).coerceIn(0f, 1f)

    // Sunnah rings
    val sunnahCount = state.prayerLog.count {
        it.date == todayStr && (it.prayer == "dhuha" || it.prayer == "rawatib" || it.prayer == "tahajjud")
    }
    val sunnahDenominator = 3f // target of 3 per day
    val sunnahProgress = (sunnahCount.toFloat() / sunnahDenominator).coerceIn(0f, 1f)

    // Tilawah rings
    val tilawahLogged = state.prayerLog.any { it.date == todayStr && it.prayer == "tilawah" }
    val tilawahProgress = if (tilawahLogged) 1f else 0f

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .muslimPattern()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp), // space for bottom switcher
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Nickname, rank, level
            GameHeaderView(state, viewModel)

            Spacer(modifier = Modifier.height(12.dp))

            // A) RITUAL RINGS
            Text(
                text = "RITUAL RING HARIAN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(DarkSurface.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                    .border(BorderStroke(1.dp, DarkSurfaceVariant), RoundedCornerShape(24.dp))
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: Concentric rings with overlay progress text
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(108.dp)
                    ) {
                        RitualRingsCanvas(
                            wajibProgress = wajibProgress,
                            sunnahProgress = sunnahProgress,
                            tilawahProgress = tilawahProgress,
                            showSunnahRing = isSultanMode,
                            modifier = Modifier.fillMaxSize().testTag("ritual_rings_canvas")
                        )
                        Text(
                            text = "$checkedTrackedWajibToday/$wajibDenominator",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                    }

                    // Right side: Countdown and tagline
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 18.dp)
                    ) {
                        val timer = getNextPrayerTimerInfo(state.prayerTimesCache.timings, checkedTrackedWajibToday, wajibDenominator)
                        Text(
                            text = timer.label,
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = timer.duration,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldAccent,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = timer.tagline,
                            fontSize = 10.sp,
                            color = TextLight.copy(alpha = 0.85f),
                            lineHeight = 14.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive clean index labels centered under the circle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RingLabelView(color = RingRed, name = "Wajib", value = "$checkedTrackedWajibToday/$wajibDenominator")
                    if (isSultanMode) {
                        RingLabelView(color = RingGreen, name = "Sunnah", value = "$sunnahCount/3")
                    }
                    RingLabelView(color = RingBlue, name = "Tilawah", value = if (tilawahLogged) "Lengkap" else "Belum")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // C) HERO STREAK CARD
            HeroStreakCard(state = state, isSantaiMode = isSantaiMode)

            Spacer(modifier = Modifier.height(20.dp))

            // B) JADWAL SHOLAT LIST
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "JADWAL SHOLAT HARI INI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.5.sp
                )
                val modeLabel = when (state.user.intensityMode) {
                    "santai" -> "Mode: Santai"
                    "sultan" -> "Mode: Sultan"
                    else -> "Mode: Standar"
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1F2937), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = modeLabel,
                        fontSize = 10.sp,
                        color = TextMuted,
                        style = androidx.compose.ui.text.TextStyle(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5 prayers list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val timings = state.prayerTimesCache.timings
                val prayers = listOf(
                    Pair("Subuh", timings.subuh),
                    Pair("Dzuhur", timings.dzuhur),
                    Pair("Ashar", timings.ashar),
                    Pair("Maghrib", timings.maghrib),
                    Pair("Isya", timings.isya)
                )

                prayers.forEach { (name, time) ->
                    val lowerName = name.lowercase()
                    val isChecked = state.prayerLog.any { it.date == todayStr && it.prayer == lowerName }
                    val isActive = isCurrentOrUpcoming(lowerName, timings)
                    val isTrackedInSantai = if (isSantaiMode) {
                        state.user.santaiTrackedPrayers.contains(lowerName)
                    } else true

                    PrayerRowCard(
                        name = name,
                        time = time,
                        isChecked = isChecked,
                        isActive = isActive,
                        isTrackedInSantai = isTrackedInSantai,
                        isSantaiMode = isSantaiMode,
                        onCheckedChange = { check ->
                            if (check) {
                                viewModel.logPrayer(lowerName, "wajib")
                            } else {
                                showUnlogConfirm = lowerName
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Side Quest Divider Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFF1F2937)))
                Text(
                    text = "SIDE QUEST",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), // Slate-500
                    letterSpacing = 2.sp
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFF1F2937)))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tilawah checkin
                val tilawahDone = state.prayerLog.any { it.date == todayStr && it.prayer == "tilawah" }
                SunnahActionCard(
                    title = "Tilawah & Dzikir Harian",
                    description = "Membaca Al-Qur'an / Dzikir Pagi & Petang",
                    icon = "📖",
                    isClaimed = tilawahDone,
                    accentColor = RingBlue,
                    onLog = { viewModel.logPrayer("tilawah", "tilawah") }
                )

                if (isSultanMode) {
                    val dhuhaDone = state.prayerLog.any { it.date == todayStr && it.prayer == "dhuha" }
                    val rawatibCount = state.prayerLog.count { it.date == todayStr && it.prayer == "rawatib" }
                    val tahajjudDone = state.prayerLog.any { it.date == todayStr && it.prayer == "tahajjud" }

                    SunnahActionCard(
                        title = "Sholat Dhuha",
                        description = "Sholat sunnah di pagi hari",
                        icon = "🌙",
                        isClaimed = dhuhaDone,
                        accentColor = RingGreen,
                        onLog = { viewModel.logPrayer("dhuha", "sunnah") }
                    )

                    SunnahActionCard(
                        title = "Sholat Sunnah Rawatib ($rawatibCount kali)",
                        description = "Sholat sunnah pengiring sholat fardhu",
                        icon = "✨",
                        isClaimed = false, // can log multiple times
                        accentColor = GoldAccent,
                        buttonLabel = "+ Catat Rawatib",
                        onLog = { viewModel.logPrayer("rawatib", "sunnah") }
                    )

                    SunnahActionCard(
                        title = "Sholat Tahajjud",
                        description = "Sholat sunnah sepertiga malam terakhir",
                        icon = "⭐",
                        isClaimed = tahajjudDone,
                        accentColor = CyanAccent,
                        onLog = { viewModel.logPrayer("tahajjud", "sunnah") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Unlog Confirm Modal
    if (showUnlogConfirm != null) {
        val prayerName = showUnlogConfirm!!
        AlertDialog(
            onDismissRequest = { showUnlogConfirm = null },
            title = { Text("Uncheck Sholat?", color = TextLight, fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin membatalkan catatan Sholat ${prayerName.capitalize()} hari ini? XP dan streak harian pendoa ini akan dikurangi.", color = TextLight) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.unlogPrayer(prayerName, todayStr)
                        showUnlogConfirm = null
                    }
                ) {
                    Text("Ya, Batalkan", color = RingRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlogConfirm = null }) {
                    Text("Tutup", color = TextLight)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

data class NextPrayerTimer(
    val label: String,
    val duration: String,
    val tagline: String
)

fun getNextPrayerTimerInfo(timings: Timings, currentWajib: Int, denominator: Int): NextPrayerTimer {
    val now = LocalTime.now()
    val prayerList = listOf(
        Pair("Subuh", timings.subuh),
        Pair("Dzuhur", timings.dzuhur),
        Pair("Ashar", timings.ashar),
        Pair("Maghrib", timings.maghrib),
        Pair("Isya", timings.isya)
    )

    for (p in prayerList) {
        try {
            val pTime = LocalTime.parse(p.second)
            if (now.isBefore(pTime)) {
                val diffMins = ChronoUnit.MINUTES.between(now, pTime)
                val hrs = diffMins / 60
                val mins = diffMins % 60
                val duration = String.format("%02d:%02d", hrs, mins)
                val label = "${p.first} dalam"
                val tagline = "Lengkapi ring Wajib hari ini biar streak gak putus, Bang!"
                return NextPrayerTimer(label, duration, tagline)
            }
        } catch (e: Exception) {
            // ignore
        }
    }
    return NextPrayerTimer("Subuh besok", timings.subuh, "Perjuangan hari ini selesai! Istirahat yang cukup ya, Bang.")
}

@Composable
fun GameHeaderView(state: MuslimLevelingData, viewModel: GameViewModel) {
    val levelInfo = viewModel.getLevelInfo(state.user.xp)
    val rankTitle = viewModel.getRankTitle(levelInfo.level)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Avatar with level in gold border
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(DarkSurfaceVariant, CircleShape)
                    .border(2.dp, GoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LV ${levelInfo.level}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextLight
                )
            }

            Column {
                Text(
                    text = rankTitle.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = state.user.username,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "XP: ${levelInfo.xpInCurrentLevel}/${levelInfo.xpNeededForNextLevel}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { levelInfo.progress },
                modifier = Modifier
                    .width(96.dp)
                    .height(6.dp)
                    .clip(CircleShape),
                color = IslamicGreen,
                trackColor = DarkSurfaceVariant
            )
        }
    }
}

@Composable
fun RitualRingsCanvas(
    wajibProgress: Float,
    sunnahProgress: Float,
    tilawahProgress: Float,
    showSunnahRing: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = center
        val strokeWidth = 8.dp.toPx()

        // Outer Ring: Wajib (Green in Sleek theme, to correspond with #00A86B)
        val outerRadius = (size.minDimension / 2) - 10.dp.toPx()
        drawCircle(
            color = RingGreen.copy(alpha = 0.1f),
            radius = outerRadius,
            center = center,
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            color = RingGreen,
            startAngle = -90f,
            sweepAngle = wajibProgress * 360f,
            useCenter = false,
            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
            size = Size(outerRadius * 2, outerRadius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        val middleRadius = if (showSunnahRing) {
            // Middle Ring: Sunnah (Gold Accent)
            val r = (size.minDimension / 2) - 22.dp.toPx()
            drawCircle(
                color = GoldAccent.copy(alpha = 0.1f),
                radius = r,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = GoldAccent,
                startAngle = -90f,
                sweepAngle = sunnahProgress * 360f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            r
        } else {
            outerRadius
        }

        // Inner Ring: Tilawah (Blue)
        val innerRadius = if (showSunnahRing) {
            (size.minDimension / 2) - 34.dp.toPx()
        } else {
            (size.minDimension / 2) - 22.dp.toPx()
        }

        drawCircle(
            color = RingBlue.copy(alpha = 0.1f),
            radius = innerRadius,
            center = center,
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            color = RingBlue,
            startAngle = -90f,
            sweepAngle = tilawahProgress * 360f,
            useCenter = false,
            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
            size = Size(innerRadius * 2, innerRadius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun RingLabelView(color: Color, name: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = "$name: ",
            fontSize = 11.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 11.sp,
            color = TextLight,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HeroStreakCard(state: MuslimLevelingData, isSantaiMode: Boolean) {
    val hero = state.heroStreak

    if (isSantaiMode) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .testTag("hero_streak_card_locked"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, Color(0xFF1F2937))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔒", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                    Column {
                        Text(
                            text = "Hero Streak [LOCKED]",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "Hero Streak terkunci di mode Santai — selesaikan semua 5 sholat wajib untuk mulai!",
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    } else {
        val heroGradient = Brush.linearGradient(
            colors = listOf(Color(0xFF00A86B), Color(0xFF004D31))
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .testTag("hero_streak_card_active"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .background(heroGradient)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "🕋",
                    fontSize = 110.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-20).dp),
                    color = Color.White.copy(alpha = 0.08f)
                )

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(text = "🔥", fontSize = 22.sp)
                        Text(
                            text = "SHOLAT 5/5 STREAK",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = TextLight,
                            letterSpacing = 1.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${hero.current} HARI",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = TextLight,
                                letterSpacing = (-1).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "REKOR TERBAIKMU: ${hero.best} HARI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLight.copy(alpha = 0.8f),
                                letterSpacing = 0.7.sp
                            )
                        }

                        val freezeText = if (hero.freezeAvailable) "FREEZE READY ❄" else "FREEZE USE COOLDOWN"
                        val freezeBgColor = if (hero.freezeAvailable) Color.Black.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f)
                        val freezeBorderColor = if (hero.freezeAvailable) Color.White.copy(alpha = 0.15f) else Color.Transparent

                        Box(
                            modifier = Modifier
                                .background(freezeBgColor, RoundedCornerShape(100.dp))
                                .border(BorderStroke(1.dp, freezeBorderColor), RoundedCornerShape(100.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = freezeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (hero.freezeAvailable) Color(0xFF38BDF8) else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "STREAK PEJUANG PER-SHOLAT:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf("subuh", "dzuhur", "ashar", "maghrib", "isya").forEach { prayer ->
                                    val streakObj = state.perPrayerStreaks[prayer] ?: StreakState()
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = prayer.substring(0, 1).uppercase() + prayer.substring(1, 3),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextLight.copy(alpha = 0.7f)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Text(text = "🔥", fontSize = 10.sp)
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "${streakObj.current}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = TextLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerRowCard(
    name: String,
    time: String,
    isChecked: Boolean,
    isActive: Boolean,
    isTrackedInSantai: Boolean,
    isSantaiMode: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val borderStroke = when {
        isChecked -> BorderStroke(1.dp, Color(0xFF374151).copy(alpha = 0.5f))
        isActive -> BorderStroke(2.dp, GoldAccent)
        else -> BorderStroke(1.dp, Color(0xFF1F2937))
    }

    val containerColor = when {
        isChecked -> DarkSurface.copy(alpha = 0.4f)
        isActive -> DarkSurface
        else -> DarkSurface.copy(alpha = 0.6f)
    }

    val textColor = when {
        isChecked -> TextLight.copy(alpha = 0.6f)
        isActive -> GoldAccent
        else -> TextLight
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(borderStroke, RoundedCornerShape(16.dp))
            .testTag("prayer_card_${name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    if (isActive) {
                        drawCircle(
                            Brush.radialGradient(
                                listOf(GoldAccent.copy(alpha = 0.08f), Color.Transparent),
                                center = Offset(size.width * 0.15f, size.height * 0.5f),
                                radius = size.height * 1.5f
                            )
                        )
                    }
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isChecked) IslamicGreen else Color.Transparent)
                        .border(
                            BorderStroke(
                                width = if (isChecked) 0.dp else 2.dp,
                                color = if (isActive) GoldAccent else Color(0xFF4B5563)
                            ),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onCheckedChange(!isChecked) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isChecked) {
                        Text(
                            text = "✓",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            fontSize = 15.sp,
                            fontWeight = if (isActive || isChecked) FontWeight.Bold else FontWeight.SemiBold,
                            color = textColor
                        )
                        if (isSantaiMode && !isTrackedInSantai) {
                            Text(
                                text = " (bonus)",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                    Text(
                        text = "Jam $time WID",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Text(
                text = time,
                fontSize = 13.sp,
                color = if (isActive) GoldAccent else TextMuted,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun SunnahActionCard(
    title: String,
    description: String,
    icon: String,
    isClaimed: Boolean,
    accentColor: Color,
    buttonLabel: String = "Catat Done",
    onLog: () -> Unit
) {
    val cardBg = accentColor.copy(alpha = 0.05f)
    val cardBorder = accentColor.copy(alpha = 0.15f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(BorderStroke(1.dp, cardBorder), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 18.sp)
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isClaimed) {
                Box(
                    modifier = Modifier
                        .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Selesai ✓",
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            } else {
                Button(
                    onClick = onLog,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = if (buttonLabel == "Catat Done") "+ Claim" else buttonLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

// Helpers
fun getNextPrayerCountdown(timings: Timings, currentWajib: Int, denominator: Int): String {
    val now = LocalTime.now()
    val prayerList = listOf(
        Pair("Subuh", timings.subuh),
        Pair("Dzuhur", timings.dzuhur),
        Pair("Ashar", timings.ashar),
        Pair("Maghrib", timings.maghrib),
        Pair("Isya", timings.isya)
    )

    for (p in prayerList) {
        try {
            val pTime = LocalTime.parse(p.second)
            if (now.isBefore(pTime)) {
                val diffMins = ChronoUnit.MINUTES.between(now, pTime)
                val timeNeeded = if (diffMins >= 60) {
                    val hrs = diffMins / 60
                    val mins = diffMins % 60
                    "$hrs jam $mins menit"
                } else {
                    "$diffMins menit"
                }
                return "${p.first} dalam $timeNeeded — lengkapi = ring Wajib jadi $currentWajib/$denominator!"
            }
        } catch (e: Exception) {
            // ignore
        }
    }
    return "Jadwal sholat fardhu selesai untuk hari ini. Subuh besok jam ${timings.subuh}."
}

fun isCurrentOrUpcoming(prayer: String, timings: Timings): Boolean {
    val now = LocalTime.now()

    val subuh = tryParsing(timings.subuh, LocalTime.of(4, 30))
    val dzuhur = tryParsing(timings.dzuhur, LocalTime.of(12, 0))
    val ashar = tryParsing(timings.ashar, LocalTime.of(15, 10))
    val maghrib = tryParsing(timings.maghrib, LocalTime.of(17, 50))
    val isya = tryParsing(timings.isya, LocalTime.of(19, 0))

    return when (prayer) {
        "subuh" -> now.isBefore(dzuhur) && (now.isAfter(subuh.minusMinutes(30)) || now.isBefore(subuh.plusMinutes(90)))
        "dzuhur" -> now.isAfter(subuh.plusMinutes(90)) && now.isBefore(ashar)
        "ashar" -> now.isAfter(dzuhur) && now.isBefore(maghrib)
        "maghrib" -> now.isAfter(ashar) && now.isBefore(isya)
        "isya" -> now.isAfter(maghrib) || now.isBefore(subuh.minusMinutes(30))
        else -> false
    }
}

fun tryParsing(timeStr: String, default: LocalTime): LocalTime {
    return try {
        LocalTime.parse(timeStr)
    } catch (e: Exception) {
        default
    }
}
