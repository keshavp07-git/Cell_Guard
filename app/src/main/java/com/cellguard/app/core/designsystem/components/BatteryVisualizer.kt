package com.cellguard.app.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cellguard.app.core.designsystem.theme.LocalCellGuardColors

@Composable
fun BatteryVisualizer(
    percentage: Int,
    isCharging: Boolean,
    statusText: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCellGuardColors.current

    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.coerceIn(0, 100).toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "BatteryLevelAnim"
    )

    // Base color based on battery level
    val targetColor = when {
        isCharging -> colors.accentGreen
        percentage <= 15 -> colors.accentRed
        percentage <= 25 -> colors.accentOrange
        else -> colors.accentGreen
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(400),
        label = "BatteryColorAnim"
    )

    // Charging pulse or shimmer
    val infiniteTransition = rememberInfiniteTransition(label = "Shimmer")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Battery Body & Terminal Cap
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Main Battery Shell
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(colors.surfaceElevated)
                    .border(
                        width = 3.5.dp,
                        color = colors.border.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(6.dp)
            ) {
                // Fill Bar
                val fillRatio = animatedPercentage / 100f
                val fillBrush = if (isCharging) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            animatedColor.copy(alpha = 0.8f * pulseAlpha),
                            animatedColor,
                            animatedColor.copy(alpha = 0.9f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            animatedColor.copy(alpha = 0.85f),
                            animatedColor
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = fillRatio.coerceAtLeast(0.04f))
                        .clip(RoundedCornerShape(14.dp))
                        .background(fillBrush)
                )

                // Overlay Content (Percentage & Bolt)
                Row(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isCharging) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Charging",
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 32.sp,
                            color = Color.White
                        )
                    )
                }
            }

            // Battery Anode Cap
            Spacer(modifier = Modifier.width(3.dp))
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                    .background(colors.border.copy(alpha = 0.8f))
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status Subtitle (e.g. "Charging • Fast Charge" or "Discharging • 5h remaining")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isCharging) animatedColor.copy(alpha = pulseAlpha) else animatedColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textSecondary
            )
        }
    }
}
