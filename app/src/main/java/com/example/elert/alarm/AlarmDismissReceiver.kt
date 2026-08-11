package com.example.elert.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmDismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == AlarmDismissHelper.ACTION_DISMISS_ALARM) {
            AlarmDismissHelper.dismiss(context)
        }
    }
}
