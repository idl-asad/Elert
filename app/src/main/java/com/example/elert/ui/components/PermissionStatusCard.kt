package com.example.elert.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.elert.alarm.AlarmPermissionHelper
import com.example.elert.notification.NotificationAccessHelper

/**
 * Shows a warning card on the home screen when any permission Elert needs is
 * missing (notification listener access, POST_NOTIFICATIONS, or full-screen
 * intent). Renders nothing when all permissions are granted.
 */
@Composable
fun PermissionStatusCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isAccessEnabled by remember { mutableStateOf(NotificationAccessHelper.isNotificationListenerEnabled(context)) }
    var canPostNotifications by remember { mutableStateOf(AlarmPermissionHelper.canPostNotifications(context)) }
    var canUseFullScreenIntent by remember { mutableStateOf(AlarmPermissionHelper.canUseFullScreenIntent(context)) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        canPostNotifications = AlarmPermissionHelper.canPostNotifications(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessEnabled = NotificationAccessHelper.isNotificationListenerEnabled(context)
                canPostNotifications = AlarmPermissionHelper.canPostNotifications(context)
                canUseFullScreenIntent = AlarmPermissionHelper.canUseFullScreenIntent(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val allGranted = isAccessEnabled && canPostNotifications && canUseFullScreenIntent
    if (allGranted) return

    val errorColor = MaterialTheme.colorScheme.error

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 12.dp,
        borderColor = errorColor.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = errorColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Permissions required",
                    style = MaterialTheme.typography.titleSmall,
                    color = errorColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Elert needs these permissions to detect alerts and sound alarms. " +
                    "Without them the app can't protect you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isAccessEnabled) {
                PermissionRow(
                    label = "Notification access",
                    description = "Required to read incoming notifications and match rules."
                ) {
                    NotificationAccessHelper.openNotificationListenerSettings(context)
                }
            }

            if (!canPostNotifications && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionRow(
                    label = "Notifications",
                    description = "Required to show alarms and alerts."
                ) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            if (!canUseFullScreenIntent && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                PermissionRow(
                    label = "Full-screen alarms",
                    description = "Required so alarms appear even when the phone is locked."
                ) {
                    AlarmPermissionHelper.openFullScreenIntentSettings(context)
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(
    label: String,
    description: String,
    onGrant: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Button(onClick = onGrant) {
            Text("Grant")
        }
    }
}
