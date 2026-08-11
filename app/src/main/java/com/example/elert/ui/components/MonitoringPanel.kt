package com.example.elert.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MonitoringPanel(
    isMonitoring: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val statusText = if (isMonitoring) {
        "Real-time Monitoring Active"
    } else {
        "Ready to Monitor"
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 12.dp,
        borderColor = primaryContainer.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dotColor = if (isMonitoring) primaryContainer else Color.White.copy(alpha = 0.3f)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(if (isMonitoring) pulseScale else 1f)
                    .drawBehind {
                        if (isMonitoring) {
                            drawCircle(
                                color = primaryContainer.copy(alpha = 0.5f * pulseAlpha),
                                radius = size.maxDimension * 1.5f,
                                center = center
                            )
                        }
                        drawCircle(
                            color = dotColor.copy(alpha = if (isMonitoring) pulseAlpha else 1f),
                            radius = size.minDimension / 2f,
                            center = center
                        )
                    }
                    .clip(CircleShape)
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}
