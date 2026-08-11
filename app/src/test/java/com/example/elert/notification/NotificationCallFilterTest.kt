package com.example.elert.notification

import android.app.Notification
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotificationCallFilterTest {

    @Test
    fun isCallNotification_detectsCallCategory() {
        val notification = Notification.Builder("test", "ch")
            .setContentTitle("Mom")
            .setContentText("hello")
            .setCategory(Notification.CATEGORY_CALL)
            .build()

        assertTrue(NotificationCallFilter.isCallNotification(notification, "Mom", "hello"))
    }

    @Test
    fun isCallNotification_detectsIncomingCallPhrase() {
        val notification = Notification.Builder("test", "ch")
            .setContentTitle("Mom")
            .setContentText("Incoming voice call")
            .build()

        assertTrue(
            NotificationCallFilter.isCallNotification(
                notification,
                "Mom",
                "Incoming voice call"
            )
        )
    }

    @Test
    fun isCallNotification_allowsNormalMessage() {
        val notification = Notification.Builder("test", "ch")
            .setContentTitle("Mom")
            .setContentText("see you soon")
            .build()

        assertFalse(
            NotificationCallFilter.isCallNotification(notification, "Mom", "see you soon")
        )
    }
}
