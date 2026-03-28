package com.uzazi.app.feature.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.DeepPlum
import com.uzazi.app.ui.theme.PetalLight
import com.uzazi.app.ui.theme.WarmAmber
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GardenCanvas(petalCount: Int, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "bloom")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    val animatedScales = remember { List(30) { Animatable(0f) } }

    LaunchedEffect(petalCount) {
        for (i in 0 until 30) {
            if (i < petalCount) {
                animatedScales[i].animateTo(1f, tween(durationMillis = 600, delayMillis = i * 40))
            } else {
                animatedScales[i].snapTo(0f)
            }
        }
    }

    Canvas(modifier = modifier.size(280.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        // 1. Draw organic leaves/stem background
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFE8F5E9), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = size.width / 2
            ),
            radius = size.width / 2
        )

        // 2. Petal Distribution (concentric layers like a Rose/Dahlia)
        val layers = listOf(
            Layer(count = 6, radiusRatio = 0.18f, petalSize = 45f),
            Layer(count = 10, radiusRatio = 0.32f, petalSize = 55f),
            Layer(count = 14, radiusRatio = 0.46f, petalSize = 65f)
        )
        
        var globalPetalIdx = 0
        layers.forEachIndexed { layerIdx, layer ->
            for (i in 0 until layer.count) {
                val angle = (2 * Math.PI * i / layer.count).toFloat() + (layerIdx * 0.5f) // Stagger angles
                val px = centerX + (size.width * layer.radiusRatio) * cos(angle)
                val py = centerY + (size.width * layer.radiusRatio) * sin(angle)
                
                val isEarned = globalPetalIdx < petalCount
                val scaleValue = if (isEarned) animatedScales[globalPetalIdx].value else 0.8f
                
                rotate(degrees = Math.toDegrees(angle.toDouble()).toFloat() + 90f, pivot = Offset(px, py)) {
                    scale(scaleValue * pulseScale, pivot = Offset(px, py)) {
                        drawDetailedPetal(
                            px, py, layer.petalSize,
                            color = if (isEarned) BloomPink else PetalLight.copy(alpha = 0.3f),
                            outlineColor = if (isEarned) DeepPlum.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.2f)
                        )
                    }
                }
                globalPetalIdx++
            }
        }

        // 3. Glowing Center (The heart of the flower)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(WarmAmber, Color(0xFFFFD54F), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = 30.dp.toPx()
            ),
            radius = 25.dp.toPx() * pulseScale
        )
        
        // Center texture (stigma)
        for (i in 0 until 8) {
            val angle = (2 * Math.PI * i / 8).toFloat()
            val sx = centerX + 10.dp.toPx() * cos(angle)
            val sy = centerY + 10.dp.toPx() * sin(angle)
            drawCircle(color = DeepPlum.copy(alpha = 0.3f), radius = 2.dp.toPx(), center = Offset(sx, sy))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDetailedPetal(
    x: Float, y: Float, petalSize: Float,
    color: Color, outlineColor: Color
) {
    val path = Path().apply {
        moveTo(x, y)
        // More organic heart/teardrop shape
        cubicTo(x - petalSize, y - petalSize * 0.5f, x - petalSize * 0.5f, y - petalSize * 1.5f, x, y - petalSize * 1.5f)
        cubicTo(x + petalSize * 0.5f, y - petalSize * 1.5f, x + petalSize, y - petalSize * 0.5f, x, y)
        close()
    }
    
    drawPath(path, color = color)
    drawPath(path, color = outlineColor, style = Stroke(width = 1.5.dp.toPx()))
    
    // Add a delicate "vein" line in the center of the petal
    val veinPath = Path().apply {
        moveTo(x, y)
        lineTo(x, y - petalSize * 0.8f)
    }
    drawPath(veinPath, color = outlineColor.copy(alpha = 0.2f), style = Stroke(width = 1.dp.toPx()))
}

private data class Layer(val count: Int, val radiusRatio: Float, val petalSize: Float)
