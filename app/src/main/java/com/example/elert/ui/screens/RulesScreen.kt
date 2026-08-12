package com.example.elert.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elert.data.model.Rule
import com.example.elert.ui.components.BottomNavTab
import com.example.elert.ui.components.ElertBottomBar
import com.example.elert.ui.components.EmptyState
import com.example.elert.ui.components.RuleCard
import com.example.elert.ui.components.ScreenTopBar
import com.example.elert.ui.theme.ElertBackground
import com.example.elert.viewmodel.RuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    onNavigateToAddRule: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: RuleViewModel = viewModel()
) {
    val rules by viewModel.rules.collectAsState()
    var rulePendingDelete by remember { mutableStateOf<Rule?>(null) }

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

    Scaffold(
        containerColor = ElertBackground,
        topBar = { ScreenTopBar(title = "Rules") },
        bottomBar = {
            ElertBottomBar(
                selectedTab = BottomNavTab.Rules,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.Home -> onNavigateToHome()
                        BottomNavTab.Rules -> Unit
                        BottomNavTab.History -> onNavigateToHistory()
                        BottomNavTab.Settings -> onNavigateToSettings()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddRule,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add rule"
                )
            }
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
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
            }
        }
    }
}
