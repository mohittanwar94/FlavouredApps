package com.qureka.skool.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

object ComposeUtils {
    fun Modifier.gradientBackground(colors: List<Color>, angle: Float) = this.then(
        Modifier.drawBehind {
            val angleRad = angle / 180f * PI
            val x = kotlin.math.cos(angleRad).toFloat()
            val y = kotlin.math.sin(angleRad).toFloat()
            val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
            val offset = center + Offset(x * radius, y * radius)
            val exactOffset = Offset(
                x = min(offset.x.coerceAtLeast(0f), size.width),
                y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(size.width, size.height) - exactOffset,
                    end = exactOffset
                ),
                cornerRadius = CornerRadius(25f),
                size = size
            )
        }
    )
}