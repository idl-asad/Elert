package com.example.elert

import android.app.Application
import android.os.Build
import android.webkit.WebView
import com.example.elert.alarm.AlarmNotificationHelper
import com.example.elert.data.local.ElertDatabase
import com.example.elert.data.local.RuleSeedData
import com.example.elert.data.repository.AlarmHistoryRepository
import com.example.elert.data.repository.RoomAlarmHistoryRepository
import com.example.elert.data.repository.RoomRuleRepository
import com.example.elert.data.repository.RuleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ElertApplication : Application() {

    lateinit var ruleRepository: RuleRepository
        private set

    lateinit var alarmHistoryRepository: AlarmHistoryRepository
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        AlarmNotificationHelper.createChannel(this)
        val database = ElertDatabase.getInstance(this)
        ruleRepository = RoomRuleRepository(database.ruleDao(), applicationScope)
        alarmHistoryRepository = RoomAlarmHistoryRepository(database.alarmHistoryDao(), applicationScope)
        applicationScope.launch(Dispatchers.IO) {
            if (database.ruleDao().count() == 0) {
                database.ruleDao().insertAll(RuleSeedData.sampleRules())
            }
        }
    }

    companion object {
        fun from(application: Application): ElertApplication =
            application as ElertApplication
    }
}
