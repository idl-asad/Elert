package com.example.elert.notification

import android.app.Notification
import android.content.Context
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NotificationCallFilterTest {

    private fun buildNotification(title: String, body: String, category: String? = null): Notification {
        val context: Context = RuntimeEnvironment.getApplication()
        val builder = Notification.Builder(context, "ch")
            .setContentTitle(title)
            .setContentText(body)
        if (category != null) builder.setCategory(category)
        return builder.build()
    }

    @Test
    fun isCallNotification_detectsCallCategory() {
        val notification = buildNotification("Mom", "hello", Notification.CATEGORY_CALL)

        assertTrue(NotificationCallFilter.isCallNotification(notification, "Mom", "hello"))
    }

    @Test
    fun isCallNotification_detectsIncomingCallPhrase() {
        val notification = buildNotification("Mom", "Incoming voice call")

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
        val notification = buildNotification("Mom", "see you soon")

        assertFalse(
            NotificationCallFilter.isCallNotification(notification, "Mom", "see you soon")
        )
    }
}
