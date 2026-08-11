package com.example.elert.notification

import android.app.Notification
import android.os.Bundle
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotificationTextExtractorTest {

    @Test
    fun extractBody_prefersLastMessagingMessageOverSummaryText() {
        val message1 = Bundle().apply {
            putCharSequence("text", "hello")
        }
        val message2 = Bundle().apply {
            putCharSequence("text", "urgent call me")
        }
        val extras = Bundle().apply {
            putCharSequence(Notification.EXTRA_TEXT, "2 new messages")
            putParcelableArray(Notification.EXTRA_MESSAGES, arrayOf(message1, message2))
        }

        assertEquals("urgent call me", NotificationTextExtractor.extractBody(extras))
    }

    @Test
    fun extractBody_prefersLastInboxLineOverSummaryText() {
        val extras = Bundle().apply {
            putCharSequence(Notification.EXTRA_TEXT, "3 new messages")
            putCharSequenceArray(
                Notification.EXTRA_TEXT_LINES,
                arrayOf("Alice: hi", "Alice: urgent please")
            )
        }

        assertEquals("Alice: urgent please", NotificationTextExtractor.extractBody(extras))
    }

    @Test
    fun extractBody_fallsBackToExtraTextWhenNoStructuredMessages() {
        val extras = Bundle().apply {
            putCharSequence(Notification.EXTRA_TEXT, "Meeting in 5 min")
        }

        assertEquals("Meeting in 5 min", NotificationTextExtractor.extractBody(extras))
    }

    @Test
    fun extractMatchableText_includesTitleAndMessages() {
        val message = Bundle().apply {
            putCharSequence("text", "urgent")
        }
        val extras = Bundle().apply {
            putCharSequence(Notification.EXTRA_TITLE, "Mom")
            putCharSequence(Notification.EXTRA_TEXT, "2 new messages")
            putParcelableArray(Notification.EXTRA_MESSAGES, arrayOf(message))
        }

        val text = NotificationTextExtractor.extractMatchableText(extras)
        assert(text.contains("Mom"))
        assert(text.contains("urgent"))
    }
}
