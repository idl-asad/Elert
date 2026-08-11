package com.example.elert.notification

import android.app.Notification

/**
 * Detects phone/voice/video call notifications so they are not treated as chat messages.
 */
object NotificationCallFilter {

    private val CALL_PHRASES = listOf(
        "incoming call",
        "incoming voice call",
        "incoming video call",
        "voice call",
        "video call",
        "ringing",
        "missed call",
        "missed voice call",
        "missed video call",
        "ongoing call",
        "call in progress",
        "calling",
        "is calling"
    )

    fun isCallNotification(notification: Notification, title: String, body: String): Boolean {
        if (notification.category == Notification.CATEGORY_CALL) return true
        if (notification.category == Notification.CATEGORY_MISSED_CALL) return true

        val haystack = "$title $body".lowercase()
        if (CALL_PHRASES.any { haystack.contains(it) }) return true

        val channelId = notification.channelId?.lowercase().orEmpty()
        if (channelId.contains("call")) return true

        return false
    }
}
