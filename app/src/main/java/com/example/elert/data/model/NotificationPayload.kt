package com.example.elert.data.model

data class NotificationPayload(
    val packageName: String,
    val title: String,
    val body: String,
    val postedAt: Long = System.currentTimeMillis()
)
