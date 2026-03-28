package com.uzazi.app.feature.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.components.MamaBearAvatar
import com.uzazi.app.ui.theme.BloomPink
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDateScreen(onDateSelected: (Long) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = Calendar.getInstance()
                val today = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, -90)
                val ninetyDaysAgo = calendar.timeInMillis
                return utcTimeMillis in ninetyDaysAgo..today
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "When did your baby arrive?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        MamaBearAvatar(size = 100.dp)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            val dateText = datePickerState.selectedDateMillis?.let {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
            } ?: "Select Date"
            Text(text = dateText)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        datePickerState.selectedDateMillis?.let {
            val days = (System.currentTimeMillis() - it) / (1000 * 60 * 60 * 24)
            Text(text = "Day $days of your journey", color = BloomPink, fontWeight = FontWeight.Medium)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { datePickerState.selectedDateMillis?.let { onDateSelected(it) } },
            modifier = Modifier.fillMaxWidth(),
            enabled = datePickerState.selectedDateMillis != null,
            colors = ButtonDefaults.buttonColors(containerColor = BloomPink)
        ) {
            Text("Continue")
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
