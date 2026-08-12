package com.example.elert.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_history")
data class AlarmHistoryEntity(
    @PrimaryKey val id: String,
    val ruleId: String,
    val ruleTitle: String,
    val appName: String,
    val keywordsJson: String,
    val notificationTitle: String,
    val notificationBody: String,
    val triggeredAt: Long
)
