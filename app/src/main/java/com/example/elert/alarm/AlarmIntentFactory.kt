package com.example.elert.alarm

import android.content.Context
import android.content.Intent
import com.example.elert.data.model.NotificationPayload
import com.example.elert.data.model.Rule

object AlarmIntentFactory {

    fun alarmActivityIntent(
        context: Context,
        rule: Rule,
        notification: NotificationPayload
    ): Intent {
        return Intent(context, AlarmActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            putExtra(AlarmActivity.EXTRA_RULE_TITLE, rule.title)
            putExtra(AlarmActivity.EXTRA_APP_NAME, rule.appName)
            putExtra(AlarmActivity.EXTRA_KEYWORDS, rule.keywords.joinToString("\u001F"))
            putExtra(AlarmActivity.EXTRA_CONTACTS, rule.contacts.joinToString("\u001F"))
            putExtra(AlarmActivity.EXTRA_NOTIFICATION_TITLE, notification.title)
            putExtra(AlarmActivity.EXTRA_NOTIFICATION_BODY, notification.body)
        }
    }

    fun alarmServiceIntent(
        context: Context,
        rule: Rule,
        notification: NotificationPayload
    ): Intent {
        return Intent(context, AlarmForegroundService::class.java).apply {
            putExtra(AlarmActivity.EXTRA_RULE_TITLE, rule.title)
            putExtra(AlarmActivity.EXTRA_APP_NAME, rule.appName)
            putExtra(AlarmActivity.EXTRA_KEYWORDS, rule.keywords.joinToString("\u001F"))
            putExtra(AlarmActivity.EXTRA_CONTACTS, rule.contacts.joinToString("\u001F"))
            putExtra(AlarmActivity.EXTRA_NOTIFICATION_TITLE, notification.title)
            putExtra(AlarmActivity.EXTRA_NOTIFICATION_BODY, notification.body)
        }
    }
}
