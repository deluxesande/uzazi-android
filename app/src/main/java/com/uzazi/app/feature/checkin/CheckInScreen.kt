package com.uzazi.app.feature.checkin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uzazi.app.ui.theme.BloomPink

@Composable
fun CheckInScreen(
    onNavigateToResult: (String) -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.riskResult) {
        uiState.riskResult?.let {
            // In real app, serialize to JSON
            onNavigateToResult(it.level.name)
        }
    }

    Scaffold(
        topBar = {
            CheckInProgressBar(
                current = uiState.currentQuestionIndex + 1,
                total = viewModel.questions.size
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    QuestionCard(
                        question = viewModel.questions[uiState.currentQuestionIndex],
                        selectedIndex = uiState.answers[uiState.currentQuestionIndex],
                        onAnswerSelected = { viewModel.selectAnswer(uiState.currentQuestionIndex, it) }
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        enabled = uiState.answers.containsKey(uiState.currentQuestionIndex),
                        colors = ButtonDefaults.buttonColors(containerColor = BloomPink)
                    ) {
                        Text(if (uiState.currentQuestionIndex == viewModel.questions.size - 1) "Finish" else "Next")
                    }
                }
            }
        }
    }
}
