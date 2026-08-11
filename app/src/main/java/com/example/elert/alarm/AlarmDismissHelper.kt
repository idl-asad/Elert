package com.example.elert.alarm

import android.content.Context
import android.util.Log

object AlarmDismissHelper {

    const val ACTION_DISMISS_ALARM = "com.example.elert.action.DISMISS_ALARM"

    fun dismiss(context: Context) {
        Log.i(TAG, "Dismissing alarm")
        AlarmSession.stop()
        AlarmForegroundService.stop(context.applicationContext)
    }

    private const val TAG = "ElertAlarmDismiss"
}
