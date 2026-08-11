package com.example.elert.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rules")
data class RuleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val appName: String,
    val packageName: String,
    val keywordsJson: String,
    val contactsJson: String,
    val isEnabled: Boolean,
    val startHour: Int,
    val endHour: Int
)
