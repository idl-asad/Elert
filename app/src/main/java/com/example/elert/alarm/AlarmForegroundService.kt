package com.example.elert.alarm

import android.app.KeyguardManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.elert.data.model.NotificationPayload
import com.example.elert.data.model.Rule

class AlarmForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == AlarmDismissHelper.ACTION_DISMISS_ALARM) {
            AlarmDismissHelper.dismiss(applicationContext)
            return START_NOT_STICKY
        }

        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val ruleTitle = intent.getStringExtra(AlarmActivity.EXTRA_RULE_TITLE).orEmpty()
        val appName = intent.getStringExtra(AlarmActivity.EXTRA_APP_NAME).orEmpty()
        val keywords = intent.decodeAlarmStringList(AlarmActivity.EXTRA_KEYWORDS)
        val contacts = intent.decodeAlarmStringList(AlarmActivity.EXTRA_CONTACTS)
        val notificationTitle = intent.getStringExtra(AlarmActivity.EXTRA_NOTIFICATION_TITLE).orEmpty()
        val notificationBody = intent.getStringExtra(AlarmActivity.EXTRA_NOTIFICATION_BODY).orEmpty()

        val rule = Rule(
            id = "alarm",
            title = ruleTitle,
            appName = appName,
            packageName = "",
            keywords = keywords,
            contacts = contacts
        )
        val payload = NotificationPayload(
            packageName = "",
            title = notificationTitle,
            body = notificationBody
        )

        AlarmSession.start(applicationContext)

        val activityIntent = AlarmIntentFactory.alarmActivityIntent(applicationContext, rule, payload)
        val displayTitle = "Elert: $ruleTitle"
        val displayBody = notificationBody.ifEmpty { "Rule matched" }
        val useFullScreenIntent = shouldUseFullScreenIntent()

        startForeground(
            AlarmNotificationHelper.FOREGROUND_NOTIFICATION_ID,
            AlarmNotificationHelper.buildForegroundNotification(
                context = applicationContext,
                title = displayTitle,
                body = displayBody,
                activityIntent = activityIntent
            )
        )

        AlarmNotificationHelper.postFullScreenAlarmNotification(
            context = applicationContext,
            title = displayTitle,
            body = displayBody,
            fullScreenIntent = activityIntent,
            useFullScreenIntent = useFullScreenIntent
        )

        AlarmActivityLauncher.launchFromBackground(applicationContext, activityIntent)

        Log.i(TAG, "Alarm foreground service running for rule: $ruleTitle")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        AlarmSession.stop()
        AlarmNotificationHelper.cancelFullScreenAlarmNotification(applicationContext)
        super.onDestroy()
    }

    private fun shouldUseFullScreenIntent(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardLocked
    }

    companion object {
        private const val TAG = "ElertAlarmService"

        fun start(context: Context, rule: Rule, notification: NotificationPayload) {
            if (DeviceUsageHelper.isActivelyInUse(context)) {
                Log.i(TAG, "Skipping alarm start — phone in active use")
                return
            }

            val intent = AlarmIntentFactory.alarmServiceIntent(context, rule, notification)
            val started = runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }.isSuccess

            if (!started) {
                Log.w(TAG, "Foreground service start failed; starting alarm session directly")
                AlarmSession.start(context.applicationContext)
                val activityIntent = AlarmIntentFactory.alarmActivityIntent(context, rule, notification)
                val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                AlarmNotificationHelper.postFullScreenAlarmNotification(
                    context = context.applicationContext,
                    title = "Elert: ${rule.title}",
                    body = notification.body,
                    fullScreenIntent = activityIntent,
                    useFullScreenIntent = keyguardManager.isKeyguardLocked
                )
                AlarmActivityLauncher.launchFromBackground(context, activityIntent)
            }
        }

        fun stop(context: Context) {
            AlarmNotificationHelper.cancelFullScreenAlarmNotification(context)
            context.stopService(Intent(context, AlarmForegroundService::class.java))
        }
    }
}

private fun Intent.decodeAlarmStringList(extra: String): List<String> =
    getStringExtra(extra)
        ?.split('\u001F')
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()
