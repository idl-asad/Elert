package com.example.elert.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserManager
import com.example.elert.data.model.InstalledApp

object InstalledAppsProvider {

    /**
     * Returns user-selectable installed apps for rule setup.
     *
     * Uses multiple sources because [PackageManager.queryIntentActivities] with a narrow
     * &lt;queries&gt; declaration misses many apps on Android 11+ (e.g. WhatsApp).
     */
    fun getLaunchableApps(context: Context): List<InstalledApp> {
        val packageManager = context.packageManager
        val ourPackage = context.packageName

        val apps = LinkedHashMap<String, InstalledApp>()

        loadFromLauncherApps(context, packageManager, ourPackage, apps)
        loadFromInstalledApplications(packageManager, ourPackage, apps)
        loadFromLauncherIntentQuery(packageManager, ourPackage, apps)

        return apps.values.sortedBy { it.label.lowercase() }
    }

    private fun loadFromLauncherApps(
        context: Context,
        packageManager: PackageManager,
        ourPackage: String,
        apps: MutableMap<String, InstalledApp>
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return

        val launcherApps = context.getSystemService(LauncherApps::class.java) ?: return
        val userManager = context.getSystemService(UserManager::class.java) ?: return

        for (user in userManager.userProfiles) {
            val activities = launcherApps.getActivityList(null, user)
            for (activity in activities) {
                val packageName = activity.applicationInfo.packageName
                if (packageName == ourPackage) continue
                apps[packageName] = InstalledApp(
                    label = activity.label.toString().trim(),
                    packageName = packageName
                )
            }
        }
    }

    private fun loadFromInstalledApplications(
        packageManager: PackageManager,
        ourPackage: String,
        apps: MutableMap<String, InstalledApp>
    ) {
        val installed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(
                PackageManager.ApplicationInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(0)
        }

        for (appInfo in installed) {
            if (!appInfo.enabled || appInfo.packageName == ourPackage) continue
            if (!isSelectableApp(packageManager, appInfo)) continue

            val label = packageManager.getApplicationLabel(appInfo).toString().trim()
            if (label.isEmpty()) continue

            apps[appInfo.packageName] = InstalledApp(
                label = label,
                packageName = appInfo.packageName
            )
        }
    }

    private fun loadFromLauncherIntentQuery(
        packageManager: PackageManager,
        ourPackage: String,
        apps: MutableMap<String, InstalledApp>
    ) {
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        @Suppress("DEPRECATION")
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.MATCH_ALL
        } else {
            0
        }

        val resolveInfos = packageManager.queryIntentActivities(launcherIntent, flag)
        for (resolveInfo in resolveInfos) {
            val appInfo = resolveInfo.activityInfo.applicationInfo
            val packageName = appInfo.packageName
            if (packageName == ourPackage) continue

            val label = resolveInfo.loadLabel(packageManager).toString().trim()
            if (label.isEmpty()) continue

            apps[packageName] = InstalledApp(label = label, packageName = packageName)
        }
    }

  /**
     * Include apps with a launcher icon, or user-installed apps (covers apps that post
     * notifications but may not appear in a narrow intent query).
     */
    private fun isSelectableApp(
        packageManager: PackageManager,
        appInfo: ApplicationInfo
    ): Boolean {
        val hasLauncher = packageManager.getLaunchIntentForPackage(appInfo.packageName) != null
        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        return hasLauncher || !isSystemApp
    }
}
