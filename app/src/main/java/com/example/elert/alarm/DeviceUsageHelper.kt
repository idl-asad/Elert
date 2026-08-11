package com.example.elert.alarm

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager

/**
 * Returns true when the user is actively using the phone (screen on, unlocked).
 * In that case they can already see incoming notifications — no loud alarm needed.
 */
object DeviceUsageHelper {

    fun isActivelyInUse(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isInteractive) return false

        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return !keyguardManager.isKeyguardLocked
    }
}
