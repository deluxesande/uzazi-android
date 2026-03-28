package com.uzazi.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.uzazi.app.MainActivity
import com.uzazi.app.R

object NotificationHelper {
    private const val CHANNEL_CHECKIN = "daily_checkin"
    private const val CHANNEL_ABSENCE = "wellness_check"
    private const val CHANNEL_BADGE = "badge_unlocked"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            val checkInChannel = NotificationChannel(
                CHANNEL_CHECKIN, "Daily check-in", NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Reminders to complete your daily wellness check-in" }

            val absenceChannel = NotificationChannel(
                CHANNEL_ABSENCE, "Wellness check", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Alerts when you haven't checked in for a few days" }

            val badgeChannel = NotificationChannel(
                CHANNEL_BADGE, "Badge unlocked", NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Celebrations when you earn a new badge" }

            notificationManager.createNotificationChannels(listOf(checkInChannel, absenceChannel, badgeChannel))
        }
    }

    private fun getDeepLinkIntent(context: Context, path: String): PendingIntent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("uzazi://open/$path"),
            context,
            MainActivity::class.java
        )
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun showCheckInReminder(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_CHECKIN)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Your garden is waiting 🌸")
            .setContentText("Take 2 minutes for yourself today, mama.")
            .setContentIntent(getDeepLinkIntent(context, "checkin"))
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1001, notification)
    }

    fun showAbsenceNudge(context: Context, daysAbsent: Int) {
        val body = when (daysAbsent) {
            1 -> "Mama Bear misses you 🌸 — your garden is waiting"
            2 -> "We've been thinking about you. How are you feeling today?"
            else -> "We miss you, mama. Even a quick tap helps your garden grow."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ABSENCE)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Thinking of you")
            .setContentText(body)
            .setContentIntent(getDeepLinkIntent(context, "checkin"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1002, notification)
    }

    fun showBadgeUnlocked(context: Context, badgeName: String, badgeEmoji: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_BADGE)
            .setSmallIcon(android.R.drawable.btn_star)
            .setContentTitle("$badgeEmoji You earned a badge!")
            .setContentText("You unlocked: $badgeName")
            .setContentIntent(getDeepLinkIntent(context, "badges"))
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1003, notification)
    }

    fun showChwMessage(context: Context, body: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ABSENCE)
            .setSmallIcon(android.R.drawable.ic_menu_send)
            .setContentTitle("Message from Health Worker")
            .setContentText(body)
            .setContentIntent(getDeepLinkIntent(context, "share"))
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1004, notification)
    }
}
