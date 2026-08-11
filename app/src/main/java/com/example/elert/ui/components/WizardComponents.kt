package com.example.elert.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elert.ui.theme.ElertGlassBorder
import com.example.elert.ui.theme.ElertGlassBorderSubtle
import com.example.elert.ui.theme.ElertSurfaceContainerHigh
import com.example.elert.ui.theme.ElertSurfaceContainerLow
import com.example.elert.ui.theme.ElertSurfaceContainerLowest
import com.example.elert.ui.theme.ElertOnPrimary
import com.example.elert.ui.theme.ElertTopBarBackground

private val WizardNavHeight = 52.dp
private val WizardNavShape = RoundedCornerShape(percent = 50)

@Composable
fun WizardTopBar(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    showBack: Boolean = true,
    modifier: Modifier = Modifier,
    onNotificationsClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ElertTopBarBackground)
            .border(width = 1.dp, color = ElertGlassBorder)
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(48.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (showBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center,
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
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun WizardProgressTracker(
    currentStep: Int,
    totalSteps: Int,
    categoryLabel: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STEP $currentStep OF $totalSteps",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp
            )
            Text(
                text = categoryLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50)),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(totalSteps) { index ->
                val stepNumber = index + 1
                val primaryColor = MaterialTheme.colorScheme.primary
                val color = when {
                    stepNumber < currentStep -> primaryColor.copy(alpha = 0.4f)
                    stepNumber == currentStep -> primaryColor
                    else -> Color.White.copy(alpha = 0.1f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color)
                        .then(
                            if (stepNumber == currentStep) {
                                Modifier.drawBehind {
                                    drawCircle(
                                        color = primaryColor.copy(alpha = 0.5f),
                                        radius = size.maxDimension,
                                        center = Offset(size.width / 2f, size.height / 2f)
                                    )
                                }
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }
    }
}

@Composable
fun WizardStepHeader(
    headline: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun WizardGlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Default.Key
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ElertSurfaceContainerLowest.copy(alpha = 0.5f))
                .border(1.dp, ElertGlassBorderSubtle, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { inner ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                        }
                        inner()
                    }
                }
            )
        }
    }
}

@Composable
fun WizardInfoCard(
    title: String,
    titleIcon: ImageVector,
    modifier: Modifier = Modifier,
    showGlow: Boolean = false,
    content: @Composable () -> Unit
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 12.dp
    ) {
        val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        Box(modifier = Modifier.fillMaxWidth()) {
            if (showGlow) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(96.dp)
                        .drawBehind {
                            drawCircle(
                                color = glowColor,
                                radius = size.maxDimension / 2f,
                                center = center
                            )
                        }
                )
            }
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = titleIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )
                }
                content()
            }
        }
    }
}

@Composable
fun WizardLogicBadge(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ElertSurfaceContainerHigh.copy(alpha = 0.5f))
            .border(1.dp, ElertGlassBorderSubtle, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun WizardTipItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun WizardNavigationButtons(
    onContinue: () -> Unit,
    continueLabel: String,
    continueEnabled: Boolean,
    showTopDivider: Boolean = true,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = ElertOnPrimary

    val containerModifier = if (showTopDivider) {
        modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .border(1.dp, ElertGlassBorderSubtle, RoundedCornerShape(0.dp))
            .padding(top = 20.dp)
    } else {
        modifier.fillMaxWidth().padding(top = 16.dp)
    }

    Box(
        modifier = containerModifier
            .fillMaxWidth()
            .height(WizardNavHeight)
            .shadow(
                elevation = if (continueEnabled) 8.dp else 0.dp,
                shape = WizardNavShape,
                ambientColor = primary.copy(alpha = 0.25f),
                spotColor = primary.copy(alpha = 0.35f)
            )
            .clip(WizardNavShape)
            .background(if (continueEnabled) primary else primary.copy(alpha = 0.35f))
            .clickable(enabled = continueEnabled, onClick = onContinue),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = continueLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = onPrimary.copy(alpha = if (continueEnabled) 1f else 0.55f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = onPrimary.copy(alpha = if (continueEnabled) 0.9f else 0.45f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun WizardRuleBadge(ruleTitle: String, modifier: Modifier = Modifier) {
    if (ruleTitle.isBlank()) return
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(ElertSurfaceContainerLow)
            .border(1.dp, ElertGlassBorderSubtle, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Rule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "Rule: \"$ruleTitle\"",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun WizardMainPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 12.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            content()
        }
    }
}
