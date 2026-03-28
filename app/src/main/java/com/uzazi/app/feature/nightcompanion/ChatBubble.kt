package com.uzazi.app.feature.nightcompanion

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uzazi.app.core.utils.DateUtils
import com.uzazi.app.domain.models.ChatMessage
import com.uzazi.app.domain.models.MessageRole
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.NightSurface

@Composable
fun ChatBubble(
    message: ChatMessage,
    isStreaming: Boolean = false
) {
    val isUser = message.role == MessageRole.USER
    
    val backgroundColor = if (isUser) BloomPink else NightSurface
    val shape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 8.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 8.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor, shape)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(0.75f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f, fill = false)
                )
                
                if (isStreaming) {
                    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ), label = "cursor_alpha"
                    )
                    Text(
                        text = " █",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(alpha)
                    )
                }
            }
        }
        
        Text(
            text = DateUtils.formatTimestamp(message.timestamp.time, "hh:mm a"),
            color = Color.Gray,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp)
        )
    }
}
