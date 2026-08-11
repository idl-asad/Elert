package com.example.elert.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.elert.ui.theme.ElertSurfaceContainerLow

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    accentBorderColor: Color? = null,
    accentBorderWidth: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val surfaceColor = ElertSurfaceContainerLow

    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                val radius = cornerRadius.toPx()
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.4f),
                    topLeft = Offset(6f, 6f),
                    size = size,
                    cornerRadius = CornerRadius(radius, radius)
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.03f),
                    topLeft = Offset(-4f, -4f),
                    size = size,
                    cornerRadius = CornerRadius(radius, radius)
                )
            }
            .background(surfaceColor, shape)
            .then(
                if (accentBorderColor != null && accentBorderWidth > 0.dp) {
                    Modifier.drawBehind {
                        drawRect(
                            color = accentBorderColor,
                            size = Size(accentBorderWidth.toPx(), size.height)
                        )
                    }
                } else {
                    Modifier
                }
            ),
        content = content
    )
}
