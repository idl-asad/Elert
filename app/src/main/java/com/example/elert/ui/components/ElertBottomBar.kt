package com.example.elert.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.elert.ui.theme.ElertBottomBarBackground
import com.example.elert.ui.theme.ElertGlassBorder

enum class BottomNavTab {
    Home,
    Rules,
    History,
    Settings
}

@Composable
fun ElertBottomBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ElertBottomBarBackground,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .border(
                width = 1.dp,
                color = ElertGlassBorder,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Home",
            selected = selectedTab == BottomNavTab.Home,
            onClick = { onTabSelected(BottomNavTab.Home) }
        )
        BottomNavItem(
            icon = Icons.Default.Rule,
            label = "Rules",
            selected = selectedTab == BottomNavTab.Rules,
            onClick = { onTabSelected(BottomNavTab.Rules) }
        )
        BottomNavItem(
            icon = Icons.Default.History,
            label = "History",
            selected = selectedTab == BottomNavTab.History,
            onClick = { onTabSelected(BottomNavTab.History) }
        )
        BottomNavItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            selected = selectedTab == BottomNavTab.Settings,
            onClick = { onTabSelected(BottomNavTab.Settings) }
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val contentColor = if (selected) {
        primaryColor
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }
    val chipShape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .then(
                if (selected) {
                    Modifier
                        .background(primaryColor.copy(alpha = 0.1f), chipShape)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                } else {
                    Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
