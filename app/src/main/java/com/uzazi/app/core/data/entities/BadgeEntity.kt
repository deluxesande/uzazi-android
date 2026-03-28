package com.uzazi.app.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconResId: Int,
    val unlockedAt: Long? = null
)
