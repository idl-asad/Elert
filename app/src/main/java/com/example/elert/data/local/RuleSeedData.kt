package com.example.elert.data.local

import com.example.elert.data.model.ActiveHours
import com.example.elert.data.model.Rule

object RuleSeedData {

    fun sampleRules(): List<RuleEntity> = listOf(
        Rule(
            id = "1",
            title = "PagerDuty Critical",
            appName = "PagerDuty",
            packageName = "com.pagerduty.android",
            keywords = listOf("critical"),
            contacts = emptyList(),
            isEnabled = true,
            activeHours = ActiveHours(startHour = 0, endHour = 23)
        ),
        Rule(
            id = "2",
            title = "WhatsApp Urgent",
            appName = "WhatsApp",
            packageName = "com.whatsapp",
            keywords = listOf("urgent"),
            contacts = listOf("Mom"),
            isEnabled = false,
            activeHours = ActiveHours(startHour = 9, endHour = 17)
        )
    ).map { it.toEntity() }
}
