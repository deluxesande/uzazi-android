package com.uzazi.app.feature.checkin

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.DeepPlum

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuestionCard(
    question: Question,
    selectedIndex: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    AnimatedContent(
        targetState = question,
        transitionSpec = {
            slideInHorizontally { it } with slideOutHorizontally { -it }
        }, label = "question_anim"
    ) { targetQuestion ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = targetQuestion.text,
                style = MaterialTheme.typography.titleLarge,
                color = DeepPlum
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            targetQuestion.options.forEachIndexed { index, option ->
                val isSelected = selectedIndex == index
                OutlinedButton(
                    onClick = { onAnswerSelected(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) BloomPink else Color.White,
                        contentColor = if (isSelected) Color.White else DeepPlum
                    )
                ) {
                    Text(text = option)
                }
            }
        }
    }
}
