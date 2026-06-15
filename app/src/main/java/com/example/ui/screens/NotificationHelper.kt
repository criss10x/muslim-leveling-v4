package com.example.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "muslim_leveling_notifications"
    private const val CHANNEL_NAME = "Muslim Leveling Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "Reminders for prayer and streaks in Muslim Leveling game"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendTestNotification(context: Context, mode: String) {
        val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (!permissionGranted) {
            Toast.makeText(context, "Izin notifikasi tidak aktif. Silahkan aktifkan di pengaturan HP Anda.", Toast.LENGTH_LONG).show()
            return
        }

        val message = when (mode) {
            "fokus" -> "Mode Fokus aktif! Hanya akan diingatkan sholat Subuh dan 15 menit sebelum sholat berikutnya."
            "seimbang" -> "Mode Seimbang aktif! Pengingat semua waktu sholat wajib 15 menit sebelum adzan."
            "intensif" -> "Mode Intensif aktif! Gamer akan diingatkan 30 menit dan 5 menit sebelum sholat dimulai. Pertahankan streak!"
            else -> "Notikasi Muslim Leveling siap dikirim!"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(" Muslim Leveling Mode: ${mode.capitalize()}")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(99, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
