package com.uzazi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.uzazi.app.domain.models.RiskLevel
import com.uzazi.app.ui.theme.AlertRed
import com.uzazi.app.ui.theme.MintGreen
import com.uzazi.app.ui.theme.WarmAmber

@Composable
fun RiskBadge(level: RiskLevel) {
    val color = when (level) {
        RiskLevel.LOW -> MintGreen
        RiskLevel.MEDIUM -> WarmAmber
        RiskLevel.HIGH -> AlertRed
        RiskLevel.UNKNOWN -> Color.Gray
    }
    
    Surface(
        color = color,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = level.name,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
