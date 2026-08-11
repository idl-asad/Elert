package com.example.elert.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elert.ui.screens.AddRuleScreen
import com.example.elert.ui.screens.AlarmScreen
import com.example.elert.ui.screens.HomeScreen
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
