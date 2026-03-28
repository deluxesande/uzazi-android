package com.uzazi.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.DeepPlum
import com.uzazi.app.ui.theme.SoftRose

@Composable
fun MamaBearAvatar(
    size: Dp = 100.dp,
    nightMode: Boolean = false
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (nightMode) {
            Canvas(modifier = Modifier.size(size).blur(10.dp)) {
                drawCircle(
                    color = SoftRose.copy(alpha = 0.3f),
                    radius = size.toPx() / 2f
                )
            }
        }
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size.width
            val center = Offset(canvasSize / 2f, canvasSize / 2f)
            
            // Ears
            drawCircle(
                color = BloomPink,
                radius = canvasSize * 0.15f,
                center = Offset(canvasSize * 0.25f, canvasSize * 0.25f)
            )
            drawCircle(
                color = BloomPink,
                radius = canvasSize * 0.15f,
                center = Offset(canvasSize * 0.75f, canvasSize * 0.25f)
            )
            
            // Body/Face
            drawCircle(
                color = BloomPink,
                radius = canvasSize * 0.4f,
                center = center
            )
            
            // Eyes
            drawCircle(
                color = DeepPlum,
                radius = canvasSize * 0.05f,
                center = Offset(canvasSize * 0.35f, canvasSize * 0.45f)
            )
            drawCircle(
                color = DeepPlum,
                radius = canvasSize * 0.05f,
                center = Offset(canvasSize * 0.65f, canvasSize * 0.45f)
            )
            
            // Nose
            drawOval(
                color = DeepPlum,
                topLeft = Offset(canvasSize * 0.45f, canvasSize * 0.55f),
                size = Size(canvasSize * 0.1f, canvasSize * 0.06f)
            )
            
            // Smile
            drawArc(
                color = DeepPlum,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(canvasSize * 0.4f, canvasSize * 0.6f),
                size = Size(canvasSize * 0.2f, canvasSize * 0.1f),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}
