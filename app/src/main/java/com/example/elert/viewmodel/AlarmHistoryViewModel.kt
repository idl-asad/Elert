package com.example.elert.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.elert.ElertApplication
import com.example.elert.data.model.AlarmHistory
import com.example.elert.data.repository.AlarmHistoryRepository
import kotlinx.coroutines.flow.StateFlow

class AlarmHistoryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: AlarmHistoryRepository =
        ElertApplication.from(application).alarmHistoryRepository

    val history: StateFlow<List<AlarmHistory>> = repository.getHistory()

    fun clearHistory() {
        repository.clearHistory()
    }
}
