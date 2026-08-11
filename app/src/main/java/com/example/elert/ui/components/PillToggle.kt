package com.example.elert.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PillToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor = if (checked) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.White.copy(alpha = 0.1f)
    }
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "pillToggleThumb"
    )

    Box(
        modifier = modifier
            .width(36.dp)
            .height(20.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .offset(x = thumbOffset)
                .align(Alignment.CenterStart)
                .background(Color.White, CircleShape)
        )
    }
}
