package com.example.elert.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.elert.alarm.AlarmPermissionHelper
import com.example.elert.notification.LastNotificationHolder
import com.example.elert.notification.NotificationAccessHelper
import android.Manifest
import android.os.Build
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isAccessEnabled by remember { mutableStateOf(NotificationAccessHelper.isNotificationListenerEnabled(context)) }
    var canPostNotifications by remember { mutableStateOf(AlarmPermissionHelper.canPostNotifications(context)) }
    var canUseFullScreenIntent by remember { mutableStateOf(AlarmPermissionHelper.canUseFullScreenIntent(context)) }
    val lastNotification by LastNotificationHolder.lastNotification.collectAsState()

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Notification access",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (isAccessEnabled) {
                            "Enabled — Elert can read notifications"
                        } else {
                            "Not enabled — grant access to detect alerts"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isAccessEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = { NotificationAccessHelper.openNotificationListenerSettings(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isAccessEnabled) "Manage notification access" else "Enable notification access"
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Alarm while locked",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = when {
                            canPostNotifications && canUseFullScreenIntent ->
                                "Ready — alarm can show over lock screen"
                            !canPostNotifications ->
                                "Allow notifications so alarms work when the phone is locked"
                            else ->
                                "Enable full-screen alarms so the dismiss screen appears on lock screen"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (canPostNotifications && canUseFullScreenIntent) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    if (!canPostNotifications && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Button(
                            onClick = {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Allow notifications")
                        }
                    }
                    Button(
                        onClick = { AlarmPermissionHelper.openAppNotificationSettings(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Notification settings")
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Button(
                            onClick = { AlarmPermissionHelper.openFullScreenIntentSettings(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text("Full-screen alarm permission")
                        }
                    }
                    Text(
                        text = "Also disable battery restrictions for Elert in system settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Last notification (debug)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (lastNotification == null) {
                        Text(
                            text = "No notification captured yet. Send a test notification after enabling access.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        val notification = lastNotification!!
                        val time = DateFormat.getTimeInstance(DateFormat.SHORT)
                            .format(Date(notification.postedAt))
                        Text(
                            text = "App: ${notification.packageName}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = "Title: ${notification.title.ifEmpty { "—" }}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Body: ${notification.body.ifEmpty { "—" }}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Time: $time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
