package com.example.elert.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elert.data.model.Rule
import com.example.elert.ui.components.BottomNavTab
import com.example.elert.ui.components.DashboardStatCard
import com.example.elert.ui.components.ElertBottomBar
import com.example.elert.ui.components.EmptyState
import com.example.elert.ui.components.MonitoringPanel
import com.example.elert.ui.components.PermissionStatusCard
import com.example.elert.ui.components.RuleCard
import com.example.elert.ui.theme.ElertBackground
import com.example.elert.ui.theme.ElertGlassBorder
import com.example.elert.ui.theme.ElertTopBarBackground
import com.example.elert.viewmodel.RuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddRule: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: RuleViewModel = viewModel()
) {
    val rules by viewModel.rules.collectAsState()
    var rulePendingDelete by remember { mutableStateOf<Rule?>(null) }

    val activeCount = rules.count { it.isEnabled }
    val silencedCount = rules.count { !it.isEnabled }
    val isMonitoring = rules.isNotEmpty() && activeCount > 0

    var bottomBarHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val bottomBarHeight = with(density) { bottomBarHeightPx.toDp() }
    val fabGap = 12.dp
    val fabBottomPadding = if (bottomBarHeightPx > 0) {
        bottomBarHeight + fabGap
    } else {
        96.dp
    }
    val listBottomPadding = if (bottomBarHeightPx > 0) {
        bottomBarHeight + 72.dp
    } else {
        112.dp
    }

    rulePendingDelete?.let { rule ->
        AlertDialog(
            onDismissRequest = { rulePendingDelete = null },
            title = { Text("Delete rule?") },
            text = { Text("\"${rule.title}\" will be removed permanently.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRule(rule.id)
                        rulePendingDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { rulePendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = ElertBackground,
            topBar = { DashboardTopBar() },
            bottomBar = {
                ElertBottomBar(
                    modifier = Modifier.onSizeChanged { bottomBarHeightPx = it.height },
                    selectedTab = BottomNavTab.Home,
                    onTabSelected = { tab ->
                        when (tab) {
                            BottomNavTab.Settings -> onNavigateToSettings()
                            BottomNavTab.Rules -> onNavigateToRules()
                            BottomNavTab.History -> onNavigateToHistory()
                            BottomNavTab.Home -> Unit
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = listBottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    PermissionStatusCard()
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardStatCard(
                            label = "ACTIVE RULES",
                            value = activeCount.toString(),
                            valueColor = MaterialTheme.colorScheme.primaryContainer,
                            icon = Icons.Default.CheckCircle,
                            iconTint = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatCard(
                            label = "SILENCED",
                            value = silencedCount.toString(),
                            valueColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            icon = Icons.Default.NotificationsOff,
                            iconTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONFIGURED TRIGGERS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(onClick = onNavigateToRules) {
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (rules.isEmpty()) {
                    item {
                        EmptyState(message = "No rules yet. Tap + to create one.")
                    }
                } else {
                    itemsIndexed(
                        items = rules,
                        key = { _, rule -> rule.id }
                    ) { _, rule ->
                        RuleCard(
                            rule = rule,
                            onToggleEnabled = { viewModel.toggleRuleEnabled(rule.id) },
                            onDelete = { rulePendingDelete = rule }
                        )
                    }
                }

                item {
                    MonitoringPanel(isMonitoring = isMonitoring)
                }
            }
        }

        FloatingActionButton(
            onClick = onNavigateToAddRule,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = fabBottomPadding)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add rule"
            )
        }
    }
}

@Composable
private fun DashboardTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ElertTopBarBackground)
            .border(width = 1.dp, color = ElertGlassBorder)
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SignalCellularAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Elert",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = "Monitoring status",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
