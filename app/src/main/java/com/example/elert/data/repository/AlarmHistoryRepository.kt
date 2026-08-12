package com.example.elert.data.repository

import com.example.elert.data.model.AlarmHistory
import kotlinx.coroutines.flow.StateFlow

interface AlarmHistoryRepository {
    fun getHistory(): StateFlow<List<AlarmHistory>>
    fun recordTrigger(
        ruleId: String,
        ruleTitle: String,
        appName: String,
        keywords: List<String>,
        notificationTitle: String,
        notificationBody: String
    )
    fun clearHistory()
}
