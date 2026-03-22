package com.example.suiting.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A circular wave progress indicator that wraps around the watch face.
 * Supports both round and square watch screens.
 */
@Composable
fun WaveProgressIndicator(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0x33D8BAFA),
    progressColor: Color = Color(0xFFD8BAFA),
    glowColor: Color = Color(0x88B39DDB),
    strokeWidth: Float = 8f,
    waveAmplitude: Float = 4f,
    waveFrequency: Float = 3f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = (minOf(size.width, size.height) / 2f) - strokeWidth

        // Draw track (full circle, dim)
        drawCircularTrack(cx, cy, radius, trackColor, strokeWidth)

        // Draw wave progress arc
        if (progress > 0f) {
            drawWaveArc(
                cx = cx,
                cy = cy,
                radius = radius,
                progress = progress,
                progressColor = progressColor,
                glowColor = glowColor,
                strokeWidth = strokeWidth,
                wavePhase = wavePhase,
                waveAmplitude = waveAmplitude,
                waveFrequency = waveFrequency
            )
        }
    }
}

private fun DrawScope.drawCircularTrack(
    cx: Float,
    cy: Float,
    radius: Float,
    color: Color,
    strokeWidth: Float
) {
    drawCircle(
        color = color,
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawWaveArc(
    cx: Float,
    cy: Float,
    radius: Float,
    progress: Float,
    progressColor: Color,
    glowColor: Color,
    strokeWidth: Float,
    wavePhase: Float,
    waveAmplitude: Float,
    waveFrequency: Float
) {
    val sweepAngle = 360f * progress
    val startAngle = -90f // start from top

    // Number of points to approximate the arc
    val steps = (sweepAngle * 2).toInt().coerceAtLeast(4)
    val path = Path()

    for (i in 0..steps) {
        val fraction = i.toFloat() / steps
        val angleDeg = startAngle + sweepAngle * fraction
        val angleRad = Math.toRadians(angleDeg.toDouble()).toFloat()

        // Wave distortion along the radius
        val waveOffset = waveAmplitude * sin(waveFrequency * fraction * 2 * PI.toFloat() + wavePhase)
        val r = radius + waveOffset

        val x = cx + r * cos(angleRad)
        val y = cy + r * sin(angleRad)

        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    // Glow / shadow layer
    drawPath(
        path = path,
        color = glowColor,
        style = Stroke(width = strokeWidth + 6f, cap = StrokeCap.Round)
    )

    // Main progress path
    drawPath(
        path = path,
        brush = Brush.sweepGradient(
            colorStops = arrayOf(
                0f to progressColor.copy(alpha = 0.6f),
                progress to progressColor,
                1f to progressColor.copy(alpha = 0f)
            ),
            center = Offset(cx, cy)
        ),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Draw bright dot at current position
    val endAngleDeg = startAngle + sweepAngle
    val endAngleRad = Math.toRadians(endAngleDeg.toDouble()).toFloat()
    val dotX = cx + radius * cos(endAngleRad)
    val dotY = cy + radius * sin(endAngleRad)

    drawCircle(
        color = Color.White,
        radius = strokeWidth / 2f + 1f,
        center = Offset(dotX, dotY)
    )
    drawCircle(
        color = progressColor,
        radius = strokeWidth / 2f,
        center = Offset(dotX, dotY)
    )
}
