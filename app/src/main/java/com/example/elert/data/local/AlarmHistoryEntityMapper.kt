package com.example.elert.data.local

import com.example.elert.data.model.AlarmHistory

fun AlarmHistoryEntity.toModel(): AlarmHistory = AlarmHistory(
    id = id,
    ruleId = ruleId,
    ruleTitle = ruleTitle,
    appName = appName,
    keywords = StringListConverter.toStringList(keywordsJson),
    notificationTitle = notificationTitle,
    notificationBody = notificationBody,
    triggeredAt = triggeredAt
)

fun AlarmHistory.toEntity(): AlarmHistoryEntity = AlarmHistoryEntity(
    id = id,
    ruleId = ruleId,
    ruleTitle = ruleTitle,
    appName = appName,
    keywordsJson = StringListConverter.fromStringList(keywords),
    notificationTitle = notificationTitle,
    notificationBody = notificationBody,
    triggeredAt = triggeredAt
)
