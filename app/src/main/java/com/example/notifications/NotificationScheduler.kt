package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Schedules exact alarm notifications for each prayer time using AlarmManager.
 * Prayer times are persisted to SharedPreferences for rescheduling after reboot.
 *
 * Usage:
 *   NotificationScheduler.scheduleAdhanReminders(context, "Jakarta", timings)
 *   NotificationScheduler.cancelAdhanReminders(context)
 *   NotificationScheduler.setRemindersEnabled(context, true/false)
 */
object NotificationScheduler {

    private const val TAG = "NotificationScheduler"

    const val PREFS_NAME = "adhan_reminders"
    const val PREF_REMINDERS_ENABLED = "reminders_enabled"
    const val PREF_CITY = "city"
    private const val PREF_TIMINGS_PREFIX = "timing_"
    private const val PREF_DATE = "date"

    private const val CHANNEL_ID = "adhan_reminders"
    private const val CHANNEL_NAME = "Pengingat Adzan"

    // Prayer names (lowercase keys)
    private val PRAYER_NAMES = listOf("subuh", "dzuhur", "ashar", "maghrib", "isya")

    // ═══════════════════════════════════════════
    //  Channel setup
    // ═══════════════════════════════════════════

    fun createAdhanChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat waktu sholat"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    // ═══════════════════════════════════════════
    //  Schedule / Cancel
    // ═══════════════════════════════════════════

    /**
     * Schedule adhan reminders for all 5 prayers.
     * @param context app context
     * @param city city name (for notification text)
     * @param timings map of prayer name → "HH:mm" time string
     */
    fun scheduleAdhanReminders(context: Context, city: String, timings: Map<String, String>) {
        if (timings.isEmpty()) return

        createAdhanChannel(context)

        // Cancel existing alarms first
        cancelAlarms(context)

        // Persist to prefs for reboot reschedule
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(PREF_REMINDERS_ENABLED, true)
            .putString(PREF_CITY, city)
            .putString(PREF_DATE, LocalDate.now().toString())
            .apply()

        // Save each timing
        PRAYER_NAMES.forEach { prayer ->
            timings[prayer]?.let { timeStr ->
                prefs.edit().putString("$PREF_TIMINGS_PREFIX$prayer", timeStr).apply()
            }
        }

        // Schedule alarms
        scheduleAlarms(context, city, timings)

        Log.d(TAG, "Scheduled ${timings.size} adhan reminders for $city")
    }

    /**
     * Cancel all scheduled adhan reminders.
     */
    fun cancelAdhanReminders(context: Context) {
        cancelAlarms(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(PREF_REMINDERS_ENABLED, false).apply()
        Log.d(TAG, "Cancelled all adhan reminders")
    }

    /**
     * Enable/disable reminders without clearing saved timings.
     */
    fun setRemindersEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(PREF_REMINDERS_ENABLED, enabled).apply()
        if (!enabled) {
            cancelAlarms(context)
        } else {
            // Reschedule from saved timings
            val city = prefs.getString(PREF_CITY, "") ?: ""
            val timings = readTimingsFromPrefs(prefs)
            if (city.isNotEmpty() && timings.isNotEmpty()) {
                scheduleAlarms(context, city, timings)
            }
        }
    }

    fun isRemindersEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_REMINDERS_ENABLED, false)
    }

    // ═══════════════════════════════════════════
    //  Internal: AlarmManager operations
    // ═══════════════════════════════════════════

    internal fun scheduleAlarms(context: Context, city: String, timings: Map<String, String>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val today = LocalDate.now()

        PRAYER_NAMES.forEach { prayer ->
            val timeStr = timings[prayer] ?: return@forEach
            val notificationId = prayer.hashCode()

            try {
                val prayerTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
                var triggerDateTime = LocalDateTime.of(today, prayerTime)

                // If time already passed today, skip (don't schedule for tomorrow —
                // tomorrow's alarms will be set when prayer times are refreshed)
                if (triggerDateTime.toLocalTime().isBefore(LocalTime.now())) {
                    return@forEach
                }

                val triggerMillis = triggerDateTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val intent = Intent(context, AdhanReminderReceiver::class.java).apply {
                    putExtra(AdhanReminderReceiver.EXTRA_PRAYER_NAME, prayer)
                    putExtra(AdhanReminderReceiver.EXTRA_CITY, city)
                    putExtra(AdhanReminderReceiver.EXTRA_NOTIFICATION_ID, notificationId)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context, notificationId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Use setExactAndAllowWhileIdle for reliable delivery in Doze mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to schedule alarm for $prayer ($timeStr): ${e.message}")
            }
        }
    }

    private fun cancelAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        PRAYER_NAMES.forEach { prayer ->
            val notificationId = prayer.hashCode()
            val intent = Intent(context, AdhanReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    // ═══════════════════════════════════════════
    //  SharedPreferences helpers (for BootReceiver)
    // ═══════════════════════════════════════════

    internal fun readTimingsFromPrefs(prefs: SharedPreferences): Map<String, String> {
        val result = mutableMapOf<String, String>()
        PRAYER_NAMES.forEach { prayer ->
            prefs.getString("$PREF_TIMINGS_PREFIX$prayer", null)?.let {
                result[prayer] = it
            }
        }
        return result
    }
}
