package com.uzazi.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.DeepPlum

@Composable
fun PetalIcon(
    size: Dp = 24.dp,
    earned: Boolean = true,
    animated: Boolean = false
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        
        val petalPath = Path().apply {
            moveTo(w / 2, h)
            cubicTo(0f, h * 0.6f, 0f, h * 0.2f, w / 2, 0f)
            cubicTo(w, h * 0.2f, w, h * 0.6f, w / 2, h)
            close()
        }
        
        drawPath(
            path = petalPath,
            color = if (earned) BloomPink else BloomPink.copy(alpha = 0.2f)
        )
        
        drawPath(
            path = petalPath,
            color = DeepPlum.copy(alpha = 0.3f),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}
