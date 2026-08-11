package com.example.elert.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elert.ui.theme.ElertBackground
import com.example.elert.ui.theme.ElertGlassBorderSubtle
import com.example.elert.ui.theme.ElertOutlineVariant
import com.example.elert.ui.theme.ElertSurfaceContainerLow
import kotlin.math.roundToInt

object ActiveHoursTimeParser {
    fun parseHour(text: String): Int? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return null
        if (trimmed.contains(":")) {
            return trimmed.substringBefore(":").toIntOrNull()?.coerceIn(0, 23)
        }
        return trimmed.toIntOrNull()?.coerceIn(0, 23)
    }

    fun formatHour(hour: Int): String = "%02d:00".format(hour.coerceIn(0, 23))

    fun isValidRange(startText: String, endText: String): Boolean =
        parseHour(startText) != null && parseHour(endText) != null
}

@Composable
fun ActiveHoursWizardStep(
    startTimeText: String,
    endTimeText: String,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onContinue: () -> Unit,
    continueLabel: String,
    canContinue: Boolean,
    stepNumber: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val startHour = ActiveHoursTimeParser.parseHour(startTimeText) ?: 0
    val endHour = ActiveHoursTimeParser.parseHour(endTimeText) ?: 23

    Column(modifier = modifier.fillMaxWidth()) {
        ActiveHoursStepBadge(
            stepNumber = stepNumber,
            totalSteps = totalSteps,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 12.dp) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "When should this rule run?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Define the 24-hour window during which Elert monitors this specific rule. " +
                        "Alerts triggered outside this window will be logged but suppressed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                TimeRangeTimeline(
                    startHour = startHour,
                    endHour = endHour,
                    onStartHourChange = { onStartTimeChange(ActiveHoursTimeParser.formatHour(it)) },
                    onEndHourChange = { onEndTimeChange(ActiveHoursTimeParser.formatHour(it)) },
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActiveHoursTimeField(
                        label = "Start Time",
                        value = startTimeText,
                        onValueChange = onStartTimeChange,
                        trailingIcon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f)
                    )
                    ActiveHoursTimeField(
                        label = "End Time",
                        value = endTimeText,
                        onValueChange = onEndTimeChange,
                        trailingIcon = Icons.Default.History,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActiveHoursPresetButton(
                icon = Icons.Default.WbSunny,
                label = "Working Hours",
                onClick = {
                    onStartTimeChange("08:00")
                    onEndTimeChange("18:00")
                },
                modifier = Modifier.weight(1f)
            )
            ActiveHoursPresetButton(
                icon = Icons.Default.NightsStay,
                label = "Overnight",
                onClick = {
                    onStartTimeChange("22:00")
                    onEndTimeChange("06:00")
                },
                modifier = Modifier.weight(1f)
            )
            ActiveHoursPresetButton(
                icon = Icons.Default.AllInclusive,
                label = "24/7 Monitoring",
                onClick = {
                    onStartTimeChange("00:00")
                    onEndTimeChange("23:00")
                },
                modifier = Modifier.weight(1f)
            )
        }

        WizardNavigationButtons(
            onContinue = onContinue,
            continueLabel = continueLabel,
            continueEnabled = canContinue,
            showTopDivider = false
        )
    }
}

@Composable
private fun ActiveHoursStepBadge(
    stepNumber: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "ACTIVE HOURS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = "Step $stepNumber of $totalSteps",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun TimeRangeTimeline(
    startHour: Int,
    endHour: Int,
    onStartHourChange: (Int) -> Unit,
    onEndHourChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val primary = MaterialTheme.colorScheme.primary
    val trackColor = Color.White.copy(alpha = 0.05f)
    val density = LocalDensity.current
    val thumbSize = 20.dp
    val thumbRadiusPx = with(density) { thumbSize.toPx() / 2f }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "TIMELINE PREVIEW",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${ActiveHoursTimeParser.formatHour(startHour)} — ${ActiveHoursTimeParser.formatHour(endHour)}",
                style = MaterialTheme.typography.labelSmall,
                color = primary
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 24.dp)
        ) {
            val trackWidthPx = constraints.maxWidth.toFloat()

            fun hourToCenterX(hour: Int): Float =
                (hour / 24f).coerceIn(0f, 1f) * trackWidthPx

            fun centerXToHour(centerX: Float): Int =
                ((centerX / trackWidthPx) * 24f).roundToInt().coerceIn(0, 23)

            var startCenterX by remember(trackWidthPx) {
                mutableFloatStateOf(hourToCenterX(startHour))
            }
            var endCenterX by remember(trackWidthPx) {
                mutableFloatStateOf(hourToCenterX(endHour))
            }

            LaunchedEffect(startHour, trackWidthPx) {
                startCenterX = hourToCenterX(startHour)
            }
            LaunchedEffect(endHour, trackWidthPx) {
                endCenterX = hourToCenterX(endHour)
            }

            val startFraction = (startCenterX / trackWidthPx).coerceIn(0f, 1f)
            val endFraction = (endCenterX / trackWidthPx).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(thumbSize)
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(trackColor)
                        .drawBehind {
                            fun drawActiveSegment(leftFraction: Float, rightFraction: Float) {
                                val left = size.width * leftFraction
                                val width = size.width * (rightFraction - leftFraction)
                                if (width <= 0f) return
                                drawRoundRect(
                                    color = primary.copy(alpha = 0.25f),
                                    topLeft = Offset(left, -4f),
                                    size = Size(width, size.height + 8f),
                                    cornerRadius = CornerRadius(6f, 6f)
                                )
                                drawRoundRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(primaryContainer, primary)
                                    ),
                                    topLeft = Offset(left, 0f),
                                    size = Size(width, size.height),
                                    cornerRadius = CornerRadius(4f, 4f)
                                )
                            }

                            if (startHour <= endHour) {
                                drawActiveSegment(startFraction, endFraction)
                            } else {
                                drawActiveSegment(startFraction, 1f)
                                drawActiveSegment(0f, endFraction)
                            }
                        }
                )

                TimelineThumb(
                    centerX = startCenterX,
                    thumbRadiusPx = thumbRadiusPx,
                    trackWidthPx = trackWidthPx,
                    onDrag = { delta ->
                        startCenterX = (startCenterX + delta)
                            .coerceIn(thumbRadiusPx, trackWidthPx - thumbRadiusPx)
                        onStartHourChange(centerXToHour(startCenterX))
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                TimelineThumb(
                    centerX = endCenterX,
                    thumbRadiusPx = thumbRadiusPx,
                    trackWidthPx = trackWidthPx,
                    onDrag = { delta ->
                        endCenterX = (endCenterX + delta)
                            .coerceIn(thumbRadiusPx, trackWidthPx - thumbRadiusPx)
                        onEndHourChange(centerXToHour(endCenterX))
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("00:00", "06:00", "12:00", "18:00", "23:59").forEach { marker ->
                Text(
                    text = marker,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun TimelineThumb(
    centerX: Float,
    thumbRadiusPx: Float,
    trackWidthPx: Float,
    onDrag: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val offsetX = (centerX - thumbRadiusPx).coerceIn(0f, trackWidthPx - thumbRadiusPx * 2)

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .size(20.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }
            }
            .border(4.dp, ElertBackground, CircleShape)
            .background(primary, CircleShape)
    )
}

@Composable
private fun ActiveHoursTimeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ElertSurfaceContainerLow)
                .border(1.dp, ElertOutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 28.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { inner -> inner() }
            )
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun ActiveHoursPresetButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.clickable(onClick = onClick),
        cornerRadius = 12.dp,
        borderColor = ElertGlassBorderSubtle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}
