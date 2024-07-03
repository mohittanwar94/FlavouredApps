package com.qureka.skool.stopwatch

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qureka.skool.theme.Color_6c31d2
import com.qureka.skool.theme.Color_fedc77
import com.qureka.skool.theme.DetailScreenTypography
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StopWatchCanvas() {
    val timerViewModel: StopWatchViewModel = viewModel()

    val grayColor = Color.Gray.copy(alpha = 0.5F)
    val radius = 300F
    val offset = Offset(0F, 0F)

    val duration = timerViewModel.duration.observeAsState().value!!

    val watchFacePaint = Paint().apply {
        color = Color_fedc77
        style = PaintingStyle.Stroke
        strokeWidth = 8F
        strokeCap = StrokeCap.Round
    }
    val clockTickPaint = Paint().apply {
        color = Color_fedc77
        style = PaintingStyle.Stroke
        strokeWidth = 5.5F
        strokeCap = StrokeCap.Round
    }
    val clockNumbersPaint = Paint().asFrameworkPaint().apply {
        // this.color = Color_fedc77
        this.textSize = 80F
        this.strokeWidth = 1F
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    val activeColor = Color_6c31d2
    val strokeWidth = 7.dp

    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(size = 300.dp)
        ) {
            drawContext.canvas.save()
            drawContext.canvas.translate(size.width / 2, size.height / 2)

            val scale = 1.5F
            val faceSize = Size(size.width / scale, size.height / scale)
            val arcRadius = faceSize.width / 2

            // draw face
//            drawWatchFace(drawContext, radius = faceSize.width / 2, paint = watchFacePaint)
            drawWatchFace(center = offset, radius = faceSize.width / 2, color = Color_fedc77)
            // draw ticks
            drawWatchTicks(
                drawContext,
                radius = radius,
                paint = clockTickPaint,
                offset = offset
            )

            val angle = duration.inWholeSeconds * 6F

            drawArc(
                color = activeColor,
                startAngle = -90F,
                sweepAngle = angle,
                useCenter = false,
                size = faceSize,
                topLeft = Offset(
                    -(arcRadius),
                    -(arcRadius)
                ),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)

            )

            val center = Offset(0F, 0F)
            val beta = (angle - 90) * (PI / 180f).toFloat()
            val r = faceSize.width / 2f
            val a = cos(beta) * r
            val b = sin(beta) * r

            // draw the circular pointer/ cap
            drawPoints(
                listOf(Offset(center.x + a, center.y + b)),
                pointMode = PointMode.Points,
                color = activeColor,
                strokeWidth = (strokeWidth * 3f).toPx(),
                cap = StrokeCap.Round // make the pointer round
            )

            drawContext.canvas.restore()
        }

        val text = duration.toComponents { hours, minutes, seconds, _ ->
            return@toComponents formatTime(
                hours = hours.toInt().pad(),
                minutes = minutes.pad(),
                seconds = seconds.pad()
            )
        }

        Text(
            text,
            modifier = Modifier.align(Alignment.Center),
            style = DetailScreenTypography.labelLarge
        )
    }
}

private fun DrawScope.drawWatchFace(radius: Float, center: Offset, color: Color) {
    drawCircle(radius = radius, center = center, color = color, style = Stroke(width = 8F))
}

private fun drawWatchFace(drawContext: DrawContext, radius: Float, paint: Paint) {
    drawContext.canvas.drawCircle(
        center = Offset(0F, 0F),
        radius = radius,
        paint = paint
    )
}

private fun drawWatchTicks(
    drawContext: DrawContext,
    radius: Float,
    offset: Offset,
    paint: Paint
) {
    val angle = 6F
    val p1 = radius - 30

    drawContext.canvas.save()

    for (i in 1..60) {
        val path = Path()

        var delta = 12

        if (i % 5 == 0) {
            delta = 25
        }

        drawContext.canvas.rotate(angle, offset.x, offset.y)
        path.moveTo(p1, 0F)
        path.lineTo(p1 - delta, 0F)
        drawContext.canvas.drawPath(path, paint)
        path.close()
    }

    drawContext.canvas.restore()
}
