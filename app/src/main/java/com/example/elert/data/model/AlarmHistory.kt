package com.example.elert.data.model

data class AlarmHistory(
    val id: String,
    val ruleId: String,
    val ruleTitle: String,
    val appName: String,
    val keywords: List<String> = emptyList(),
    val notificationTitle: String = "",
    val notificationBody: String = "",
    val triggeredAt: Long
)
