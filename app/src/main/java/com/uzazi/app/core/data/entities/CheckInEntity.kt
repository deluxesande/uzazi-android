package com.uzazi.app.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_ins")
data class CheckInEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val mood: Int,
    val symptoms: String, // Comma separated
    val connection: Int,
    val sleep: Int,
    val support: Int,
    val riskLevel: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)
