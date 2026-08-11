package com.example.elert.alarm

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowPowerManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DeviceUsageHelperTest {

    @Test
    fun isActivelyInUse_returnsFalse_whenScreenOff() {
        val context = RuntimeEnvironment.getApplication()
        setInteractive(context, interactive = false)
        setKeyguardLocked(context, locked = false)

        assertFalse(DeviceUsageHelper.isActivelyInUse(context))
    }

    @Test
    fun isActivelyInUse_returnsFalse_whenScreenOnButLocked() {
        val context = RuntimeEnvironment.getApplication()
        setInteractive(context, interactive = true)
        setKeyguardLocked(context, locked = true)

        assertFalse(DeviceUsageHelper.isActivelyInUse(context))
    }

    @Test
    fun isActivelyInUse_returnsTrue_whenScreenOnAndUnlocked() {
        val context = RuntimeEnvironment.getApplication()
        setInteractive(context, interactive = true)
        setKeyguardLocked(context, locked = false)

        assertTrue(DeviceUsageHelper.isActivelyInUse(context))
    }

    private fun setInteractive(context: Context, interactive: Boolean) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val shadow = Shadows.shadowOf(powerManager) as ShadowPowerManager
        shadow.setIsInteractive(interactive)
    }

    private fun setKeyguardLocked(context: Context, locked: Boolean) {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        Shadows.shadowOf(keyguardManager).setKeyguardLocked(locked)
    }
}
