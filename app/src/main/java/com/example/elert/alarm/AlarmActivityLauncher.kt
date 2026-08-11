package com.example.elert.alarm

import android.app.ActivityOptions
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity

object AlarmActivityLauncher {

    fun launchFromBackground(context: Context, intent: Intent) {
        val launchIntent = intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        )

        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val options = ActivityOptions.makeBasic().apply {
                    pendingIntentBackgroundActivityStartMode =
                        ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                }
                context.startActivity(launchIntent, options.toBundle())
            } else {
                context.startActivity(launchIntent)
            }
            Log.i(TAG, "AlarmActivity launched from background")
        }.onFailure { error ->
            Log.e(TAG, "Failed to launch AlarmActivity from background", error)
        }
    }

    fun canUseFullScreenIntent(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return true
        val manager = context.getSystemService(NotificationManager::class.java)
        return manager.canUseFullScreenIntent()
    }

    fun configureLockScreenDisplay(activity: ComponentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            activity.setShowWhenLocked(true)
            activity.setTurnScreenOn(true)
            val keyguardManager = activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(activity, null)
        }
        @Suppress("DEPRECATION")
        activity.window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }

    private const val TAG = "ElertAlarmLaunch"
}
