package com.uzazi.app.feature.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.Text
import kotlinx.coroutines.delay

private val flowerTypes = listOf("🌸", "🌺", "🌼", "💮", "🌷", "✿", "❀")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GardenCanvas(
    petalCount: Int,          // how many flowers are earned (0–30)
    totalSlots: Int = 30,     // total flower slots shown
    modifier: Modifier = Modifier
) {
    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateIn = true }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5), RoundedCornerShape(16.dp))
            .border(0.5.dp, Color(0xFFF4C0D1), RoundedCornerShape(16.dp))
            .padding(12.dp)
            .defaultMinSize(minHeight = 72.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(totalSlots) { index ->
                FlowerItem(
                    index = index,
                    earned = index < petalCount,
                    animateIn = animateIn
                )
            }
        }
    }
}

@Composable
private fun FlowerItem(
    index: Int,
    earned: Boolean,
    animateIn: Boolean
) {
    var visible by remember { mutableStateOf(!animateIn) }
    LaunchedEffect(earned) {
        if (earned && animateIn) {
            delay(index * 60L)
            visible = true
        }
    }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "petalScale"
    )

    Box(
        modifier = Modifier
            .size(36.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (earned) Color(0xFFFBEAF0) else Color(0xFFF4C0D1).copy(alpha = 0.2f)
            )
            .border(
                1.5.dp,
                if (earned) Color(0xFFD4537E) else Color(0xFFF4C0D1).copy(alpha = 0.3f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = flowerTypes[index % 7],
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(if (earned) 1f else 0.35f)
        )
    }
}
