package com.uzazi.app.feature.nightcompanion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.components.LoadingDots
import com.uzazi.app.ui.theme.NightSurface

@Composable
fun TypingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    NightSurface,
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 8.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            LoadingDots()
        }
    }
}
