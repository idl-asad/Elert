package com.example.elert.notification

import android.app.Notification
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

/**
 * Extracts human-readable title/body from notification extras.
 *
 * Messaging apps (e.g. WhatsApp) often put a summary like "2 new messages" in [Notification.EXTRA_TEXT]
 * while the latest message lives in [Notification.EXTRA_MESSAGES] or [Notification.EXTRA_TEXT_LINES].
 */
object NotificationTextExtractor {

    private val NEW_MESSAGES_SUMMARY = Regex("""\d+\s+new messages?""", RegexOption.IGNORE_CASE)

    fun extractTitle(extras: Bundle): String {
        return extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty().trim()
    }

    fun extractBody(extras: Bundle): String {
        extractFromMessagingMessages(extras)?.let { return it }
        extractFromTextLines(extras)?.let { return it }
        extractFromAndroidMessagesKey(extras)?.let { return it }

        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()?.trim()
        if (!bigText.isNullOrBlank() && !isGenericSummary(bigText)) return bigText

        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim()
        if (!text.isNullOrBlank() && !isGenericSummary(text)) return text

        return bigText
            ?: text
            ?: extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString().orEmpty().trim()
    }

    /** Text to use for keyword matching: title + body + any message lines. */
    fun extractMatchableText(extras: Bundle): String {
        val parts = linkedSetOf<String>()
        extractTitle(extras).takeIf { it.isNotBlank() }?.let { parts.add(it) }
        collectAllMessageTexts(extras).forEach { parts.add(it) }
        extractBody(extras).takeIf { it.isNotBlank() }?.let { parts.add(it) }
        return parts.joinToString(" ")
    }

    private fun extractFromMessagingMessages(extras: Bundle): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return null
        val messages = extras.getParcelableArrayCompat(Notification.EXTRA_MESSAGES) ?: return null
        return messages.lastMessageText()
    }

    private fun extractFromAndroidMessagesKey(extras: Bundle): String? {
        val messages = extras.getParcelableArrayCompat("android.messages") ?: return null
        return messages.lastMessageText()
    }

    private fun extractFromTextLines(extras: Bundle): String? {
        val lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES) ?: return null
        for (i in lines.indices.reversed()) {
            val line = lines[i]?.toString()?.trim()
            if (!line.isNullOrBlank() && !isGenericSummary(line)) return line
        }
        return lines.lastOrNull()?.toString()?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun collectAllMessageTexts(extras: Bundle): List<String> {
        val texts = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            extras.getParcelableArrayCompat(Notification.EXTRA_MESSAGES)?.forEach { parcelable ->
                messageTextFromBundle(parcelable as? Bundle)?.let { texts.add(it) }
            }
        }
        extras.getParcelableArrayCompat("android.messages")?.forEach { parcelable ->
            messageTextFromBundle(parcelable as? Bundle)?.let { texts.add(it) }
        }
        extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.forEach { line ->
            line?.toString()?.trim()?.takeIf { it.isNotBlank() }?.let { texts.add(it) }
        }
        return texts
    }

    private fun Array<Parcelable>.lastMessageText(): String? {
        for (i in indices.reversed()) {
            messageTextFromBundle(this[i] as? Bundle)?.let { return it }
        }
        return null
    }

    private fun messageTextFromBundle(bundle: Bundle?): String? {
        if (bundle == null) return null
        val text = bundle.getCharSequence("text")?.toString()?.trim()
            ?: bundle.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim()
        return text?.takeIf { it.isNotBlank() && !isGenericSummary(it) }
    }

    private fun isGenericSummary(text: String): Boolean {
        return NEW_MESSAGES_SUMMARY.containsMatchIn(text) ||
            text.equals("New message", ignoreCase = true) ||
            text.equals("New messages", ignoreCase = true)
    }

    private fun Bundle.getParcelableArrayCompat(key: String): Array<Parcelable>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArray(key, Parcelable::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableArray(key)
        }
    }
}
