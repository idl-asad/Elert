package com.example.elert.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.elert.data.model.NotificationPayload
import com.example.elert.notification.LastNotificationHolder
import com.example.elert.notification.NotificationCallFilter
import com.example.elert.notification.NotificationRuleProcessor
import com.example.elert.notification.NotificationTextExtractor

class ElertNotificationListenerService : NotificationListenerService() {

    private var listenerConnectedAtMs: Long = 0L

    override fun onListenerConnected() {
        super.onListenerConnected()
        listenerConnectedAtMs = System.currentTimeMillis()
        Log.i(TAG, "Notification listener connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            if (sbn.packageName == packageName) return
            if (isReplayedNotification(sbn)) return

            val notification = sbn.notification ?: return
            if (notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return

            val extras = notification.extras
            val title = NotificationTextExtractor.extractTitle(extras)
            val body = NotificationTextExtractor.extractBody(extras)

            if (NotificationCallFilter.isCallNotification(notification, title, body)) {
                Log.d(TAG, "Skipping call notification: ${sbn.packageName} | $title")
                return
            }

            val payload = NotificationPayload(
                packageName = sbn.packageName,
                title = title,
                body = body,
                postedAt = sbn.postTime
            )

            LastNotificationHolder.update(payload)
            Log.d(
                TAG,
                "Notification: ${payload.packageName} | ${payload.title} | ${payload.body}"
            )

            NotificationRuleProcessor.process(applicationContext, payload)
        } catch (error: Exception) {
            Log.e(TAG, "Failed to process notification from ${sbn.packageName}", error)
        }
    }

    /**
     * When the listener connects, Android re-delivers existing status-bar notifications.
     * Skip those so rules only run for notifications posted after connect.
     */
    private fun isReplayedNotification(sbn: StatusBarNotification): Boolean {
        if (listenerConnectedAtMs <= 0L) return false
        return sbn.postTime < listenerConnectedAtMs - REPLAY_GRACE_MS
    }

    companion object {
        private const val TAG = "ElertNotification"
        private const val REPLAY_GRACE_MS = 2_000L
    }
}
