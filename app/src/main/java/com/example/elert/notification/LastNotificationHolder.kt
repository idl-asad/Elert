package com.example.elert.notification

import com.example.elert.data.model.NotificationPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LastNotificationHolder {

    private val _lastNotification = MutableStateFlow<NotificationPayload?>(null)
    val lastNotification: StateFlow<NotificationPayload?> = _lastNotification.asStateFlow()

    fun update(payload: NotificationPayload) {
        _lastNotification.value = payload
    }
}
