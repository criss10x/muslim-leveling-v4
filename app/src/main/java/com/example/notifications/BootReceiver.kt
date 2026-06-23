package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Reschedules adhan reminders after device reboot.
 * Reads the saved prayer times from SharedPreferences and reschedules
 * all alarms via NotificationScheduler.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Log.d("BootReceiver", "Boot completed — rescheduling adhan reminders")

        // Only reschedule if reminders were enabled
        val prefs = context.getSharedPreferences(NotificationScheduler.PREFS_NAME, Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(NotificationScheduler.PREF_REMINDERS_ENABLED, false)

        if (!enabled) return

        val city = prefs.getString(NotificationScheduler.PREF_CITY, "") ?: ""
        if (city.isEmpty()) return

        // Read saved timings
        val timings = NotificationScheduler.readTimingsFromPrefs(prefs)
        if (timings.isEmpty()) return

        // Reschedule all alarms
        NotificationScheduler.scheduleAlarms(context, city, timings)
        Log.d("BootReceiver", "Rescheduled ${timings.size} adhan reminders for $city")
    }
}
