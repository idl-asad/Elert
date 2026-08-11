package com.example.elert.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.elert.data.model.Rule
import com.example.elert.ui.theme.ElertGlassBorderSubtle

private fun ruleSubtitle(rule: Rule): String {
    val keyword = rule.keywords.firstOrNull()?.trim().orEmpty()
    if (keyword.isNotEmpty()) return "Keyword: \"$keyword\""
    val contact = rule.contacts.firstOrNull()?.trim().orEmpty()
    if (contact.isNotEmpty()) return "Contact: $contact"
    return rule.appName
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RuleCard(
    rule: Rule,
    onToggleEnabled: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appIcon = remember(rule.packageName) {
        if (rule.packageName.isBlank()) {
            null
        } else {
            runCatching {
                context.packageManager
                    .getApplicationIcon(rule.packageName)
                    .toBitmap(width = 96, height = 96)
                    .asImageBitmap()
            }.getOrNull()
        }
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onDelete
            ),
        cornerRadius = 8.dp,
        borderColor = ElertGlassBorderSubtle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (appIcon != null) {
                        Image(
                            bitmap = appIcon,
                            contentDescription = rule.appName,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = rule.appName,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = rule.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = ruleSubtitle(rule),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
            PillToggle(
                checked = rule.isEnabled,
                onCheckedChange = { onToggleEnabled() }
            )
        }
    }
}
