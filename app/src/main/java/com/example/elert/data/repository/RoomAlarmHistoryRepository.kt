package com.example.elert.data.repository

import com.example.elert.data.local.AlarmHistoryDao
import com.example.elert.data.local.toEntity
import com.example.elert.data.local.toModel
import com.example.elert.data.model.AlarmHistory
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomAlarmHistoryRepository(
    private val dao: AlarmHistoryDao,
    private val scope: CoroutineScope
) : AlarmHistoryRepository {

    private val _history = MutableStateFlow<List<AlarmHistory>>(emptyList())

    init {
        scope.launch {
            dao.observeAll().collect { entities ->
                _history.value = entities.map { it.toModel() }
            }
        }
    }

    override fun getHistory(): StateFlow<List<AlarmHistory>> = _history.asStateFlow()

    override fun recordTrigger(
        ruleId: String,
        ruleTitle: String,
        appName: String,
        keywords: List<String>,
        notificationTitle: String,
        notificationBody: String
    ) {
        scope.launch(Dispatchers.IO) {
            dao.insert(
                AlarmHistory(
                    id = UUID.randomUUID().toString(),
                    ruleId = ruleId,
                    ruleTitle = ruleTitle,
                    appName = appName,
                    keywords = keywords,
                    notificationTitle = notificationTitle,
                    notificationBody = notificationBody,
                    triggeredAt = System.currentTimeMillis()
                ).toEntity()
            )
        }
    }

    override fun clearHistory() {
        scope.launch(Dispatchers.IO) {
            dao.clear()
        }
    }
}
