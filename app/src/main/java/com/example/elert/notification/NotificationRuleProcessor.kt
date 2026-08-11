package com.example.elert.notification

import android.content.Context
import android.util.Log
import com.example.elert.alarm.AlarmTrigger
import com.example.elert.data.local.ElertDatabase
import com.example.elert.data.local.toRule
import com.example.elert.data.model.NotificationPayload
import com.example.elert.domain.RuleMatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object NotificationRuleProcessor {

    fun process(context: Context, notification: NotificationPayload) {
        val rules = runBlocking(Dispatchers.IO) {
            runCatching {
                ElertDatabase.getInstance(context)
                    .ruleDao()
                    .getAll()
                    .map { it.toRule() }
            }.getOrElse { error ->
                Log.e(TAG, "Failed to load rules from database", error)
                emptyList()
            }
        }

        var matchCount = 0

        for (rule in rules) {
            if (RuleMatcher.matches(rule, notification)) {
                matchCount++
                Log.d(TAG, "Rule matched: ${rule.id} (${rule.title})")
                AlarmTrigger.onMatch(context, rule, notification)
            }
        }

        if (matchCount == 0) {
            Log.d(TAG, "No rule match for ${notification.packageName}")
        }
    }

    private const val TAG = "ElertRuleProcessor"
}
