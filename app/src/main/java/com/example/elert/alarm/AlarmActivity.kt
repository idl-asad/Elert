package com.example.elert.alarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.elert.ui.screens.AlarmScreen
import com.example.elert.ui.theme.ElertTheme

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AlarmActivityLauncher.configureLockScreenDisplay(this)
        super.onCreate(savedInstanceState)
        AlarmSession.start(this)

        setContent {
            ElertTheme {
                AlarmScreen(
                    ruleTitle = intent.getStringExtra(EXTRA_RULE_TITLE).orEmpty(),
                    appName = intent.getStringExtra(EXTRA_APP_NAME).orEmpty(),
                    keywords = intent.decodeStringList(EXTRA_KEYWORDS),
                    contacts = intent.decodeStringList(EXTRA_CONTACTS),
                    notificationTitle = intent.getStringExtra(EXTRA_NOTIFICATION_TITLE).orEmpty(),
                    notificationBody = intent.getStringExtra(EXTRA_NOTIFICATION_BODY).orEmpty(),
                    onDismiss = ::dismissAlarm
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        AlarmActivityLauncher.configureLockScreenDisplay(this)
        AlarmSession.start(this)
    }

    override fun onDestroy() {
        AlarmSession.stop()
        super.onDestroy()
    }

    private fun dismissAlarm() {
        AlarmDismissHelper.dismiss(applicationContext)
        finish()
    }

    companion object {
        const val EXTRA_RULE_TITLE = "extra_rule_title"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_KEYWORDS = "extra_keywords"
        const val EXTRA_CONTACTS = "extra_contacts"
        const val EXTRA_NOTIFICATION_TITLE = "extra_notification_title"
        const val EXTRA_NOTIFICATION_BODY = "extra_notification_body"

        /** @deprecated use [EXTRA_KEYWORDS] */
        const val EXTRA_KEYWORD = "extra_keyword"
    }
}

private fun Intent.decodeStringList(extra: String): List<String> =
    getStringExtra(extra)
        ?.split(LIST_DELIMITER)
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

private const val LIST_DELIMITER = '\u001F'
