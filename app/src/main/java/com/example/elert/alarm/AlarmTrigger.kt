package com.example.elert.alarm

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.elert.ElertApplication
import com.example.elert.data.model.NotificationPayload
import com.example.elert.data.model.Rule

object AlarmTrigger {

    fun onMatch(context: Context, rule: Rule, notification: NotificationPayload) {
        if (DeviceUsageHelper.isActivelyInUse(context)) {
            Log.i(
                TAG,
                "Rule matched while phone in use — skipping alarm | rule=${rule.title} | " +
                    "notification=${notification.title} / ${notification.body}"
            )
            return
        }

        Log.i(
            TAG,
            "ALARM TRIGGERED | rule=${rule.title} | keywords=${rule.keywords} | contacts=${rule.contacts} | " +
                "app=${rule.packageName} | notification=${notification.title} / ${notification.body}"
        )

        ElertApplication.from(context.applicationContext as Application)
            .alarmHistoryRepository
            .recordTrigger(
                ruleId = rule.id,
                ruleTitle = rule.title,
                appName = rule.appName,
                keywords = rule.keywords,
                notificationTitle = notification.title,
                notificationBody = notification.body
            )

        AlarmForegroundService.start(context.applicationContext, rule, notification)
    }

    private const val TAG = "ElertAlarm"
}
