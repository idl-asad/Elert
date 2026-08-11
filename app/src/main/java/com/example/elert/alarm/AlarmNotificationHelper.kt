package com.example.elert.alarm

import android.app.ActivityOptions
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.elert.R

object AlarmNotificationHelper {

    const val CHANNEL_ID = "elert_alarm"
    const val FOREGROUND_NOTIFICATION_ID = 9001
    const val FULL_SCREEN_ALARM_NOTIFICATION_ID = 9002

    private const val REQUEST_OPEN_ALARM = 9101
    private const val REQUEST_DISMISS_FOREGROUND = 9102
    private const val REQUEST_DISMISS_FULLSCREEN = 9103

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Elert alarms",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Critical alerts when a rule matches"
            setBypassDnd(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        manager.createNotificationChannel(channel)
    }

    fun buildForegroundNotification(
        context: Context,
        title: String,
        body: String,
        activityIntent: Intent
    ): Notification {
        createChannel(context)

        val openPending = createActivityPendingIntent(
            context = context,
            requestCode = REQUEST_OPEN_ALARM,
            activityIntent = activityIntent
        )
        val dismissPending = createDismissPendingIntent(
            context = context,
            requestCode = REQUEST_DISMISS_FOREGROUND
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setContentIntent(openPending)
            .addAction(0, "Open", openPending)
            .addAction(0, "Dismiss", dismissPending)
            .build()
    }

    fun postFullScreenAlarmNotification(
        context: Context,
        title: String,
        body: String,
        fullScreenIntent: Intent,
        useFullScreenIntent: Boolean
    ) {
        createChannel(context)

        val openPending = createActivityPendingIntent(
            context = context,
            requestCode = REQUEST_OPEN_ALARM + 1,
            activityIntent = fullScreenIntent
        )
        val dismissPending = createDismissPendingIntent(
            context = context,
            requestCode = REQUEST_DISMISS_FULLSCREEN
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(openPending)
            .addAction(0, "Open", openPending)
            .addAction(0, "Dismiss", dismissPending)

        if (useFullScreenIntent && AlarmActivityLauncher.canUseFullScreenIntent(context)) {
            builder.setFullScreenIntent(openPending, true)
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(FULL_SCREEN_ALARM_NOTIFICATION_ID, builder.build())
    }

    fun cancelFullScreenAlarmNotification(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.cancel(FULL_SCREEN_ALARM_NOTIFICATION_ID)
    }

    private fun createActivityPendingIntent(
        context: Context,
        requestCode: Int,
        activityIntent: Intent
    ): PendingIntent {
        val launchIntent = activityIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val options = ActivityOptions.makeBasic().apply {
                pendingIntentBackgroundActivityStartMode =
                    ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
            }
            PendingIntent.getActivity(
                context,
                requestCode,
                launchIntent,
                flags,
                options.toBundle()
            )
        } else {
            PendingIntent.getActivity(context, requestCode, launchIntent, flags)
        }
    }

    private fun createDismissPendingIntent(
        context: Context,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, AlarmDismissReceiver::class.java).apply {
            action = AlarmDismissHelper.ACTION_DISMISS_ALARM
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }
}
