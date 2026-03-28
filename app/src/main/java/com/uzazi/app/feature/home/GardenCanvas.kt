package com.uzazi.app.feature.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.DeepPlum
import com.uzazi.app.ui.theme.PetalLight
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GardenCanvas(petalCount: Int, modifier: Modifier = Modifier) {
    val animatedScales = remember { List(30) { Animatable(0f) } }

    LaunchedEffect(petalCount) {
        for (i in 0 until 30) {
            if (i < petalCount) {
                animatedScales[i].animateTo(1f, tween(durationMillis = 500, delayMillis = i * 50))
            } else {
                animatedScales[i].snapTo(0f)
            }
        }
    }

    Canvas(modifier = modifier.size(260.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        val rings = listOf(6, 10, 14)
        val radii = listOf(size.width * 0.15f, size.width * 0.28f, size.width * 0.42f)
        
        var petalIdx = 0
        rings.forEachIndexed { ringIdx, count ->
            val radius = radii[ringIdx]
            for (i in 0 until count) {
                val angle = (2 * Math.PI * i / count).toFloat()
                val px = centerX + radius * cos(angle)
                val py = centerY + radius * sin(angle)
                
                val scaleValue = if (petalIdx < petalCount) animatedScales[petalIdx].value else 1f
                val isEarned = petalIdx < petalCount
                
                scale(scaleValue, pivot = androidx.compose.ui.geometry.Offset(px, py)) {
                    drawPetal(
                        px, py, angle + (Math.PI / 2).toFloat(),
                        fillColor = if (isEarned) BloomPink else PetalLight.copy(alpha = 0.25f),
                        strokeColor = if (isEarned) DeepPlum.copy(alpha = 0.3f) else DeepPlum.copy(alpha = 0.1f)
                    )
                }
                petalIdx++
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPetal(
    x: Float, y: Float, rotation: Float,
    fillColor: androidx.compose.ui.graphics.Color,
    strokeColor: androidx.compose.ui.graphics.Color
) {
    val petalPath = Path().apply {
        moveTo(0f, 0f)
        cubicTo(-10f, -20f, -15f, -40f, 0f, -50f)
        cubicTo(15f, -40f, 10f, -20f, 0f, 0f)
        close()
    }
    
    val matrix = androidx.compose.ui.graphics.Matrix()
    matrix.translate(x, y)
    matrix.rotateZ(Math.toDegrees(rotation.toDouble()).toFloat())
    
    val transformedPath = Path()
    transformedPath.addPath(petalPath)
    transformedPath.transform(matrix)
    
    drawPath(transformedPath, color = fillColor)
    drawPath(transformedPath, color = strokeColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()))
}
