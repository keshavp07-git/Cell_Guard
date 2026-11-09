package com.cellguard.app.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cellguard.app.core.designsystem.theme.LocalCellGuardColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChartPoint(
    val timestamp: Long,
    val value: Float,
    val label: String? = null
)

@Composable
fun InteractiveChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = LocalCellGuardColors.current.accentGreen,
    unit: String = "",
    minY: Float? = null,
    maxY: Float? = null,
    showAverage: Boolean = true,
    heightDp: Int = 180
) {
    val colors = LocalCellGuardColors.current

    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceSecondary.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No history recorded for this period",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textTertiary
            )
        }
        return
    }

    var selectedIndex by remember(points) { mutableStateOf<Int?>(null) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(points) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(600))
    }

    val computedMinY = minY ?: (points.minOfOrNull { it.value } ?: 0f).coerceAtLeast(0f)
    val computedMaxY = (maxY ?: (points.maxOfOrNull { it.value } ?: 100f)).let {
        if (it == computedMinY) it + 1f else it
    }
    val avgY = points.map { it.value }.average().toFloat()

    val dateFormatter = remember { SimpleDateFormat("HH:mm, MMM d", Locale.getDefault()) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Scrubber / Value Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedIndex != null && selectedIndex!! in points.indices) {
                val pt = points[selectedIndex!!]
                Column {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", pt.value)} $unit".trim(),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = dateFormatter.format(Date(pt.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            } else {
                Column {
                    val latest = points.last()
                    Text(
                        text = "Current: ${String.format(Locale.US, "%.1f", latest.value)} $unit".trim(),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary
                    )
                    if (showAverage) {
                        Text(
                            text = "Avg: ${String.format(Locale.US, "%.1f", avgY)} $unit",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // High / Low Indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "L: ${String.format(Locale.US, "%.0f", computedMinY)}$unit",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
                Text(
                    text = "H: ${String.format(Locale.US, "%.0f", computedMaxY)}$unit",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
            }
        }

        // Canvas Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(points) {
                        detectTapGestures(
                            onTap = { offset ->
                                val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                                val idx = ((offset.x / stepX).toInt()).coerceIn(0, points.lastIndex)
                                selectedIndex = idx
                            }
                        )
                    }
                    .pointerInput(points) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                change.consume()
                                val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                                val idx = ((change.position.x / stepX).toInt()).coerceIn(0, points.lastIndex)
                                selectedIndex = idx
                            },
                            onDragEnd = { selectedIndex = null },
                            onDragCancel = { selectedIndex = null }
                        )
                    }
            ) {
                val width = size.width
                val height = size.height
                val pointCount = points.size

                val yRange = computedMaxY - computedMinY
                fun getY(value: Float): Float {
                    val norm = (value - computedMinY) / yRange
                    return height - (norm * height * progress.value)
                }

                fun getX(index: Int): Float {
                    return if (pointCount <= 1) width / 2f
                    else index * (width / (pointCount - 1))
                }

                // Grid lines (horizontal)
                val gridLevels = 3
                for (i in 0..gridLevels) {
                    val gy = height * (i.toFloat() / gridLevels)
                    drawLine(
                        color = colors.border.copy(alpha = 0.5f),
                        start = Offset(0f, gy),
                        end = Offset(width, gy),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                // Build Path
                if (pointCount > 1) {
                    val strokePath = Path()
                    val fillPath = Path()

                    val firstX = getX(0)
                    val firstY = getY(points[0].value)

                    strokePath.moveTo(firstX, firstY)
                    fillPath.moveTo(firstX, height)
                    fillPath.lineTo(firstX, firstY)

                    for (i in 0 until pointCount - 1) {
                        val p0X = getX(i)
                        val p0Y = getY(points[i].value)
                        val p1X = getX(i + 1)
                        val p1Y = getY(points[i + 1].value)

                        // Smooth Cubic Bezier Control Points
                        val cx = (p0X + p1X) / 2f
                        strokePath.cubicTo(cx, p0Y, cx, p1Y, p1X, p1Y)
                        fillPath.cubicTo(cx, p0Y, cx, p1Y, p1X, p1Y)
                    }

                    fillPath.lineTo(getX(pointCount - 1), height)
                    fillPath.close()

                    // Draw Gradient Fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.35f),
                                lineColor.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw Line Stroke
                    drawPath(
                        path = strokePath,
                        color = lineColor,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                } else if (pointCount == 1) {
                    val cx = width / 2f
                    val cy = getY(points[0].value)
                    drawCircle(color = lineColor, radius = 6.dp.toPx(), center = Offset(cx, cy))
                }

                // Draw Scrubber Indicator
                val activeIndex = selectedIndex
                if (activeIndex != null && activeIndex in points.indices) {
                    val sx = getX(activeIndex)
                    val sy = getY(points[activeIndex].value)

                    // Vertical Scrubber Line
                    drawLine(
                        color = colors.textPrimary.copy(alpha = 0.7f),
                        start = Offset(sx, 0f),
                        end = Offset(sx, height),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    // Point Halo & Outer Ring
                    drawCircle(
                        color = lineColor.copy(alpha = 0.25f),
                        radius = 10.dp.toPx(),
                        center = Offset(sx, sy)
                    )
                    drawCircle(
                        color = colors.surfaceElevated,
                        radius = 6.dp.toPx(),
                        center = Offset(sx, sy)
                    )
                    drawCircle(
                        color = lineColor,
                        radius = 4.dp.toPx(),
                        center = Offset(sx, sy)
                    )
                }
            }
        }
    }
}
