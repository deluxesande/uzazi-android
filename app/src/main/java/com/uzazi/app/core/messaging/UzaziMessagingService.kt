package com.uzazi.app.core.messaging

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.notifications.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UzaziMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var secureStorage: SecureStorage

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onNewToken(token: String) {
        Log.d("FCM", "New token: $token")
        secureStorage.saveString("fcm_token", token)
        val userId = secureStorage.getString(SecureStorage.KEY_USER_ID)
        if (userId != null) {
            firestore.collection("users").document(userId)
                .update("fcmToken", token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"]
        when (type) {
            "checkin_reminder" -> NotificationHelper.showCheckInReminder(this)
            "absence_nudge" -> {
                val days = message.data["daysAbsent"]?.toIntOrNull() ?: 1
                NotificationHelper.showAbsenceNudge(this, days)
            }
            "badge_unlocked" -> {
                val name = message.data["badgeName"] ?: "New Badge"
                val emoji = message.data["badgeEmoji"] ?: "🏆"
                NotificationHelper.showBadgeUnlocked(this, name, emoji)
            }
            "chw_message" -> {
                val body = message.notification?.body ?: message.data["body"] ?: "New message from CHW"
                NotificationHelper.showChwMessage(this, body)
            }
            "night_companion" -> {
                // Open night companion deep link logic is handled by system if notification has click_action
                // or we can manually show a notification here.
            }
        }
    }
}
