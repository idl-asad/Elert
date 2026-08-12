package com.example.elert.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elert.data.model.AlarmHistory
import com.example.elert.ui.components.BottomNavTab
import com.example.elert.ui.components.ElertBottomBar
import com.example.elert.ui.components.EmptyState
import com.example.elert.ui.components.GlassCard
import com.example.elert.ui.components.ScreenTopBar
import com.example.elert.ui.theme.ElertBackground
import com.example.elert.ui.theme.ElertGlassBorderSubtle
import com.example.elert.viewmodel.AlarmHistoryViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun HistoryScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: AlarmHistoryViewModel = viewModel()
) {
    val history by viewModel.history.collectAsState()
    var showClearConfirm by remember { mutableStateOf(false) }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear history?") },
            text = { Text("All triggered alarm history will be removed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearConfirm = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = ElertBackground,
        topBar = {
            ScreenTopBar(
                title = "History",
                trailing = {
                    if (history.isNotEmpty()) {
                        TextButton(onClick = { showClearConfirm = true }) {
                            Text(
                                text = "Clear",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            ElertBottomBar(
                selectedTab = BottomNavTab.History,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.Home -> onNavigateToHome()
                        BottomNavTab.Rules -> onNavigateToRules()
                        BottomNavTab.History -> Unit
                        BottomNavTab.Settings -> onNavigateToSettings()
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (history.isEmpty()) {
                    item {
                        EmptyState(
                            message = "No alarms triggered yet.\nWhen a rule matches, it shows up here."
                        )
                    }
                } else {
                    itemsIndexed(
                        items = history,
                        key = { _, entry -> entry.id }
                    ) { _, entry ->
                        HistoryCard(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(entry: AlarmHistory) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 8.dp,
        borderColor = ElertGlassBorderSubtle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = entry.ruleTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = formatTriggerTime(entry.triggeredAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            val keyword = entry.keywords.firstOrNull()?.trim().orEmpty()
            val contextLine = when {
                keyword.isNotEmpty() -> "Keyword: \"$keyword\""
                entry.appName.isNotBlank() -> entry.appName
                else -> "Matched rule"
            }
            Text(
                text = contextLine,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            val notificationLine = listOf(entry.notificationTitle, entry.notificationBody)
                .filter { it.isNotBlank() }
                .joinToString(" — ")
            if (notificationLine.isNotEmpty()) {
                Text(
                    text = notificationLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun formatTriggerTime(timestamp: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        .format(Date(timestamp))
