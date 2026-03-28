package com.uzazi.app.domain.models

import java.util.Date

data class CheckIn(
    val id: String,
    val userId: String,
    val date: Date,
    val moodEmoji: String,
    val physicalSymptoms: List<String>,
    val sleepHours: Int,
    val hydrationGlasses: Int,
    val notes: String?
)
