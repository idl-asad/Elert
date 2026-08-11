package com.example.elert.domain

import com.example.elert.data.model.ActiveHours
import com.example.elert.data.model.NotificationPayload
import com.example.elert.data.model.Rule
import java.time.LocalTime

object RuleMatcher {

    fun matches(
        rule: Rule,
        notification: NotificationPayload,
        now: LocalTime = LocalTime.now()
    ): Boolean {
        if (!rule.isEnabled) return false
        if (rule.packageName != notification.packageName) return false
        if (!isWithinActiveHours(rule.activeHours, now)) return false

        val keywords = RuleValidation.normalizeItems(rule.keywords)
        val contacts = RuleValidation.normalizeItems(rule.contacts)
        if (keywords.isEmpty() && contacts.isEmpty()) return false

        val haystack = "${notification.title} ${notification.body}".lowercase().trim()
        if (haystack.isEmpty()) return false

        val contactMatch = contacts.any { haystack.contains(it.lowercase()) }
        val keywordMatch = keywords.any { haystack.contains(it.lowercase()) }

        return when {
            keywords.isNotEmpty() && contacts.isNotEmpty() -> contactMatch && keywordMatch
            contacts.isNotEmpty() -> contactMatch
            else -> keywordMatch
        }
    }

    fun isWithinActiveHours(activeHours: ActiveHours, now: LocalTime): Boolean {
        val start = activeHours.startHour.coerceIn(0, 23)
        val end = activeHours.endHour.coerceIn(0, 23)
        val hour = now.hour

        return if (start <= end) {
            hour in start..end
        } else {
            hour >= start || hour <= end
        }
    }
}
