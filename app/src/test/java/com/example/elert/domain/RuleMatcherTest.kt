package com.example.elert.domain

import com.example.elert.data.model.ActiveHours
import com.example.elert.data.model.NotificationPayload
import com.example.elert.data.model.Rule
import java.time.LocalTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RuleMatcherTest {

    @Test
    fun matches_returnsTrue_forEnabledRuleWithMatchingPackageKeywordAndHour() {
        val rule = baseRule(keywords = listOf("urgent"))
        val notification = NotificationPayload(
            packageName = "com.whatsapp",
            title = "Mom",
            body = "urgent call me now"
        )

        val result = RuleMatcher.matches(rule, notification, LocalTime.of(10, 0))

        assertTrue(result)
    }

    @Test
    fun matches_returnsFalse_whenRuleDisabled() {
        val rule = baseRule(isEnabled = false)
        val notification = baseNotification()

        val result = RuleMatcher.matches(rule, notification, LocalTime.of(10, 0))

        assertFalse(result)
    }

    @Test
    fun matches_returnsFalse_whenPackageDoesNotMatch() {
        val rule = baseRule(packageName = "com.whatsapp")
        val notification = baseNotification(packageName = "com.slack")

        val result = RuleMatcher.matches(rule, notification, LocalTime.of(10, 0))

        assertFalse(result)
    }

    @Test
    fun matches_returnsFalse_whenNoKeywordOrContactMatches() {
        val rule = baseRule(keywords = listOf("urgent"))
        val notification = baseNotification(title = "Mom", body = "call me maybe")

        val result = RuleMatcher.matches(rule, notification, LocalTime.of(10, 0))

        assertFalse(result)
    }

    @Test
    fun matches_anyKeywordOrLogic() {
        val rule = baseRule(keywords = listOf("urgent", "asap"))
        val notification = baseNotification(body = "reply asap please")

        assertTrue(RuleMatcher.matches(rule, notification, LocalTime.of(10, 0)))
    }

    @Test
    fun matches_contactOnlyRule() {
        val rule = baseRule(keywords = emptyList(), contacts = listOf("Mom"))
        val notification = baseNotification(title = "Mom", body = "hello")

        assertTrue(RuleMatcher.matches(rule, notification, LocalTime.of(10, 0)))
    }

    @Test
    fun matches_contactOnlyRule_doesNotMatch_unrelatedNotification() {
        val rule = baseRule(keywords = emptyList(), contacts = listOf("Mom"))
        val notification = baseNotification(title = "Alice", body = "hello there")

        assertFalse(RuleMatcher.matches(rule, notification, LocalTime.of(10, 0)))
    }

    @Test
    fun matches_keywordOnlyRule_doesNotMatch_unrelatedNotification() {
        val rule = baseRule(keywords = listOf("urgent"), contacts = emptyList())
        val notification = baseNotification(title = "Mom", body = "hello there")

        assertFalse(RuleMatcher.matches(rule, notification, LocalTime.of(10, 0)))
    }

    @Test
    fun matches_whenBothPresent_requiresContactAndKeyword() {
        val rule = baseRule(keywords = listOf("urgent"), contacts = listOf("Dad"))
        val unrelated = baseNotification(title = "Alice", body = "hello")

        assertFalse(RuleMatcher.matches(rule, unrelated, LocalTime.of(10, 0)))
    }

    @Test
    fun matches_whenBothPresent_contactOnlyIsNotEnough() {
        val rule = baseRule(keywords = listOf("urgent"), contacts = listOf("Dad"))
        val notification = baseNotification(title = "Dad", body = "ping")

        assertFalse(RuleMatcher.matches(rule, notification, LocalTime.of(10, 0)))
    }

    @Test
    fun matches_whenBothPresent_contactAndKeywordMatch() {
        val rule = baseRule(keywords = listOf("urgent"), contacts = listOf("Dad"))
        val notification = baseNotification(title = "Dad", body = "this is urgent")

        assertTrue(RuleMatcher.matches(rule, notification, LocalTime.of(10, 0)))
    }

    @Test
    fun matches_isCaseInsensitiveForKeyword() {
        val rule = baseRule(keywords = listOf("URGENT"))
        val notification = baseNotification(body = "please urgent reply")

        val result = RuleMatcher.matches(rule, notification, LocalTime.of(10, 0))

        assertTrue(result)
    }

    @Test
    fun matches_returnsFalse_whenOutsideSameDayActiveHours() {
        val rule = baseRule(activeHours = ActiveHours(startHour = 9, endHour = 17))
        val notification = baseNotification()

        val result = RuleMatcher.matches(rule, notification, LocalTime.of(20, 0))

        assertFalse(result)
    }

    @Test
    fun matches_supportsCrossMidnightHours() {
        val rule = baseRule(activeHours = ActiveHours(startHour = 22, endHour = 6))
        val notification = baseNotification()

        assertTrue(RuleMatcher.matches(rule, notification, LocalTime.of(23, 0)))
        assertTrue(RuleMatcher.matches(rule, notification, LocalTime.of(3, 0)))
        assertFalse(RuleMatcher.matches(rule, notification, LocalTime.of(12, 0)))
    }

    @Test
    fun matches_returnsFalse_whenNoKeywordsOrContactsConfigured() {
        val rule = baseRule(keywords = emptyList(), contacts = emptyList())
        val notification = baseNotification()

        val result = RuleMatcher.matches(rule, notification, LocalTime.of(10, 0))

        assertFalse(result)
    }

    private fun baseRule(
        packageName: String = "com.whatsapp",
        keywords: List<String> = listOf("urgent"),
        contacts: List<String> = emptyList(),
        isEnabled: Boolean = true,
        activeHours: ActiveHours = ActiveHours(startHour = 0, endHour = 23)
    ): Rule {
        return Rule(
            id = "r1",
            title = "WhatsApp urgent",
            appName = "WhatsApp",
            packageName = packageName,
            keywords = keywords,
            contacts = contacts,
            isEnabled = isEnabled,
            activeHours = activeHours
        )
    }

    private fun baseNotification(
        packageName: String = "com.whatsapp",
        title: String = "Mom",
        body: String = "urgent: where are you?"
    ): NotificationPayload {
        return NotificationPayload(
            packageName = packageName,
            title = title,
            body = body
        )
    }
}
