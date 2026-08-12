package com.example.elert.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elert.ui.components.BottomNavTab
import com.example.elert.ui.screens.AddRuleScreen
import com.example.elert.ui.screens.AlarmScreen
import com.example.elert.ui.screens.HistoryScreen
import com.example.elert.ui.screens.HomeScreen
import com.example.elert.ui.screens.RulesScreen
import com.example.elert.ui.screens.SettingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToAddRule = { navController.navigate(Routes.ADD_RULE) },
                onNavigateToRules = { navController.navigateToTab(Routes.RULES) },
                onNavigateToHistory = { navController.navigateToTab(Routes.HISTORY) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.RULES) {
            RulesScreen(
                onNavigateToAddRule = { navController.navigate(Routes.ADD_RULE) },
                onNavigateToHome = { navController.navigateToTab(Routes.HOME) },
                onNavigateToHistory = { navController.navigateToTab(Routes.HISTORY) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateToHome = { navController.navigateToTab(Routes.HOME) },
                onNavigateToRules = { navController.navigateToTab(Routes.RULES) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.ADD_RULE) {
            AddRuleScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ALARM) {
            AlarmScreen(
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
